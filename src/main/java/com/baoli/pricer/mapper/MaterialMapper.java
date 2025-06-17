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
     * 分页查询
     * @param offset 偏移量 = (page-1)*size
     * @param limit  每页大小
     */
    @Select("SELECT material_category, material_name, photo_daban, photo_chengpin, photo_xiaoguo, price, id "
            + "FROM baoli.material "
            + "ORDER BY id "
            + "LIMIT #{limit} OFFSET #{offset}")
    List<Material> findByPage(@Param("offset") int offset, @Param("limit") int limit);

    /** 模糊匹配查询：根据关键字查询材料 */
    List<Material> searchByKeyword(@Param("keyword") String keyword);

    /**
     * 根据材料品类精确或模糊检索
     */
    @Select("SELECT id, material_category, material_name, photo_daban, photo_chengpin, photo_xiaoguo, price " +
            "FROM baoli.material " +
            "WHERE material_category = #{category}")
    List<Material> findByCategory(@Param("category") String category);

}