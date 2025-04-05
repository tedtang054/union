package com.github.tedtang054.union.transport.protocol.rfid.channel;

import com.github.tedtang054.union.transport.channel.ByteToClientMsgHandler;
import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import com.github.tedtang054.union.transport.channel.PipelineInitializer;
import io.netty.channel.ChannelPipeline;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 17:51
 */
public class RfidPipelineInitializer implements PipelineInitializer {

    private RfidEncodeHandler encodeHandler;

    private RfidDecodeHandler decodeHandler;

    private ByteToClientMsgHandler clientMsgHandler;

    public RfidPipelineInitializer() {
        this.encodeHandler = RfidEncodeHandler.INSTANCE;
        this.clientMsgHandler = new ByteToClientMsgHandler(ClientProtocol.RFID_STATION);
        this.decodeHandler = new RfidDecodeHandler();
    }

    @Override
    public void initPipelineDecoder(ChannelPipeline pipeline, String baseName, TransportType type) {
        if (TransportType.UDP.equals(type)) {
            pipeline.addAfter(baseName, RfidDecodeHandler.NAME, decodeHandler);
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
        return RfidDecodeHandler.NAME;
    }

}
