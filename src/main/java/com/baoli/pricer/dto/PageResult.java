package com.baoli.pricer.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PageResult<T> {
    private long total;       // 总记录数
    private int page;         // 当前页
    private int size;         // 每页大小
    private List<T> data;     // 当前页数据
}

