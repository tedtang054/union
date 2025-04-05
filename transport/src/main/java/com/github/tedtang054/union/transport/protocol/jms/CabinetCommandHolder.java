package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.transport.constant.CabinetCmdEnum;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.msg.client.cabinet.ClientPayload;
import com.github.tedtang054.union.transport.msg.server.cabinet.ServerPayload;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.TimeUnit;

/**
 * @Author: dengJh
 * @Date: 2024/04/17 17:23
 */
@Slf4j
public class CabinetCommandHolder {

    private static final ConcurrentHashMap<CmdMessageKey, CmdMessageValue> CMD_HOLDER = new ConcurrentHashMap<>();

    static {
        var thread = new Thread("command cleanup") {
            @Override
            public void run() {
                cleanup();
            }
        };
        thread.setDaemon(true);
        thread.start();
    }

    private static void cleanup() {
        try {
            while (true) {
                TimeUnit.SECONDS.sleep(5);
                CMD_HOLDER.forEach((key, value) -> {
                    if (value.expired()) {
                        log.debug("clean up expired command {}", value.payload);
                        CMD_HOLDER.remove(key);
                    }
                });
            }
        } catch (InterruptedException e) {
            log.error("command cleanup error : ", e);
        }
    }

    public static void registerCommand(InetSocketAddress client, ServerPayload<?> payload) {
        var key = CmdMessageKey.builder()
                .msgId(payload.getMsg())
                .cmd(CabinetCmdEnum.toCmdEnum(payload.getCmd()))
                .client(client).build();
        var value = CmdMessageValue.builder()
                .cmdTime((int) (System.currentTimeMillis() / 1000))
                .payload(payload)
                .build();
        CMD_HOLDER.put(key, value);
    }

    public static CmdMessageValue registeredResponse(ClientMessage msg, Integer msgAck, boolean delete) {
        var key = CmdMessageKey.builder()
                .msgId(msgAck)
                .cmd(((ClientPayload<?>) msg.getMessage()).getCmd())
                .client(msg.getPeer())
                .build();
        if (delete) {
            return CMD_HOLDER.remove(key);
        }
        return CMD_HOLDER.get(key);
    }

    @Data
    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CmdMessageKey {

        private InetSocketAddress client;

        private CabinetCmdEnum cmd;

        private Integer msgId;

    }

    @Data
    @Builder
    @AllArgsConstructor
    @NoArgsConstructor
    public static class CmdMessageValue {

        private Integer cmdTime;

        ServerPayload<?> payload;

        public Boolean expired() {
            return System.currentTimeMillis() / 1000 - cmdTime > 60;
        }

    }

}