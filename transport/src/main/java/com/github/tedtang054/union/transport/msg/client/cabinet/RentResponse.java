package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 11:57
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RentResponse implements ClientVo {

    /**
     * 租借口
     */
    private Short n;
    /**
     * 响应状态码
     */
    private Short r;
    /**
     * 设备库存，最大支持255
     */
    private Short st;
    /**
     * 应该消息序号
     */
    private Integer msgack;
    /**
     * 库存充电口的详细信息
     */
    private List<DetailResponse.Detail> d;

}
