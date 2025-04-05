package com.github.tedtang054.union.transport.protocol.jms;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.ClientSessionManager;
import com.github.tedtang054.union.transport.ProtocolHandler;
import com.github.tedtang054.union.transport.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import lombok.extern.slf4j.Slf4j;
import reactor.core.publisher.Mono;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:44
 */
@Slf4j
public class UpgradeProtocolHandler implements ProtocolHandler {

    private ClientSessionManager sessionManager;


    private UpgradeTaskQueue taskQueue;

    public UpgradeProtocolHandler(ClientSessionManager sessionManager,
                                  UpgradeTaskQueue taskQueue) {
        this.sessionManager = sessionManager;
        this.taskQueue = taskQueue;
    }

    @Override
    public Mono<Void> handle(Mono<ClientMessage> messageMono) {
        return messageMono.flatMap(message -> {
            byte[] data = ByteBufUtil.getBytes(message.getByteMessage(), 0, message.getByteMessage().readableBytes(), false);
            if (bccCheck(data, data.length - 1) != data[data.length - 1]) {
                log.error("message bcc check fail, message : {}, client : {}", ByteBufUtil.hexDump(data), message.getPeer());
            }
            ByteBuf byteBuf = message.getByteMessage();
            // mw 6字节BCD编码设备号
            int dataLength = byteBuf.getUnsignedShort(1);
            byte shift = (byte) (dataLength == 13 ? 2 : 0);

            byte[] identity = shift == 0 ? new byte[4] : ByteBufUtil.getBytes(message.getByteMessage(), 3, 6, false);
            short cmd = byteBuf.getUnsignedByte(0);
            // 升级命令字：0x87
            if (cmd != 0x87) {
                return Mono.empty();
            }
            String codeStr = "";
            if (shift > 0) {
                codeStr = bcdBytesToString(identity).substring(1);
            } else {
                byte code = 0;
                for (int i = 0; i < 4; i++) {
                    identity[i] = byteBuf.getByte(i + 3);
                    var b = byteBuf.getUnsignedByte(i + 3);
                    code = (byte) ((code << 1) | (b >>> 7));
                    codeStr += (0x7fff & (0x7f & b)) < 10 ? "0" + (0x7fff & (0x7f & b)) : (0x7fff & (0x7f & b));
                }
                codeStr = 130 + code + codeStr;
            }
            log.debug("upgrade device code : {}", codeStr);
            // 状态字：81请求数据，82升级成功，83升级失败
            short upgradeState = byteBuf.getUnsignedByte(7 + shift);
            if (upgradeState == 0x81) {
                int packetNum = byteBuf.getUnsignedShort(8 + shift);
                int packetSize = byteBuf.getUnsignedShort(10 + shift);
                return fireFirmwarePacket(message, codeStr, packetNum, packetSize, identity, shift);
            }
            if (upgradeState == 0x82) {
                return updateSuccess(codeStr, message.getPeer());
            }
            if (upgradeState == 0x83) {
                return updateFail(codeStr, message.getPeer());
            }
            log.error("Unknown upgrade state : {}, client : {}, code : {}",
                    String.format("%02X", upgradeState), message.getPeer(), codeStr);
            return Mono.just(false);
        }).then();
    }

    private Mono<Boolean> fireFirmwarePacket(ClientMessage message, String codeStr,
                                             int packetNum, int packetSize, byte[] identity, int shift) {
        var job = taskQueue.getJob(codeStr);
        if (null == job) {
            message.getByteMessage().release();
            return Mono.just(false);
        }
        job.setActiveTime(System.currentTimeMillis());
        byte[] data = job.getFirmware();
        // jms跳过固件包前8个字节，mw不用
        int startIndex = packetNum * packetSize + (shift == 0 ? 8 : 0);
        boolean lastPacket = startIndex + packetSize >= data.length;
        packetSize = lastPacket ? data.length - startIndex : packetSize;
        if (packetSize > 0) {
            byte[] firmwarePacket = new byte[packetSize];
            System.arraycopy(data, startIndex, firmwarePacket, 0, packetSize);
            // 数据包长度
            int packetLength = firmwarePacket.length + 9 + shift;
            ByteBuf byteBuf = message.getByteMessage().alloc().directBuffer(packetLength);
            // 头部
            byteBuf.writeBytes(new byte[]{0x29, 0x29, 0x63});
            // 长度
            byteBuf.writeShort(packetLength);
            // 设备号
            byteBuf.writeBytes(identity);
            // 最后一包标志，最后一包为0xFF,其余为0
            byteBuf.writeByte(lastPacket ? 0xff : 0);
            // 固件分包编号
            byteBuf.writeShort(packetNum);
            // 固件包内容
            byteBuf.writeBytes(firmwarePacket);
            // 异或校验
            byte[] bytes = ByteBufUtil.getBytes(byteBuf);
            byteBuf.writeByte(bccCheck(bytes, bytes.length));
            // 结束标志
            byteBuf.writeByte(0x0d);
            ServerMessage res = ServerMessage.builder()
                    .message(byteBuf)
                    .peer(message.getPeer())
                    .protocol(ClientProtocol.FIRMWARE)
                    .transportType(message.getTransportType())
                    .empty(false)
                    .loginFailed(false)
                    .build();
            message.getByteMessage().release();
            sessionManager.fireMsg(message.getPeer(), List.of(res));
            return Mono.just(true);
        }
        log.error("invalid packet size : {}, packet num : {}, device code : {}", packetSize, packetNum, codeStr);
        return Mono.just(false);
    }

    public byte bccCheck(byte[] data, int size) {
        byte check = 0;
        for (int i = 0; i < size; i++) {
            check ^= data[i];
        }
        return check;
    }

    private Mono<Boolean> updateSuccess(String codeStr, InetSocketAddress peer) {
        taskQueue.updateSuccess(codeStr, peer);
        return Mono.just(false);
    }

    private Mono<Boolean> updateFail(String codeStr, InetSocketAddress peer) {
        taskQueue.updateFail(codeStr, peer);
        return Mono.just(false);
    }

    @Override
    public ClientProtocol protocol() {
        return ClientProtocol.FIRMWARE;
    }

    public static String bcdBytesToString(byte[] bytes) {
        StringBuilder sb = new StringBuilder();
        for (byte b : bytes) {
            // 处理高四位
            int high = (b & 0xF0) >>> 4;
            sb.append(high);
            // 处理低四位
            int low = b & 0x0F;
            sb.append(low);
        }
        return sb.toString();
    }


}
