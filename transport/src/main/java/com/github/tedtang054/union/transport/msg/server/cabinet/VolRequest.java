package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:56
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class VolRequest implements ServerVo {

    /**
     * 0：查询，1：设置
     */
    private Byte r;
    /**
     * r为1时n才有效。最小1，最大100
     */
    private Byte n;

}
