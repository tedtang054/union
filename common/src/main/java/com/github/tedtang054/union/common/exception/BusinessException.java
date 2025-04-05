package com.github.tedtang054.union.common.exception;

import com.github.tedtang054.union.common.constant.ErrorCode;
import lombok.Data;

/** @author ydh 2021/3/8 */
@Data
public class BusinessException extends RuntimeException {

  private ErrorCode errorCode;

  private String message;

  private Object data;

  public BusinessException(ErrorCode errorCode) {
    super();
    this.errorCode = errorCode;
    this.message = errorCode.toString();
  }

  public BusinessException(ErrorCode errorCode, Object data) {
    super();
    this.errorCode = errorCode;
    this.data = data;
    this.message = errorCode.toString();
  }

  @Override
  public synchronized Throwable fillInStackTrace() {
    return this;
  }

}
