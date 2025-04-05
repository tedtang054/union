package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 9:52
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ServerPayload<T extends ServerVo> {

    /**
     * 指令
     */
    private String cmd;
    /**
     * 消息序号
     */
    private Integer msg;
    /**
     * 应该消息序号
     */
    private Integer msgack;
    /**
     * aims说明：
     * 1.机柜主板在设备中的编号，编号从1开始（带网络通讯模组的板子）。
     * 2.数据上行时（设备-服务端）aims的值表示该消息是设备中的aims号卡片主板上报。
     * 3.数据下行时（服务器-设备）aims的值表示该消息由设备中的aims号机柜主板接收，如果amis为0则设备中所有机柜主板接收该消息。
     * 4.租借时aims一定要为非0的值
     * 5.设备登录和心跳时因为只有1号机柜主板带有网络通讯模组所以这些消息都由1号机柜主板统一上报，服务器应答时需要告诉所有机柜主板（也就是aims要为0）。
     */
    private Byte aims;
    /**
     * 消息体
     */
    private T data;

    private transient Boolean empty;

}
