package com.baoli.pricer.pojo;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;

import java.io.Serial;
import java.time.LocalDateTime;
import java.io.Serializable;

/**
 * 对应数据库表 baoli.version
 */
@Data
@Setter
@Getter
@AllArgsConstructor
public class Version implements Serializable {
    @Serial
    private static final long serialVersionUID = 1L;

    /** 自动生成主键 */
    private Integer id;

    /** 0=材料表,1=工艺表 */
    private Byte flag;

    /** 版本名称，如 material_202506281530 */
    private String versionName;

    /** 版本描述或备注 */
    private String description;

    /** 创建时间，自动填充 */
    private LocalDateTime createdAt;

    public Version() {
    }

    public Version(Byte flag, String versionName, String description) {
        this.flag = flag;
        this.versionName = versionName;
        this.description = description;
        this.createdAt = LocalDateTime.now(); // 默认创建时间为当前时间
    }

    @Override
    public String toString() {
        return "Version{" +
                "id=" + id +
                ", flag=" + flag +
                ", versionName='" + versionName + '\'' +
                ", description='" + description + '\'' +
                ", createdAt=" + createdAt +
                '}';
    }
}
