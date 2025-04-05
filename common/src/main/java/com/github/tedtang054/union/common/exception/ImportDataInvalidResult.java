package com.github.tedtang054.union.common.exception;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

/**
 * @Author: dengJh
 * @Date: 2023/01/16 9:49
 */
@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ImportDataInvalidResult {

    private Integer rowNum;

    private List<ArgumentInvalidResult> invalidResults = new ArrayList<>();

    public ImportDataInvalidResult(Integer rowNum) {
        this.rowNum = rowNum;
    }

    public Boolean invalidData() {
        return !invalidResults.isEmpty();
    }

}
