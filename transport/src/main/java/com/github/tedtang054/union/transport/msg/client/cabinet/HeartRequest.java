package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 10:23
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class HeartRequest implements ClientVo {

    /**
     * 信号质量
     */
    private Integer csq;

}
