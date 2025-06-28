package com.baoli.pricer.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessMethodParse {
    @ExcelProperty("序号")
    private Integer seq;

    @ExcelProperty("材料品类")
    private String materialCategory;

    @ExcelProperty("施工工艺")
    private String method;

    @ExcelProperty("人辅费用")
    private Double price;
}
