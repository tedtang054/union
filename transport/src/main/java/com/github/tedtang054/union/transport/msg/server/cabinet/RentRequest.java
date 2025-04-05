package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 11:54
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentRequest implements ServerVo {

    /**
     * 充电口
     */
    private Short n;

}
