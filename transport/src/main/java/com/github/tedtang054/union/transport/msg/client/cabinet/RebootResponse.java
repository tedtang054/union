package com.github.tedtang054.union.transport.msg.client.cabinet;

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
public class RebootResponse implements ClientVo {

    /**
     * 状态码，0成功
     */
    private Byte r;
    /**
     * 请求序号
     */
    private Integer msgack;

}
