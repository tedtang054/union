package com.github.tedtang054.union.common.vo;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.ArrayList;
import java.util.List;

@Data
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class PageInfoVo<T> {

    private Long total;
    private Integer pages;
    private List<T> data;

    public static PageInfoVo empty() {
        return PageInfoVo.builder().total(0L).pages(0).data(new ArrayList<>()).build();
    }
}
