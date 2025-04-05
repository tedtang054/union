package com.github.tedtang054.union.transport;

import com.github.tedtang054.union.transport.constant.ClientProtocol;
import com.github.tedtang054.union.transport.constant.TransportType;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 14:56
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class VerifyMsg {

    // 接入客户端数据库id
    private Integer id;

    // 客户端唯一标识
    private String identity;

    // 接入传输协议
    private TransportType transportType;

    // 接入协议
    private ClientProtocol protocol;

}
