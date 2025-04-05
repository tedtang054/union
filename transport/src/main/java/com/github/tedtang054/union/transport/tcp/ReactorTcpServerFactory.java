package com.github.tedtang054.union.transport.tcp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;
import org.springframework.beans.factory.DisposableBean;
import org.springframework.beans.factory.FactoryBean;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 14:24
 */
public class ReactorTcpServerFactory implements FactoryBean<ReactorTcpServer>, DisposableBean {

    private TransportProperties properties;

    private ReactorTcpServer server;

    private TcpHandlerAdapter adapter;

    private CompositeDecodeHandler decoderHandler;

    private ClientSessionManager sessionManager;

    public ReactorTcpServerFactory(TransportProperties properties, TcpHandlerAdapter adapter,
                                   ClientSessionManager sessionManager, CompositeDecodeHandler decoderHandler) {
        this.properties = properties;
        this.adapter = adapter;
        this.sessionManager = sessionManager;
        this.decoderHandler = decoderHandler;
    }

    @Override
    public void destroy() throws Exception {
        server.stop();
    }

    @Override
    public ReactorTcpServer getObject() throws Exception {
        return server = new ReactorTcpServer(properties, adapter, sessionManager, decoderHandler);
    }

    @Override
    public Class<?> getObjectType() {
        return ReactorTcpServer.class;
    }
}
