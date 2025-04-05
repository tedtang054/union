package com.github.tedtang054.union.transport.protocol.jms.channel;

import com.alibaba.fastjson2.JSON;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import com.github.tedtang054.union.transport.ServerMessage;
import io.netty.buffer.ByteBufAllocator;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelOutboundHandlerAdapter;
import io.netty.channel.ChannelPromise;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

import java.nio.charset.Charset;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 17:05
 */
@Slf4j
@ChannelHandler.Sharable
public class CabinetEncodeHandler extends ChannelOutboundHandlerAdapter {

    private static final byte[] PREFIX = "#*".getBytes();

    private static final byte[] SUFFIX = "*#".getBytes();

    public static final CabinetEncodeHandler INSTANCE = new CabinetEncodeHandler();

    private CabinetEncodeHandler() {}

    @Override
    public void write(ChannelHandlerContext ctx, Object msg, ChannelPromise promise) throws Exception {
        if (msg instanceof DatagramPacket) {
            ctx.writeAndFlush(msg, promise);
            return;
        }
        if (msg instanceof ServerMessage) {
            var message = (ServerMessage) msg;
            ClientProtocol protocol = message.getProtocol();
            if (!protocol.equals(ClientProtocol.CABINET)) {
                ctx.writeAndFlush(msg, promise);
                return;
            }
            byte[] bytes = JSON.toJSONBytes(message.getMessage());
            ByteBufAllocator alloc = ctx.channel().alloc();
            var msgBuf = alloc.directBuffer(PREFIX.length + bytes.length + SUFFIX.length);
            msgBuf.writeBytes(PREFIX);
            msgBuf.writeBytes(bytes);
            msgBuf.writeBytes(SUFFIX);
            log.debug("cabinet output client : {}, message ↓↓↓ \r\n{}", message.getPeer(),
                    log.isDebugEnabled() ? msgBuf.toString(Charset.defaultCharset()) : null);
            if (TransportType.TCP.equals(message.getTransportType())) {
                ctx.writeAndFlush(msgBuf, promise);
                return;
            }
            ctx.writeAndFlush(new DatagramPacket(msgBuf, message.getPeer()), promise);
            return;
        }
        ctx.writeAndFlush(msg, promise);
    }

}
