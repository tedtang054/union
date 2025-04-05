package com.github.tedtang054.union.transport.channel;

import com.github.tedtang054.union.transport.ClientMessage;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;

/**
 * @Author: dengJh
 * @Date: 2024/09/26 23:40
 */
@Slf4j
@ChannelHandler.Sharable
public class UdpDecodeRecordHandler extends ChannelInboundHandlerAdapter {

    public static final String NAME = "udp_decode_record";

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ClientMessage)) {
            var packet = (DatagramPacket) msg;
            if (null != UdpDecoderRegisterContext.getClientProtocol(packet.sender())) {
                return;
            }
            UdpDecoderRegisterContext.addToBlackList(packet.sender());
            log.error("unsupported msg : {}, client : {}", ByteBufUtil.hexDump(packet.content()), packet.sender());
            return;
        }
        super.channelRead(ctx, msg);
    }
}
