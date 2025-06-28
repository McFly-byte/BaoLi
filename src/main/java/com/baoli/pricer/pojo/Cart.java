package com.baoli.pricer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Cart {
    private Integer id;
    private Integer materialId;
    private Integer methodId;
    private String materialCategory;
    private String materialName;
    private Double materialPrice;
    private String method;
    private Double methodPrice;
    private Integer quantity;
    private String useplace;
    private Double totalPrice;   // 由数据库自动计算，可在返回中读取
    private String orderId;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
