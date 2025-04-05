package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import io.netty.buffer.ByteBuf;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @Author: dengJh
 * @Date: 2023/10/13 10:41
 */
@Data
@Builder
@AllArgsConstructor
public class ClientMessage {

    // 传输协议
    private TransportType transportType;

    // 客户端报文协议
    private ClientProtocol protocol;

    // 客户端地址
    private InetSocketAddress peer;

    // 消息
    private ByteBuf byteMessage;

    // 消息对象
    private Object message;

}
