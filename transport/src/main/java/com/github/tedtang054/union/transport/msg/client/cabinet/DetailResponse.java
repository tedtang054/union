package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 10:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DetailResponse implements ClientVo {

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
    private List<Detail> d;

    @Data
    @Builder
    @EqualsAndHashCode
    @AllArgsConstructor
    @NoArgsConstructor
    public static class Detail {

        /**
         * 充电口
         */
        private Short n;
        /**
         * 	状态，0：无卡片，1：有卡片，2:卡片充电不正常，3：无法读取卡片sn
         */
        private Byte s;
        /**
         * 卡片的序列号
         */
        private String sn;
        /**
         * 卡片电量，0：0-9%，1:10%-19%
         */
        private Byte e;

        public Byte getE() {
            return e == null ? 0 : e;
        }

    }
}
