/**
 * DateTime: 2025/6/15 20:31
 * Author: LMC
 * Comments: MyBatis Mapper 接口：material 表操作
 **/
package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.Material;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface MaterialMapper {
    /**
     * 单条插入
     */
    int insert(@Param("entity") Material entity);

    /**
     * 批量插入
     */
    int insertBatch(@Param("list") List<Material> list);


    /**
     * 分页查询material表中所有数据
     */
    List<Material> getAll(@Param("versionId") int versionId);

    /** 根据材料名称模糊查询 */
    List<Material> getByMaterialName(@Param("versionId") int versionId, @Param("keyword") String keyword);

    /** 根据材料品类模糊查询 */
    List<Material> getByMaterialCategory(@Param("versionId") int versionId, @Param("keyword") String keyword);

    /**
     * 确定了<材料品类>就模糊查找<材料名>，否则就模糊查找<材料品类>
     */
    List<Material> getByKeyword(
            @Param("versionId") int versionId,
            @Param("keyword") String keyword,
            @Param("category") String category
    );


    /**
     * 大类 + 小类 + 材料名 查询
     */
    List<Material> getByTriple(
            @Param("versionId") int versionId,
            @Param("bigCategory") String bigCategory,
            @Param("category") String category,
            @Param("name") String name
    );

    /**
    *  查找所有不同的材料品类
    */
    @Select("SELECT distinct material_category FROM baoli.material WHERE version_id = #{versionId}")
    List<String> getAllCategories(@Param("versionId") int versionId );

    /**
     * 查找所有不同的材料大类
     */
    @Select("SELECT distinct material_big_category FROM baoli.material WHERE version_id = #{versionId}")
    List<String> getAllBigCategories(@Param("versionId") int versionId );


    /**
     * 按id查找单条
     * 就不用versionId了，因为id是唯一的
     */
    Material getById( @Param("id") int id);

    /**
     * 按多个 id 查询 Material 列表
     */
    List<Material> getByIds(@Param("ids") List<Integer> ids);


}