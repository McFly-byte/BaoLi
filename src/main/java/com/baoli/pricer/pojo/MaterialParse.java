package com.baoli.pricer.pojo;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class MaterialParse {
    @ExcelProperty("序号")
    private Integer seq;

    @ExcelProperty("材料品类")
    private String materialCategory;

    @ExcelProperty("材料名称")
    private String materialName;

    @ExcelProperty("大板图片")
    private byte[] photoDabanBytes;

    @ExcelProperty("成品图片")
    private byte[] photoChengpinBytes;

    @ExcelProperty("施工效果图")
    private byte[] photoXiaoguoBytes;

    @ExcelProperty("成品材料不含税单价")
    private Double price;
}

