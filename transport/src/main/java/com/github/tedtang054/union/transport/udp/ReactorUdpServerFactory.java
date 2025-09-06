package com.github.tedtang054.union.transport.udp;

import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.TransportProperties;
import com.github.tedtang054.union.transport.channel.CompositeDecodeHandler;


/**
 * @Author: dengJh
 * @Date: 2023/10/19 11:02
 */
public class ReactorUdpServerFactory {

    private TransportProperties properties;

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

    public ReactorUdpServer getObject() {
        return new ReactorUdpServer(properties, handlerAdapter, sessionManager, decoderHandler);
    }

}
