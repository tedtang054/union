package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.transport.ServerMessage;
import reactor.core.publisher.Mono;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 16:35
 */
public class CabinetDefaultCmdSender implements CabinetCmdSender {

    @Override
    public Mono<Boolean> onDetail(ServerMessage msg) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> onDisplay(ServerMessage msg) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> onForce(ServerMessage msg) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> onReboot(ServerMessage msg) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> onRent(ServerMessage msg) {
        return Mono.just(true);
    }

    @Override
    public Mono<Boolean> onVol(ServerMessage msg) {
        return Mono.just(true);
    }
}
