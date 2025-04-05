package com.github.tedtang054.union.transport.msg.server.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 10:48
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailRequest implements ServerVo {

    /**
     * 0：查询所有的卡片口的信息，非零指定卡片口信息
     */
    private Short n;

}
