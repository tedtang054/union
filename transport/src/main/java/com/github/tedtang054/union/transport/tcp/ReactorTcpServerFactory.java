package com.github.tedtang054.union.transport.tcp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 14:24
 */
public class ReactorTcpServerFactory {

    private TransportProperties properties;

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

    public ReactorTcpServer getObject() {
        return new ReactorTcpServer(properties, adapter, sessionManager, decoderHandler);
    }

}
