package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;

/**
 * @Author: dengJh
 * @Date: 2024/07/03 10:32
 * 服务端信息
 */
@Data
@Builder
@AllArgsConstructor
public class ServerMessage {

    // 传输协议
    private TransportType transportType;

    // 客户端协议
    private ClientProtocol protocol;

    // 是否为空响应
    private Boolean empty;

    // 是否登录失败
    private Boolean loginFailed;

    // 客户端地址
    private InetSocketAddress peer;

    // 客户端唯一标识
    private String identity;

    // 信息
    private Object message;

}
