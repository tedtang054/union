package com.github.tedtang054.union.transport.constant;

import lombok.Getter;

/**
 * @Author: dengJh
 * @Date: 2024/04/19 9:00
 */
@Getter
public enum CabinetErrorEnum {

    SUCCESS((byte) 0),
    FAILURE((byte) 1),
    DEVICE_NOT_FOUND((byte) 2),
    INTERNAL_NOT_RECOGNIZED((byte) 3),

    ;
    private Byte code;

    private CabinetErrorEnum(Byte code) {
        this.code = code;
    }
}
