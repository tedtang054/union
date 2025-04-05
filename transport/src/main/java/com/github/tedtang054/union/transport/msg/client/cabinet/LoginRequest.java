package com.github.tedtang054.union.transport.msg.client.cabinet;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/04/18 9:55
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class LoginRequest implements ClientVo {

    /**
     * 厂家,示例：ZDHX
     */
    private String mft;
    /**
     * 通讯模块,示例：EC20-EFDG
     */
    private String mod;
    /**
     * 	型号 示例：CT03
     */
    private String type;
    /**
     * 硬件版本号 示例：HWV001
     */
    private String hd;
    /**
     * 固件版本号，示例：FWV001
     */
    private String fw;
    /**
     * 设备序列号md5加密，登录认证，需要服务器校验认证，序列号+密钥进行加密，取中间16位
     */
    private String md5;
    /**
     * 设备充电口数量，不是必须，有型号确定数量
     */
    private Short num;
    /**
     * 设备设备中级联数量
     */
    private Integer bn;
    /**
     * 声音大小
     */
    private Integer vol;
    /**
     * 	imei序列号
     */
    private String imei;
    /**
     *
     */
    private String ccid;
    /**
     *
     */
    private String apn;
    /**
     * 账号
     */
    private String apnuser;
    /**
     * 密码
     */
    private String apnpass;

}
