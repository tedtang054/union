package com.github.tedtang054.union.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ArgumentInvalidResult {

    private String field;
    private Object rejectedValue;
    private String reason;

}
