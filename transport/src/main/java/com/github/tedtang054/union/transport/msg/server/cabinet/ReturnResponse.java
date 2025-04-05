package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:03
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnResponse implements ServerVo {

    /**
     * 状态码，0成功
     */
    private Byte r;
    /**
     * 归还口
     */
    private Short n;

}
