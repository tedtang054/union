package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 9:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse implements ServerVo {

    /**
     * 0：登录成功，1：认证失败，2：无此设备序列号
     */
    private Byte r;
    /**
     * 心跳配置，单位秒
     */
    private Integer heart;

}
