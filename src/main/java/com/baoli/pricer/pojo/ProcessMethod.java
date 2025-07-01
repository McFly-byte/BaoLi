package com.baoli.pricer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class ProcessMethod {
    private Integer id;
    private String materialCategory;
    private String method;
    private Double price;
}
