package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 15:00
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RebootRequest implements ServerVo {

    /**
     * 0：重启所有设备、包含自身，1：重启通讯模块
     */
    private Byte dev;

}
