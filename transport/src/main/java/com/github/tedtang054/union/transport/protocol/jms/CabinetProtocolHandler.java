package com.github.tedtang054.union.transport.protocol.jms;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.TypeReference;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.ServerMessage;
import com.github.tedtang054.union.transport.constant.CabinetCmdEnum;
import com.github.tedtang054.union.transport.constant.CabinetErrorEnum;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.msg.client.cabinet.ClientPayload;
import com.github.tedtang054.union.transport.msg.client.cabinet.ClientVo;
import com.github.tedtang054.union.transport.msg.client.cabinet.DetailResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.DetailUpRequest;
import com.github.tedtang054.union.transport.msg.client.cabinet.DisplayResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.ForceResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.HeartRequest;
import com.github.tedtang054.union.transport.msg.client.cabinet.LoginRequest;
import com.github.tedtang054.union.transport.msg.client.cabinet.RebootResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.RentResponse;
import com.github.tedtang054.union.transport.msg.client.cabinet.ReturnRequest;
import com.github.tedtang054.union.transport.msg.client.cabinet.VolResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.LoginResponse;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerPayload;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerVo;
import com.github.tedtang054.union.transport.utils.MsgIdGeneratorUtils;
import io.netty.buffer.ByteBuf;
import io.netty.util.CharsetUtil;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.StringUtils;
import reactor.core.publisher.Mono;
import reactor.util.function.Tuple2;

import java.net.InetSocketAddress;
import java.util.EnumMap;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicReference;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 17:07
 */
@Slf4j
public class CabinetProtocolHandler implements ProtocolHandler {

    private CabinetMsgHandler msgHandler;

    private CabinetCmdSender cmdSender;

    private ClientSessionManager sessionManager;

    private static final EnumMap<CabinetCmdEnum, Function<ClientMessage, Mono<List<ServerPayload<? extends ServerVo>>>>> HANDLER_METHODS = new EnumMap<>(CabinetCmdEnum.class);

    private static final EnumMap<CabinetCmdEnum, Function<ServerMessage, Mono<Boolean>>> SENDER_METHODS = new EnumMap<>(CabinetCmdEnum.class);

    private static final EnumMap<CabinetCmdEnum, Class<? extends ClientVo>> CONVERTERS = new EnumMap<>(CabinetCmdEnum.class);

    public CabinetProtocolHandler(CabinetMsgHandler msgHandler, CabinetCmdSender cmdSender, ClientSessionManager sessionManager) {
        this.msgHandler = msgHandler;
        this.cmdSender = cmdSender;
        this.sessionManager = sessionManager;
        initHandler();
        initSender();
        initConverters();
    }

    @Override
    public Mono<Void> handle(Mono<ClientMessage> msg) {
        AtomicReference<ClientMessage> clientMsg = new AtomicReference<>();
        return msg.flatMap(req -> {
            InetSocketAddress peer = req.getPeer();
            ByteBuf byteBuf = req.getByteMessage();
            clientMsg.set(req);
            ClientPayload<? extends ClientVo> request;
            try {
                String message = byteBuf.toString(CharsetUtil.UTF_8);
                if (!StringUtils.hasText(message)) {
                    return Mono.empty();
                }
                request = decode(message);
                if (null == request) {
                    log.error("cabinet decode message error, client : {}, message: {}", peer, message);
                    return Mono.empty();
                }
            } finally {
                byteBuf.release();
            }
            req.setMessage(request);
            return sessionManager.checkLogin(peer).flatMap(login -> {
                CabinetCmdEnum cmd = request.getCmd();
                if (!login && !CabinetCmdEnum.LOGIN.equals(cmd)) {
                    var loginResp = ServerPayload.builder()
                            .cmd(CabinetCmdEnum.LOGIN.getCmd())
                            .aims((byte) 0)
                            .msg(MsgIdGeneratorUtils.getMsgId())
                            .data(LoginResponse.builder().r(CabinetErrorEnum.FAILURE.getCode()).heart(30).build())
                            .build();
                    return Mono.zip(Mono.just(List.of(loginResp)), Mono.just(false));
                }
                var update = sessionManager.updateHeartbeat(req.getPeer());
                var function = HANDLER_METHODS.get(cmd);
                return Mono.zip(function.apply(req), update);
            });
        })
        .doOnNext(tuple -> {
            var responses = ((List<ServerPayload<? extends ServerVo>>) tuple.getT1());
            ClientMessage client = clientMsg.get();
            var serverMessages = responses.stream().map(response -> {
                var serverResp = response.getData();
                boolean loginFailed = serverResp instanceof LoginResponse
                        && !Objects.equals(((LoginResponse) serverResp).getR(), CabinetErrorEnum.SUCCESS.getCode());
                return ServerMessage.builder()
                        .peer(client.getPeer())
                        .message(response)
                        .transportType(client.getTransportType())
                        .loginFailed(loginFailed)
                        .empty(response.getEmpty())
                        .protocol(client.getProtocol()).build();
            }).collect(Collectors.toList());
            sessionManager.fireMsg(client.getPeer(), serverMessages);
        })
        .onErrorContinue((e, element) -> {
            log.error("protocol handle error, element : {}", element, e);
        })
        .then();
    }

