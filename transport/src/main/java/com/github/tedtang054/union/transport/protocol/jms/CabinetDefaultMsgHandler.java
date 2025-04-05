package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.transport.constant.CabinetCmdEnum;
import com.github.tedtang054.union.transport.constant.CabinetErrorEnum;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.service.CabinetDataBaseService;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.VerifyMsg;
import com.github.tedtang054.union.transport.msg.client.cabinet.ClientPayload;
import com.github.tedtang054.union.transport.msg.client.cabinet.DetailResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.LoginRequest;
import com.github.tedtang054.union.transport.msg.server.cabinet.DetailRequest;
import com.github.tedtang054.union.transport.msg.server.cabinet.DetailUpResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.HeartResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.LoginResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.ReturnResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerPayload;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerVo;
import com.github.tedtang054.union.transport.utils.MsgIdGeneratorUtils;
import reactor.core.publisher.Mono;

import java.util.List;
import java.util.concurrent.atomic.AtomicReference;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 16:34
 */
public class CabinetDefaultMsgHandler implements CabinetMsgHandler {

    private ClientSessionManager sessionManager;

    private CabinetDataBaseService dataBaseService;

    public CabinetDefaultMsgHandler(ClientSessionManager sessionManager, CabinetDataBaseService dataBaseService) {
        this.sessionManager = sessionManager;
        this.dataBaseService = dataBaseService;
    }

    @Override
    public Mono<List<ServerPayload<? extends ServerVo>>> login(ClientMessage msg) {
        AtomicReference<ClientMessage> originMsg = new AtomicReference<>();
        return Mono.just(msg).flatMap(message -> {
            originMsg.set(message);
            var payload = (ClientPayload<LoginRequest>) message.getMessage();
            var data = payload.getData();
            return dataBaseService.login(payload.getSn(), data.getApnuser(), data.getApnpass());
        }).flatMap(login -> {
            var loginResp = ServerPayload.<LoginResponse>builder()
                    .cmd(CabinetCmdEnum.LOGIN.getCmd())
                    .aims((byte) 0)
                    .msg(MsgIdGeneratorUtils.getMsgId())
                    .data(LoginResponse.builder().r(CabinetErrorEnum.SUCCESS.getCode()).heart(30).build())
                    .build();
            if (!login) {
                loginResp.getData().setR(CabinetErrorEnum.FAILURE.getCode());
                return Mono.just(List.of(loginResp));
            }
            ClientMessage clientMessage = originMsg.get();
            var payload = (ClientPayload<LoginRequest>) originMsg.get().getMessage();
            VerifyMsg verifyMsg = VerifyMsg.builder()
                    .identity(payload.getSn())
                    .transportType(clientMessage.getTransportType())
                    .protocol(ClientProtocol.CABINET)
                    .build();
            return sessionManager.registerSession(clientMessage.getPeer(), verifyMsg)
                    .map(success -> {
                        if (success) {
                            var queryDetail = ServerPayload.<DetailRequest>builder().aims((byte) 0)
                                    .msg(MsgIdGeneratorUtils.getMsgId())
                                    .cmd(CabinetCmdEnum.DETAIL.getCmd())
                                    .data(DetailRequest.builder().n((short) 0).build()).build();
                            return List.of(loginResp, queryDetail);
                        }
                        loginResp.getData().setR(CabinetErrorEnum.FAILURE.getCode());
                        return List.of(loginResp);
                    });
        });
    }

    @Override
    public Mono<List<ServerPayload<? extends ServerVo>>> detail(ClientMessage msg) {
        return Mono.just(msg).flatMap(message -> {
            var payload = (ClientPayload<DetailResponse>) message.getMessage();
            var data = payload.getData();
            var serverPayload = ServerPayload.<ReturnResponse>builder()
                    .empty(true)
                    .build();
            var update = dataBaseService.updateDetail(payload.getSn(), payload.getAims(), data);
            return Mono.zip(update, Mono.just(serverPayload));
        }).map(tuple -> List.of(tuple.getT2()));
    }

    @Override
    public Mono<List<ServerPayload<? extends ServerVo>>> detailUp(ClientMessage msg) {
        return Mono.just(msg).flatMap(message -> {
            var payload = (ClientPayload<DetailResponse>) message.getMessage();
            var data = payload.getData();
            var resp = ServerPayload.builder()
                    .aims((byte) 0)
                    .cmd(CabinetCmdEnum.DETAIL_UP.getCmd())
                    .msgack(payload.getMsg()).data(DetailUpResponse.builder().r((byte) 0).build())
                    .build();
            var updateDetail = dataBaseService.updateDetail(payload.getSn(), payload.getAims(), data);
            return Mono.zip(updateDetail, Mono.just(resp));
        }).map(tuple ->List.of(tuple.getT2()));
    }

    @Override
    public Mono<List<ServerPayload<? extends ServerVo>>> heartbeat(ClientMessage msg) {
        return Mono.just(msg).flatMap(message ->
            sessionManager.updateHeartbeat(msg.getPeer())
        ).map(flag ->
            List.of(ServerPayload.builder()
                    .aims((byte) 0)
                    .cmd(CabinetCmdEnum.HEART.getCmd()).data(HeartResponse.builder().heart(30).build())
                    .build())
        );
    }

}
