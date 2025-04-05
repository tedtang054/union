package com.github.tedtang054.union.transport.tcp;

import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.ExceptionHandler;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import lombok.extern.slf4j.Slf4j;
import org.reactivestreams.Publisher;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.NettyInbound;
import reactor.netty.NettyOutbound;

import java.util.EnumMap;
import java.util.List;
import java.util.function.BiFunction;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 17:06
 */
@Slf4j
public class TcpHandlerAdapter implements BiFunction<NettyInbound, NettyOutbound, Publisher<Void>> {

    private final EnumMap<ClientProtocol, ProtocolHandler> protocolHandlers;

    private ClientSessionManager sessionManager;

    private ExceptionHandler exceptionHandler;

    public TcpHandlerAdapter(List<? extends ProtocolHandler> protocolHandlers, ClientSessionManager sessionManager,
                             ExceptionHandler exceptionHandler) {
        this.protocolHandlers = new EnumMap<>(ClientProtocol.class);
        for (ProtocolHandler protocolHandler : protocolHandlers) {
            this.protocolHandlers.put(protocolHandler.protocol(), protocolHandler);
        }
        this.sessionManager = sessionManager;
        this.exceptionHandler = exceptionHandler;
    }

    @Override
    public Mono<Void> apply(NettyInbound nettyInbound, NettyOutbound nettyOutbound) {
        return nettyInbound.receiveObject().flatMap(message -> {
            ClientMessage msg = (ClientMessage) message;
            var objectFlux = sessionManager.registerEmitter(msg.getPeer());
            if (null == objectFlux) {
                return Flux.from(protocolHandlers.get(msg.getProtocol()).handle(Mono.just(msg)));
            }
            var outbound = nettyOutbound.sendObject(objectFlux);
            return Flux.merge(outbound, protocolHandlers.get(msg.getProtocol()).handle(Mono.just(msg)));
        })
        .onErrorContinue((e, element) -> {
            log.error("protocol adapter handle error : ", e);
        })
        .doOnComplete(() -> log.info("ProtocolAdapter complete"))
        .then();
    }

}