    @Override
    public Mono<Boolean> send(Mono<ServerMessage> command) {
        return command.flatMap(cmd -> {
            var message = (ServerPayload) cmd.getMessage();
            message.setMsg(MsgIdGeneratorUtils.getMsgId());
            var client = sessionManager.getRemoteAddress(cmd.getIdentity());
            if (null == client) {
                return Mono.zip(Mono.just(false), Mono.just(false));
            }
            return sessionManager.getSession(client).flatMap(session -> {
                cmd.setTransportType(session.getTransportType());
                var fire = sessionManager.fireMsg(cmd.getIdentity(), List.of(cmd));
                CabinetCommandHolder.registerCommand(client, message);
                var cmdEnum = CabinetCmdEnum.toCmdEnum(message.getCmd());
                var handled = SENDER_METHODS.get(cmdEnum).apply(cmd);
                return Mono.zip(Mono.just(fire), handled);
            });
        }).map(Tuple2::getT1);
    }

    @Override
    public ClientProtocol protocol() {
        return ClientProtocol.CABINET;
    }

    private static <T extends ClientVo> ClientPayload<T> decode(String msg) {
        int cmdIndex = msg.indexOf("\"cmd\"");
        if (cmdIndex != -1) {
            String cmd = msg.substring(cmdIndex + 7, msg.indexOf(",", cmdIndex) - 1);
            CabinetCmdEnum cmdEnum = CabinetCmdEnum.toCmdEnum(cmd);
            if (null != cmdEnum) {
                Class<? extends ClientVo> msgClass = CONVERTERS.get(cmdEnum);
                if (null != msgClass) {
                    return JSON.parseObject(msg, new TypeReference<>(msgClass) {});
                }
                return null;
            }
        }
        return null;
    }

    private void initHandler() {
        HANDLER_METHODS.put(CabinetCmdEnum.DETAIL, msgHandler::detail);
        HANDLER_METHODS.put(CabinetCmdEnum.DETAIL_UP, msgHandler::detailUp);
        HANDLER_METHODS.put(CabinetCmdEnum.HEART, msgHandler::heartbeat);
        HANDLER_METHODS.put(CabinetCmdEnum.LOGIN, msgHandler::login);
    }

    private void initSender() {
        SENDER_METHODS.put(CabinetCmdEnum.DETAIL, cmdSender::onDetail);
        SENDER_METHODS.put(CabinetCmdEnum.DISPLAY, cmdSender::onDisplay);
        SENDER_METHODS.put(CabinetCmdEnum.FORCE, cmdSender::onForce);
        SENDER_METHODS.put(CabinetCmdEnum.REBOOT, cmdSender::onReboot);
        SENDER_METHODS.put(CabinetCmdEnum.RENT, cmdSender::onRent);
    }

    private void initConverters() {
        CONVERTERS.put(CabinetCmdEnum.LOGIN, LoginRequest.class);
        CONVERTERS.put(CabinetCmdEnum.DETAIL, DetailResponse.class);
        CONVERTERS.put(CabinetCmdEnum.DETAIL_UP, DetailUpRequest.class);
        CONVERTERS.put(CabinetCmdEnum.DISPLAY, DisplayResponse.class);
        CONVERTERS.put(CabinetCmdEnum.FORCE, ForceResponse.class);
        CONVERTERS.put(CabinetCmdEnum.HEART, HeartRequest.class);
        CONVERTERS.put(CabinetCmdEnum.REBOOT, RebootResponse.class);
        CONVERTERS.put(CabinetCmdEnum.RENT, RentResponse.class);
        CONVERTERS.put(CabinetCmdEnum.RETURN, ReturnRequest.class);
        CONVERTERS.put(CabinetCmdEnum.VOL, VolResponse.class);
    }

}
