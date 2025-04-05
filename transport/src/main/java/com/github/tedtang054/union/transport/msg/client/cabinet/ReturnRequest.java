package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 13:59
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ReturnRequest implements ClientVo {

    /**
     * 归还口
     */
    private Short n;
    /**
     * 工卡序列号
     */
    private String sn;
    /**
     * 剩余电量
     */
    private Byte e;
    /**
     * 设备库存，最大支持255
     */
    private Short st;
    /**
     * 库存充电口的详细信息
     */
    private List<DetailResponse.Detail> d;

}
