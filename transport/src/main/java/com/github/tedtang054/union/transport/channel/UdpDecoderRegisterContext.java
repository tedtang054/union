package com.github.tedtang054.union.transport.channel;

import com.google.common.cache.Cache;
import com.google.common.cache.CacheBuilder;
import com.github.tedtang054.union.transport.constant.ClientProtocol;

import java.net.InetSocketAddress;
import java.util.concurrent.TimeUnit;

/**
 * @Author: dengJh
 * @Date: 2024/09/26 21:30
 * udp客户端协议记录器
 */
public class UdpDecoderRegisterContext {

    static Cache<InetSocketAddress, ClientProtocol> CLIENT_PROTOCOLS = CacheBuilder.newBuilder()
            .maximumSize(100000)
            .expireAfterAccess(60, TimeUnit.SECONDS)
            .build();

    static Cache<InetSocketAddress, InetSocketAddress> BLACK_LIST = CacheBuilder.newBuilder()
            .weakValues()
            .weakKeys()
            .maximumSize(100000)
            .expireAfterWrite(30, TimeUnit.SECONDS)
            .build();


    public static void registerHandler(InetSocketAddress peer, ClientProtocol protocol) {
        CLIENT_PROTOCOLS.put(peer, protocol);
    }

    public static ClientProtocol getClientProtocol(InetSocketAddress peer) {
        return CLIENT_PROTOCOLS.getIfPresent(peer);
    }

    public static boolean inBlackList(InetSocketAddress peer) {
        return BLACK_LIST.getIfPresent(peer) != null;
    }

    public static void addToBlackList(InetSocketAddress peer) {
        BLACK_LIST.put(peer, peer);
    }

}
