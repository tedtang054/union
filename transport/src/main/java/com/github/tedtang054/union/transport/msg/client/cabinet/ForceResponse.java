package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 14:41
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ForceResponse implements ClientVo {

    /**
     * 请求序列号
     */
    private Integer msgack;
    /**
     * 弹出口
     */
    private Short n;
    /**
     * 设备库存，最大支持255
     */
    private Short st;
    /**
     * 状态码，0成功
     */
    private Byte r;
    /**
     * 库存充电口的详细信息
     */
    private List<DetailResponse.Detail> d;

}
