/**
 * DateTime: 2025/6/18 11:21
 * Author: LMC
 * Comments:
 **/
package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.Cart;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

@Mapper
public interface CartMapper {
    // 单条插入（不写 total_price, created_at, updated_at）
    int addItem(Cart cart);

    // 批量插入
    int insertBatch(@Param("list") List<Cart> list);

    // 单条删除 by id
    int deleteById(@Param("id") Integer id);

    // 单条删除 by orderId
    int deleteByOrderId(@Param("orderId") String orderId);

    // 批量删除 by ids
    int deleteBatchIds(@Param("ids") List<Integer> ids);

    // 批量删除 by orderIds
    int deleteBatchOrderIds(@Param("orderIds") List<String> orderIds);

    // 查询 by orderId
    List<Cart> selectByOrderId(@Param("orderId") String orderId);

    // 查询 by materialName or method (二者至少一个不空)
    List<Cart> selectByMaterialOrMethod(@Param("material") String material,
                                        @Param("method") String method);

    /**
     * 获取指定 orderId 下的 total_price 之和
     */
    @Select("SELECT IFNULL(SUM(total_price), 0) FROM baoli.cart WHERE order_id = #{orderId}")
    Double sumTotalPriceByOrder(@Param("orderId") String orderId);
}
