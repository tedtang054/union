package com.github.tedtang054.union.common.constant;

import lombok.Getter;

/**
 * 错误提示类
 */
@Getter
public enum ErrorCode {

    SUCCESS(200),

    // 4xxx为用户操作错误码
    INVALID_PARAMS(4000),
    MISSING_PARAMS(4001),

    // 5xxx为服务器错误码
    /**
     * 服务器未知错误
     */
    UNKNOWN_SERVER_ERROR(5000),
    ;

    private final Integer code;

    ErrorCode(Integer code) {
        this.code = code;
    }

}