package com.github.tedtang054.union.transport.channel;

import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;

import java.net.InetSocketAddress;

/**
 * @Author: dengJh
 * @Date: 2024/07/05 11:55
 */
public class ByteToClientMsgHandler extends ChannelInboundHandlerAdapter {

    public static final String NAME = "byteToClientMsgHandler";

    private ClientProtocol protocol;


    public ByteToClientMsgHandler(ClientProtocol protocol) {
        this.protocol = protocol;
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ClientMessage)) {
            InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
            var byteBuf = (ByteBuf) msg;
            if (byteBuf.readableBytes() > 0) {
                var clientMsg = ClientMessage.builder()
                        .transportType(TransportType.TCP)
                        .protocol(protocol)
                        .byteMessage(byteBuf)
                        .peer(socketAddress).build();
                ctx.fireChannelRead(clientMsg);
                return;
            }
            byteBuf.release();
            return;
        }
        ctx.fireChannelRead(msg);

    }
}
