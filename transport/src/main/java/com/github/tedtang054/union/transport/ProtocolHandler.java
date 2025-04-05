package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import reactor.core.publisher.Mono;

/**
 * @Author: dengJh
 * @Date: 2023/10/12 13:46
 */
public interface ProtocolHandler {

    Mono<Void> handle(Mono<ClientMessage> messageMono);

    default Mono<Boolean> send(Mono<ServerMessage> commandMono) {
        return Mono.just(true);
    }

    ClientProtocol protocol();

}
