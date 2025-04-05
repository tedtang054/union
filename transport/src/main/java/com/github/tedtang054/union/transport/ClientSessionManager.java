package com.github.tedtang054.union.transport;

import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;
import reactor.netty.Connection;

import java.net.InetSocketAddress;
import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/07/03 10:29
 */
public interface ClientSessionManager {

    /**
     * 根据客户端地址发送信息
     * @param client
     * @param messages
     * @return
     */
    Boolean fireMsg(InetSocketAddress client, List<ServerMessage> messages);

    /**
     * 根据客户端唯一标识发送信息
     * @param identity
     * @param messages
     * @return
     */
    Boolean fireMsg(String identity, List<ServerMessage> messages);

    /**
     * 注册tcp连接信息
     * @param connection
     * @param timeout
     */
    void registerConnection(Connection connection, Integer timeout);

    /**
     * 根据唯一标识获取ip
     * @param identity
     * @return
     */
    InetSocketAddress getRemoteAddress(String identity);

    /**
     * 踢下线
     * @param client
     * @param reason
     * @param sendMsg 踢下线前是否有信息发送
     * @return
     */
    Mono<Void> kickOff(InetSocketAddress client, String reason, boolean sendMsg);

    /**
     * 登录校验成功，注册会话信息
     * @param client
     * @param msg
     * @return
     */
    Mono<Boolean> registerSession(InetSocketAddress client, VerifyMsg msg);

    /**
     * 注册发射器
     * @param client
     * @return
     */
    Flux<ServerMessage> registerEmitter(InetSocketAddress client);

    /**
     * 更新设备心跳
     * @param client
     * @return
     */
    Mono<Boolean> updateHeartbeat(InetSocketAddress client);

    /**
     * 获取客户端会话信息
     * @param client
     * @return
     */
    Mono<ClientSession> getSession(InetSocketAddress client);

    /**
     * 检查客户端是否登录
     * @param client
     * @return
     */
    Mono<Boolean> checkLogin(InetSocketAddress client);
}
