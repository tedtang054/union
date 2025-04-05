package com.github.tedtang054.union.transport.udp;

import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.ExceptionHandler;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportConstants;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyOutbound;
import reactor.netty.udp.UdpInbound;
import reactor.netty.udp.UdpOutbound;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @Author: dengJh
 * @Date: 2023/10/13 9:24
 */
@Slf4j
public class UdpHandlerAdapter implements BiFunction<UdpInbound, UdpOutbound, Mono<Void>> {

    private EnumMap<ClientProtocol, ProtocolHandler> protocolHandlers;

    private ClientSessionManager sessionManager;

    private ExceptionHandler exceptionHandler;

    public UdpHandlerAdapter(List<? extends ProtocolHandler> handlers, ExceptionHandler exceptionHandler,
                             ClientSessionManager sessionManager) {
        this.protocolHandlers = new EnumMap<>(ClientProtocol.class);
        for (ProtocolHandler protocolHandler : handlers) {
            this.protocolHandlers.put(protocolHandler.protocol(), protocolHandler);
        }
        this.exceptionHandler = exceptionHandler;
        this.sessionManager = sessionManager;
    }

    @Override
    public Mono<Void> apply(UdpInbound udpInbound, UdpOutbound udpOutbound) {
        return udpInbound.receiveObject()
                .flatMap(message -> {
                    ClientMessage clientMessage = (ClientMessage) message;
                    var objectFlux = sessionManager.registerEmitter(TransportConstants.UDP_SERVER);
                    if (null == objectFlux) {
                        return Flux.merge(Mono.empty(), protocolHandlers.get(clientMessage.getProtocol()).handle(Mono.just(clientMessage)));
                    }
                    NettyOutbound outbound = udpOutbound.sendObject(objectFlux);
                    return Flux.merge(outbound, protocolHandlers.get(clientMessage.getProtocol()).handle(Mono.just(clientMessage)));
                })
                .onErrorContinue(exceptionHandler)
                .doOnComplete(() -> log.info("UdpHandlerAdapter complete"))
                .then();
    }

}
