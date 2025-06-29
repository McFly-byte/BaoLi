/**
 * DateTime: 2025/6/16 16:23
 * Author: LMC
 * Comments:
 **/
package com.baoli.pricer.mapper;

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


    /**
     * 分页查询所有工艺
     */
    List<ProcessMethod> getAll(@Param("versionId") int versionId);

    /**
     * 模糊匹配查询：根据关键字查询材料品类或施工工艺
     */
    List<ProcessMethod> getByKeyword(@Param("versionId") int versionId, @Param("keyword") String keyword);

    /**
     * 根据材料品类精确检索
     */
    List<ProcessMethod> getByCategory(@Param("versionId") int versionId, @Param("category") String category);

    /**
     *  查找所有不同的材料品类
     */
    @Select("SELECT distinct material_category FROM baoli.process_method WHERE version_id = #{versionId}")
    List<String> getAllCategories(@Param("versionId") int versionId);

    /**
     * 按id查找单条
     */
    @Select("SELECT * FROM baoli.process_method WHERE id = #{id}")
    ProcessMethod getById(@Param("id") int id);

    /**
     * 按多个 id 查询 Material 列表
     */
    List<ProcessMethod> getByIds(@Param("ids") List<Integer> ids);
}

