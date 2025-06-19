/**
 * DateTime: 2025/6/15 20:31
 * Author: LMC
 * Comments: MyBatis Mapper 接口：material 表操作
 **/
package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
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

    /** 查询总记录数 */
    @Select("SELECT COUNT(*) FROM baoli.material")
    int countAll();

    /**
     * 分页查询material表中所有数据
     */
    @Select("SELECT * FROM baoli.material ORDER BY id" )
    List<Material> getALLByPage();

    /** 模糊匹配查询：根据关键字查询材料 */
    List<Material> getByKeyword(@Param("keyword") String keyword);

    /**
     * 根据材料品类检索
     */
    @Select("SELECT * " +
            "FROM baoli.material " +
            "WHERE material_category = #{category}")
    List<Material> getByCategory(@Param("category") String category);

    /**
    *  查找所有不同的材料品类
    */
    @Select("SELECT distinct material_category FROM baoli.material")
    List<String> getAllCategories();

    /**
     * 按id查找单条
     */
    @Select("SELECT * FROM baoli.material WHERE id = #{id}")
    Material findById(@Param("id") int id);

    /**
     * 按多个 id 查询 Material 列表
     */
    List<Material> findByIds(@Param("ids") List<Integer> ids);

}