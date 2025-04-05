package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.transport.ServerMessage;
import reactor.core.publisher.Mono;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 16:34
 * 命令下发
 */
public interface CabinetCmdSender {

    Mono<Boolean> onDetail(ServerMessage msg);

    Mono<Boolean> onDisplay(ServerMessage msg);

    Mono<Boolean> onForce(ServerMessage msg);

    Mono<Boolean> onReboot(ServerMessage msg);

    Mono<Boolean> onRent(ServerMessage msg);

    Mono<Boolean> onVol(ServerMessage msg);

}
