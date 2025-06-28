package com.baoli.pricer.dto;

import com.alibaba.excel.annotation.ExcelProperty;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class CartExportModel {
    @ExcelProperty("材料品类")
    private String materialCategory;

    @ExcelProperty("材料名称")
    private String materialName;

    @ExcelProperty("材料单价")
    private Double materialPrice;

    @ExcelProperty("使用地点")
    private String useplace;

    @ExcelProperty("工艺名称")
    private String method;

    @ExcelProperty("工艺单价")
    private Double methodPrice;

    @ExcelProperty("数量")
    private Integer quantity;

    @ExcelProperty("总价")
    private Double totalPrice;

    @ExcelProperty("订单号")
    private String orderId;
}
