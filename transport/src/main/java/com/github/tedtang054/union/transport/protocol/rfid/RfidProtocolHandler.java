package com.github.tedtang054.union.transport.protocol.rfid;

import com.github.tedtang054.union.common.event.UploadEvent;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.VerifyMsg;
import lombok.extern.slf4j.Slf4j;
import org.springframework.context.ApplicationEventPublisher;
import reactor.core.publisher.Mono;

import java.nio.charset.Charset;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Objects;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:44
 */
@Slf4j
public class RfidProtocolHandler implements ProtocolHandler {

    private ClientSessionManager sessionManager;

    private ApplicationEventPublisher eventPublisher;

    public RfidProtocolHandler(ClientSessionManager sessionManager,
                               ApplicationEventPublisher eventPublisher) {
        this.sessionManager = sessionManager;
        this.eventPublisher = eventPublisher;
    }

    @Override
    public Mono<Void> handle(Mono<ClientMessage> messageMono) {
        return messageMono.flatMap(clientMessage -> {
            var message = clientMessage.getByteMessage()
                    .toString(0, clientMessage.getByteMessage().readableBytes(), Charset.defaultCharset());
            clientMessage.getByteMessage().release();
            Integer stationId = Integer.parseInt(message.substring(1, 9), 16);
            // 固定北京时间
            ZonedDateTime now = ZonedDateTime.now(ZoneId.of("UTC+8"));
            var year = Integer.parseInt(message.substring(9, 11));
            var month = Integer.parseInt(message.substring(11, 13));
            var day = Integer.parseInt(message.substring(13, 15));
            var hour = Integer.parseInt(message.substring(15, 17));
            var minute = Integer.parseInt(message.substring(17, 19));
            var second = Integer.parseInt(message.substring(19, 21));
            var dataTime = now.withYear(2000 + year).withMonth(month).withDayOfMonth(day)
                    .withHour(hour).withMinute(minute).withSecond(second)
                    .toEpochSecond();
            Integer cardId = Integer.parseInt(message.substring(21, 31));
            VerifyMsg verifyMsg = new VerifyMsg(-1, stationId + "", clientMessage.getTransportType(), ClientProtocol.RFID_STATION);
            var registered = sessionManager.registerSession(clientMessage.getPeer(), verifyMsg);
            // 心跳包
            if (Objects.equals(cardId, 0)) {
                return Mono.empty();
            }
            String function = message.substring(31, 32);
            var addressNum = Short.parseShort(message.substring(32, 34), 16);
            UploadEvent.UploadDto uploadDto = UploadEvent.UploadDto.builder()
                    .stationId(stationId)
                    .cardId(cardId)
                    .dataTime((int) dataTime)
                    .lowPower(Character.isLowerCase(function.charAt(0)))
                    .addressNum(addressNum)
                    .function(function)
                    .build();
            var event = new UploadEvent();
            event.addEvent(uploadDto);
            eventPublisher.publishEvent(event);
            return registered;
        }).then();
    }


    public byte bccCheck(byte[] data, int size) {
        byte check = 0;
        for (int i = 0; i < size; i++) {
            check ^= data[i];
        }
        return check;
    }

    @Override
    public ClientProtocol protocol() {
        return ClientProtocol.RFID_STATION;
    }
}
