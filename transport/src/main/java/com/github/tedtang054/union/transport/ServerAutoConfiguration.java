package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;
import com.github.tedtang054.union.transport.channel.PipelineInitializer;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.properties.CabinetProperties;
import com.github.tedtang054.union.transport.tcp.ReactorTcpServerFactory;
import com.github.tedtang054.union.transport.tcp.TcpHandlerAdapter;
import com.github.tedtang054.union.transport.udp.ReactorUdpServerFactory;
import com.github.tedtang054.union.transport.udp.UdpHandlerAdapter;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.beans.factory.support.DefaultListableBeanFactory;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;

import javax.annotation.Resource;

import java.util.List;
import java.util.stream.Collectors;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 16:04
 */
@Configuration
@EnableConfigurationProperties({TransportProperties.class, CabinetProperties.class})
public class ServerAutoConfiguration {

    @Bean
    public ClientSessionManager sessionManager() {
        return new LocalClientSessionManager();
    }

    @Bean
    public ExceptionHandler exceptionHandler() {
        return new ExceptionHandler();
    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(name = "custom.transport.udp.enable", havingValue = "true")
    public static class ReactorUdpServerConfig {

        @Resource
        DefaultListableBeanFactory beanFactory;

        @Bean
        public ReactorUdpServerFactory reactorUdpServerFactory(TransportProperties properties, UdpHandlerAdapter adapter,
                                                               @Qualifier("udpDecoderHandler") CompositeDecodeHandler decoderHandler,
                                                               ClientSessionManager sessionManager) {
            ReactorUdpServerFactory udpServerFactory = new ReactorUdpServerFactory(properties, adapter, decoderHandler, sessionManager);
            beanFactory.registerSingleton("udpServer", udpServerFactory.getObject());
            return udpServerFactory;
        }

        @Bean("udpDecoderHandler")
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public CompositeDecodeHandler compositeDecoderHandler(TransportProperties properties) {
            CompositeDecodeHandler decoderHandler = new CompositeDecodeHandler();
            List<Class<? extends PipelineInitializer>> initializerClasses = properties.getUdp().getProtocols().stream()
                    .map(protocol -> ClientProtocol.valueOf(protocol.toUpperCase()).getProtocolClasses().getInitializerClass())
                    .collect(Collectors.toList());
            decoderHandler.setInitializerClasses(initializerClasses);
            return decoderHandler;
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public UdpHandlerAdapter udpHandlerAdapter(DefaultListableBeanFactory beanFactory, TransportProperties properties,
                                                ClientSessionManager sessionManager,
                                                ExceptionHandler exceptionHandler) {
            var handlers = protocolHandlers(properties.getUdp().getProtocols(), beanFactory);
            return new UdpHandlerAdapter(handlers, exceptionHandler, sessionManager);
        }
    }

    @Configuration
    @Order(Ordered.HIGHEST_PRECEDENCE)
    @ConditionalOnProperty(name = "custom.transport.tcp.enable", havingValue = "true")
    public static class ReactorTcpServerConfig {

        @Resource
        DefaultListableBeanFactory beanFactory;

        @Bean
        public ReactorTcpServerFactory reactorTcpServerFactory(TransportProperties properties, TcpHandlerAdapter handlerAdapter,
                                                               ClientSessionManager sessionManager,
                                                               @Qualifier("tcpDecoderHandler") CompositeDecodeHandler decoderHandler) {
            ReactorTcpServerFactory tcpServerFactory = new ReactorTcpServerFactory(properties, handlerAdapter, sessionManager, decoderHandler);
            beanFactory.registerSingleton("tcpServer", tcpServerFactory.getObject());
            return tcpServerFactory;
        }

        @Bean("tcpDecoderHandler")
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public CompositeDecodeHandler compositeDecoderHandler(TransportProperties properties) {
            CompositeDecodeHandler decoderHandler = new CompositeDecodeHandler();
            List<Class<? extends PipelineInitializer>> initializerClasses = properties.getTcp().getProtocols().stream()
                    .map(protocol -> ClientProtocol.valueOf(protocol.toUpperCase()).getProtocolClasses().getInitializerClass())
                    .collect(Collectors.toList());
            decoderHandler.setInitializerClasses(initializerClasses);
            return decoderHandler;
        }

        @Bean
        @Order(Ordered.HIGHEST_PRECEDENCE)
        public TcpHandlerAdapter tcpHandlerAdapter(DefaultListableBeanFactory beanFactory,
                                                TransportProperties properties, ClientSessionManager sessionManager,
                                                ExceptionHandler exceptionHandler) {
            var handlers = protocolHandlers(properties.getTcp().getProtocols(), beanFactory);
            return new TcpHandlerAdapter(handlers, sessionManager, exceptionHandler);
        }

    }

    private static List<ProtocolHandler> protocolHandlers(List<String> protocols, DefaultListableBeanFactory beanFactory) {
        return protocols.stream()
                .map(protocol -> {
                    ClientProtocol clientProtocol = ClientProtocol.valueOf(protocol.toUpperCase());
                    ClientProtocol.ProtocolClasses protocolClasses = clientProtocol.getProtocolClasses();
                    for (Class<?> dependencyClass : protocolClasses.getDependencyClasses()) {
                        if (beanFactory.getBeansOfType(dependencyClass).isEmpty()) {
                            beanFactory.registerSingleton(dependencyClass.getName(), beanFactory.createBean(dependencyClass));
                        }
                    }
                    var protocolHandlers = beanFactory.getBeansOfType(protocolClasses.getProtocolHandlerClass());
                    ProtocolHandler handler;
                    if (beanFactory.getBeansOfType(protocolClasses.getProtocolHandlerClass()).isEmpty()) {
                        handler = beanFactory.createBean(protocolClasses.getProtocolHandlerClass());
                        beanFactory.registerSingleton(protocolClasses.getProtocolHandlerClass().getName(), handler);
                    } else {
                        handler = protocolHandlers.values().iterator().next();
                    }
                    return handler;
                })
                .collect(Collectors.toList());
    }

}
