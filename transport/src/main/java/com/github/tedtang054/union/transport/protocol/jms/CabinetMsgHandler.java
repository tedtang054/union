package com.github.tedtang054.union.transport.protocol.jms;


import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerPayload;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerVo;
import reactor.core.publisher.Mono;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 16:33
 * 充电柜客户端报文处理
 */
public interface CabinetMsgHandler {

    /**
     * 登录上报
     * @param msg
     * @return
     */
    Mono<List<ServerPayload<? extends ServerVo>>> login(ClientMessage msg);

    /**
     * 库存详情查询下发
     * @param msg
     * @return
     */
    Mono<List<ServerPayload<? extends ServerVo>>> detail(ClientMessage msg);

    /**
     * 库存详情上报
     * @param msg
     * @return
     */
    Mono<List<ServerPayload<? extends ServerVo>>> detailUp(ClientMessage msg);

    /**
     * 心跳上报
     * @param msg
     * @return
     */
    Mono<List<ServerPayload<? extends ServerVo>>> heartbeat(ClientMessage msg);

}
