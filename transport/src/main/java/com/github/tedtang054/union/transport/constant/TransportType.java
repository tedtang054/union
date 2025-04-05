package com.github.tedtang054.union.transport.constant;

import lombok.Getter;

/**
 * @Author: dengJh
 * @Date: 2024/07/04 10:38
 */
@Getter
public enum TransportType {

    TCP(1),
    UDP(0),
    ;

    private Integer code;

    TransportType(Integer code) {
        this.code = code;
    }
}
