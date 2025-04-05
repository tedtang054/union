package com.github.tedtang054.union.transport.channel;

import cn.hutool.core.util.ReflectUtil;
import com.github.tedtang054.union.transport.ClientMessage;
import com.github.tedtang054.union.transport.constant.TransportType;
import io.netty.buffer.ByteBuf;
import io.netty.buffer.ByteBufUtil;
import io.netty.channel.ChannelHandler;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandlerAdapter;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.DatagramPacket;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.CollectionUtils;

import java.net.InetSocketAddress;
import java.util.List;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.CopyOnWriteArrayList;
import java.util.stream.Collectors;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 13:52
 * 复合解码器
 */
@Slf4j
@ChannelHandler.Sharable
public class CompositeDecodeHandler extends ChannelInboundHandlerAdapter {

    public static final String NAME = "composite";

    private List<Class<? extends PipelineInitializer>> initializerClasses;

    private static final Byte DECODE_THRESHOLD = 3;

    private volatile Boolean udpInit = false;

    private ConcurrentHashMap<InetSocketAddress, Integer> decodeTimes = new ConcurrentHashMap<>();

    private ConcurrentHashMap<InetSocketAddress, List<? extends PipelineInitializer>> initializers = new ConcurrentHashMap<>();

    @Override
    public void channelRead(ChannelHandlerContext ctx, Object msg) throws Exception {
        if (CollectionUtils.isEmpty(initializerClasses)) {
            ctx.channel().disconnect();
            log.info("empty protocol ");
            return;
        }
        if (!(msg instanceof ByteBuf) && !(msg instanceof DatagramPacket)) {
            log.error("unsupported transport message : {}", msg);
            ctx.channel().disconnect();
            return;
        }
        ChannelPipeline pipeline = ctx.channel().pipeline();
        TransportType transportType = msg instanceof ByteBuf ? TransportType.TCP : TransportType.UDP;
        if (TransportType.UDP.equals(transportType) && !udpInit) {
            var pipelineInitializers = initializerClasses.stream()
                    .map(ReflectUtil::newInstance)
                    .collect(Collectors.toList());
            for (PipelineInitializer pipelineInitializer : pipelineInitializers) {
                pipelineInitializer.initPipelineEncoder(pipeline, null, TransportType.UDP);
            }
            String decoderName = NAME;
            for (PipelineInitializer pipelineInitializer : pipelineInitializers) {
                pipelineInitializer.initPipelineDecoder(pipeline, decoderName, TransportType.UDP);
                decoderName = pipelineInitializer.decoderName();
            }
            pipeline.addAfter(decoderName, UdpDecodeRecordHandler.NAME, new UdpDecodeRecordHandler());
            udpInit = true;
            ctx.fireChannelRead(msg);
            return;
        }
        if (TransportType.UDP.equals(transportType)) {
            var packet = (DatagramPacket) msg;
            if (UdpDecoderRegisterContext.inBlackList(packet.sender())) {
                return;
            }
            ctx.fireChannelRead(msg);
            return;
        }
        var byteBuff = (ByteBuf) msg;
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();

        decodeTimes.merge(socketAddress, 1, Integer::sum);
        List<? extends PipelineInitializer> pipelineInitializers = initializers.get(socketAddress);
        if (null == pipelineInitializers) {
            pipelineInitializers = initializerClasses.stream()
                    .map(ReflectUtil::newInstance)
                    .collect(Collectors.toCollection(CopyOnWriteArrayList::new));
            initializers.put(socketAddress, pipelineInitializers);
        }
        List<? extends PipelineInitializer> finalPipelineInitializers = pipelineInitializers;
        PipelineInitializer targetInitializer = pipelineInitializers.stream()
                .filter(initializer -> {
                    Integer res = initializer.getCandidateDecoder().targetDecoder(ctx, byteBuff.copy(), transportType);
                    // 解析异常
                    if (res == -1) {
                        initializer.free();
                        finalPipelineInitializers.remove(initializer);
                    }
                    return res == 1;
                })
                .findFirst().orElse(null);

        if (CollectionUtils.isEmpty(pipelineInitializers)
                || (null == targetInitializer && decodeTimes.get(socketAddress) >= DECODE_THRESHOLD)) {
            decodeTimes.remove(socketAddress);
            initializers.remove(socketAddress);
            pipelineInitializers.forEach(PipelineInitializer::free);
            ctx.channel().disconnect();
            log.error("unsupported msg : {}, client : {}", ByteBufUtil.hexDump(byteBuff), socketAddress);
            byteBuff.release();
            return;
        }
        byteBuff.release();
        if (null != targetInitializer) {
            pipelineInitializers.stream()
                    .filter(initializer -> 0 == initializer.getCandidateDecoder().getFirstMessages().size())
                    .forEach(PipelineInitializer::free);
            initializers.remove(socketAddress);
            decodeTimes.remove(socketAddress);
            pipeline.remove(this);
            targetInitializer.initPipelineEncoder(pipeline, null, TransportType.TCP);
            targetInitializer.initPipelineDecoder(pipeline, null, TransportType.TCP);
            targetInitializer.getCandidateDecoder().getFirstMessages().forEach(buf -> {
                var clientMsg = ClientMessage.builder()
                        .transportType(transportType)
                        .protocol(targetInitializer.getCandidateDecoder().protocol())
                        .byteMessage(buf)
                        .peer(socketAddress).build();
                ctx.fireChannelRead(clientMsg);
            });
        }
    }

    public void setInitializerClasses(List<Class<? extends PipelineInitializer>> initializerClass) {
        initializerClasses = initializerClass;
    }

    @Override
    public void channelInactive(ChannelHandlerContext ctx) throws Exception {
        InetSocketAddress socketAddress = (InetSocketAddress) ctx.channel().remoteAddress();
        if (null != socketAddress) {
            Optional.ofNullable(initializers.remove(socketAddress))
                    .ifPresent(initializers -> initializers.forEach(PipelineInitializer::free));
            decodeTimes.remove(socketAddress);
        }
    }
}
