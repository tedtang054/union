package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import lombok.Builder;
import lombok.Data;

import java.net.InetSocketAddress;
import java.util.concurrent.ConcurrentHashMap;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 10:14
 */
@Data
@Builder
public class ClientSession {

    // 传输协议类型
    private TransportType transportType;
    // 应用协议
    private ClientProtocol protocol;
    // 设备数据库id
    private Integer id;
    // 会话序号，只增不减
    private Integer seq;
    // 服务器sessionid
    private Long sessionId;
    // 设备标识
    private String identity;
    // 客户端地址
    private InetSocketAddress peer;
    // 登录时间
    private Long loginTime;
    // 心跳时间
    private Long heartbeatTime;
    // session会话有效时长
    private Integer expires;
    // 注销
    private Boolean deregister;
    // 超时时间
    private Integer timeout;
    // 其他自定义参数
    private ConcurrentHashMap<String, Object> params = new ConcurrentHashMap<>();

    public boolean expired() {
        return System.currentTimeMillis() >= loginTime + (expires * 1000)
                || System.currentTimeMillis() - heartbeatTime >= timeout
                || deregister;
    }

    public void putParam(String key, Object value) {
        params.put(key, value);
    }

    public Object getParam(String key) {
        return params.get(key);
    }

    public void updateHeartBeat() {
        this.heartbeatTime = System.currentTimeMillis();
    }
}
