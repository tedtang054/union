package com.github.tedtang054.union.transport.channel;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.ChannelInboundHandler;
import io.netty.channel.ChannelPipeline;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 13:53
 */
public interface PipelineInitializer {

    /**
     * 初始化通道解码器
     * @param pipeline
     */
    void initPipelineDecoder(ChannelPipeline pipeline, String baseName, TransportType type);

    /**
     * 初始化通道编码器
     * @param pipeline
     */
    void initPipelineEncoder(ChannelPipeline pipeline, String baseName, TransportType type);

    /**
     * 获取候选解码器
     * @return
     */
    CandidateDecoder getCandidateDecoder();

    /**
     * 释放无效的协议解析器
     */
    void free();

    /**
     * 解码器名称
     * @return
     */
    String decoderName();

    interface CandidateDecoder extends ChannelInboundHandler {

        /**
         * 是否为可用的tcp解码器
         *
         * @param ctx
         * @param msg
         * @return -1异常，0 否，1是
         * @throws Exception
         */
        Integer targetDecoder(ChannelHandlerContext ctx, ByteBuf msg, TransportType transportType);

        /**
         * 获取第一条可用信息
         * @return
         */
        List<ByteBuf> getFirstMessages();

        /**
         * 客户端协议
         * @return
         */
        ClientProtocol protocol();

    }

}
