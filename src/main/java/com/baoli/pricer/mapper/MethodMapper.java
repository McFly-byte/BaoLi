/**
 * DateTime: 2025/6/16 16:23
 * Author: LMC
 * Comments:
 **/
package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MethodMapper {
    /**
     * 批量插入多条施工工艺记录
     */
    int insertBatch(@Param("list") List<ProcessMethod> list);

    /** 查询总记录数 */
    @Select("SELECT COUNT(*) FROM baoli.process_method")
    int countAll();

    /**
     * 分页查询
     * @param offset 偏移量 = (page-1)*size
     * @param limit  每页大小
     */
    @Select("SELECT id, material_category, method, price "
            + "FROM baoli.process_method "
            + "ORDER BY id "
            + "LIMIT #{limit} OFFSET #{offset}")
    List<ProcessMethod> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    /** 模糊匹配查询：根据关键字查询材料品类或施工工艺 */
    List<ProcessMethod> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据材料品类精确检索
     */
    @Select("SELECT * FROM baoli.process_method WHERE material_category = #{category}")
    List<ProcessMethod> findByCategory(@Param("category") String category);
}

