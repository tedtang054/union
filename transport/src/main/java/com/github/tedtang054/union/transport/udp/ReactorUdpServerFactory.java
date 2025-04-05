package com.github.tedtang054.union.transport.udp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;


/**
 * @Author: dengJh
 * @Date: 2023/10/19 11:02
 */
public class ReactorUdpServerFactory implements FactoryBean<ReactorUdpServer>, DisposableBean {

    private TransportProperties properties;

    private ReactorUdpServer udpServer;

    private UdpHandlerAdapter handlerAdapter;

    private CompositeDecodeHandler decoderHandler;

    private ClientSessionManager sessionManager;

    public ReactorUdpServerFactory(TransportProperties properties, UdpHandlerAdapter handlerAdapter,
                                   CompositeDecodeHandler decoderHandler, ClientSessionManager sessionManager) {
        this.properties = properties;
        this.handlerAdapter = handlerAdapter;
        this.decoderHandler = decoderHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public ReactorUdpServer getObject() throws Exception {
        return udpServer = new ReactorUdpServer(properties, handlerAdapter, sessionManager, decoderHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return ReactorUdpServer.class;
    }

    @Override
    public void destroy() throws Exception {
        udpServer.stop();
    }
}
