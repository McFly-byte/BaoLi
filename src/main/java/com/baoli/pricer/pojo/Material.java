package com.baoli.pricer.pojo;

import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.AllArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class Material {
    private Integer id;
    private String materialBigCategory;
    private String materialCategory;
    private String materialName;
    private String photoDaban;     // 存放 MinIO URL
    private String photoChengpin;
    private String photoXiaoguo;
    private Double price;
    private Integer versionId;
    private String description;
}
