package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:40
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForceRequest implements ServerVo {

    /**
     * 弹出口，0弹出所有
     */
    private Short n;

}
