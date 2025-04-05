package com.github.tedtang054.union.transport.protocol.rfid.channel;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import com.github.tedtang054.union.transport.ServerMessage;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:48
 */
@Slf4j
@ChannelHandler.Sharable
public class RfidEncodeHandler extends ChannelOutboundHandlerAdapter {

    public static final RfidEncodeHandler INSTANCE = new RfidEncodeHandler();

    private RfidEncodeHandler() {}

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof DatagramPacket) {
            ctx.writeAndFlush(msg, promise);
            return;
        }
        if (msg instanceof ServerMessage) {
            var message = (ServerMessage) msg;
            ClientProtocol protocol = message.getProtocol();
            if (!protocol.equals(ClientProtocol.RFID_STATION)) {
                ctx.writeAndFlush(msg, promise);
                return;
            }
            log.debug("rfid output client : {}, message ↓↓↓ \r\n{}", message.getPeer(), ByteBufUtil.hexDump((ByteBuf) message.getMessage()));
            if (TransportType.TCP.equals(message.getTransportType())) {
                ctx.writeAndFlush(message.getMessage(), promise);
                return;
            }
            ctx.writeAndFlush(new DatagramPacket((ByteBuf) message.getMessage(), message.getPeer()), promise);
            return;
        }
        ctx.writeAndFlush(msg, promise);
    }
}
