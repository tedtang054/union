package com.github.tedtang054.union.transport.protocol.jms.channel;

import com.github.tedtang054.union.transport.channel.UdpDecoderRegisterContext;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.channel.PipelineInitializer;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.buffer.Unpooled;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.socket.DatagramPacket;
import io.netty.handler.codec.DelimiterBasedFrameDecoder;
import lombok.extern.slf4j.Slf4j;

import java.net.InetSocketAddress;
import java.util.ArrayList;
import java.util.List;


/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:46
 * 信息格式：292987000bda0052138100000400920d
 */
@Slf4j
public class UpgradeDecodeHandler extends DelimiterBasedFrameDecoder implements PipelineInitializer.CandidateDecoder {

    public static final String NAME = "upgrade";

    private List<ByteBuf> messages = new ArrayList<>();

    private ByteBuf cumulation;

    private boolean first;

    public UpgradeDecodeHandler() {
        super(32, Unpooled.copiedBuffer(new byte[]{0x29, 0x29}), Unpooled.copiedBuffer(new byte[]{0x0d}));
    }

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (!(msg instanceof ClientMessage)) {
            InetSocketAddress peer = (InetSocketAddress) (msg instanceof ByteBuf ? ctx.channel().remoteAddress() : ((DatagramPacket) msg).sender());
            if (msg instanceof ByteBuf) {
                var byteBuf = (ByteBuf) msg;
                log.debug("upgrade input tcp client : {} message ↓↓↓ \r\n{}", peer, log.isDebugEnabled() ? ByteBufUtil.hexDump(byteBuf) : null);
                if (null == cumulation) {
                    super.channelRead(ctx, msg);
                    return;
                }
                // 剩余半包数据
                var newBuf = cumulation.writeBytes(byteBuf).copy();
                cumulation.release();
                byteBuf.release();
                cumulation = null;
                super.channelRead(ctx, newBuf);
                return;
            }
            var protocol = UdpDecoderRegisterContext.getClientProtocol(peer);
            if (null != protocol && !protocol.equals(protocol())) {
                ctx.fireChannelRead(msg);
                return;
            }
            var packet = (DatagramPacket) msg;
            ByteBuf content = packet.content();
            log.debug("upgrade input udp client : {} message ↓↓↓ \r\n{}", peer, log.isDebugEnabled() ? ByteBufUtil.hexDump(content) : null);
            var lastByte = content.getByte(content.readableBytes() - 1);
            if (content.getByte(0) == 0x29 && content.getByte(1) == 0x29 && lastByte == 0x0d) {
                UdpDecoderRegisterContext.registerHandler(packet.sender(), ClientProtocol.FIRMWARE);
                var clientMsg = ClientMessage.builder()
                        .transportType(TransportType.UDP)
                        .protocol(ClientProtocol.FIRMWARE)
                        .byteMessage(content.retainedSlice(2, content.readableBytes() - 3))
                        .peer(peer).build();
                ctx.fireChannelRead(clientMsg);
                content.release();
                return;
            }
        }
        ctx.fireChannelRead(msg);
    }

    @Override
    public Integer targetDecoder(ChannelHandlerContext ctx, ByteBuf msg, TransportType transportType) {
        // tcp协议
        try {
            first = cumulation == null;
            cumulation = MERGE_CUMULATOR.cumulate(ctx.alloc(),
                    first ? Unpooled.EMPTY_BUFFER : cumulation, msg);
            var bufSize = cumulation.readableBytes();
            if (bufSize > 64) {
                log.debug("UpgradeDecodeHandler exceed max size : {} ", bufSize);
                return -1;
            }
            while (true) {
                var message = (ByteBuf) super.decode(ctx, cumulation);
                // 解析出的报文有可能为空，因为有首部和尾部分隔符
                if (null != message && message.readableBytes() != 0) {
                    messages.add(message);
                }
                // bufSize不变表示解不出包，0代表已经完成解析
                if (bufSize == cumulation.readableBytes() || 0 == cumulation.readableBytes()) {
                    break;
                }
                bufSize = cumulation.readableBytes();
            }
            return !messages.isEmpty() ? 1 : 0;
        } catch (Exception e) {
            log.error("UpgradeDecodeHandler decode msg failed : ", e);
            return -1;
        }
    }

    public void free() {
        if (null != cumulation) {
            cumulation.release();
            cumulation = null;
        }
    }

    @Override
    public List<ByteBuf> getFirstMessages() {
        return messages;
    }

    @Override
    public ClientProtocol protocol() {
        return ClientProtocol.FIRMWARE;
    }
}
