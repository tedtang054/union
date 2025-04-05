package com.github.tedtang054.union.transport.protocol.jms.channel;

import com.github.tedtang054.union.transport.channel.ByteToClientMsgHandler;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import com.github.tedtang054.union.transport.channel.PipelineInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:51
 */
public class CabinetPipelineInitializer implements PipelineInitializer {

    private CabinetEncodeHandler encodeHandler;

    private CabinetDecodeHandler decodeHandler;

    private ByteToClientMsgHandler clientMsgHandler;

    public CabinetPipelineInitializer() {
        this.encodeHandler = CabinetEncodeHandler.INSTANCE;
        this.clientMsgHandler = new ByteToClientMsgHandler(ClientProtocol.CABINET);
        this.decodeHandler = new CabinetDecodeHandler();
    }

    @Override
    public void initPipelineDecoder(ChannelPipeline pipeline, String baseName, TransportType type) {
        if (TransportType.UDP.equals(type)) {
            pipeline.addAfter(baseName, CabinetDecodeHandler.NAME, decodeHandler);
            return;
        }
        pipeline.addFirst(clientMsgHandler);
        pipeline.addFirst(decodeHandler);
    }

    @Override
    public void initPipelineEncoder(ChannelPipeline pipeline, String baseName, TransportType type) {
        pipeline.addFirst(encodeHandler);
    }

    @Override
    public CandidateDecoder getCandidateDecoder() {
        return decodeHandler;
    }

    @Override
    public void free() {
        if (decodeHandler != null) {
            decodeHandler.free();
        }
    }

    @Override
    public String decoderName() {
        return CabinetDecodeHandler.NAME;
    }

}
