package com.baoli.pricer.service;

import com.baoli.pricer.dto.CartExportModel;
import com.baoli.pricer.mapper.CartMapper;
import com.baoli.pricer.mapper.MaterialMapper;
import com.baoli.pricer.mapper.MethodMapper;
import com.baoli.pricer.pojo.Cart;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.github.pagehelper.PageHelper;
import com.github.pagehelper.PageInfo;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
public class CartService {

    private final CartMapper cartMapper;
    private final MaterialMapper materialMapper;
    private final MethodMapper methodMapper;

    /**
     * 单条添加：Cart
     */
    @Transactional
    public void addItem(Cart cart) {
        cartMapper.addItem(cart);
    }

    /**
     * 单挑添加
     */
    @Transactional
    public void addItem(Integer materialId, Integer methodId, Integer quantity, String orderId ) {
        // 1. 查询原表获取冗余数据
        Material mat = materialMapper.getById(materialId);
        ProcessMethod pm = methodMapper.findById(methodId);

        // 2. 构造 Cart 对象
        Cart cart = new Cart();
        cart.setMaterialId(materialId);
        cart.setMethodId(methodId);
        cart.setMaterialCategory(mat.getMaterialCategory());
        cart.setMaterialName(mat.getMaterialName());
        cart.setMaterialPrice(mat.getPrice());
        cart.setMethod(pm.getMethod());
        cart.setMethodPrice(pm.getPrice());
        cart.setQuantity(quantity == null ? 1 : quantity);
        cart.setOrderId(orderId);

        // 3. 插入数据库
        cartMapper.addItem(cart);
        // total_price, created_at, updated_at 均由数据库自动计算/填充
    }

    /**
     * 批量添加：传入同一 orderId 下的多条数据
     * 每条传 materialId, methodId, quantity
     */
    @Transactional
    public int addCartItems(List<Cart> items) {
        // 1. 收集所有 ID 去重
        Set<Integer> materialIds = items.stream()
                .map(Cart::getMaterialId).collect(Collectors.toSet());
        Set<Integer> methodIds   = items.stream()
                .map(Cart::getMethodId).collect(Collectors.toSet());

        // 2. 一次性批量查询
        List<Material> mats = materialMapper.getByIds(new ArrayList<>(materialIds));
        List<ProcessMethod> pms = methodMapper.findByIds(new ArrayList<>(methodIds));

        // 3. 转成 Map
        Map<Integer, Material> matMap = mats.stream()
                .collect(Collectors.toMap(Material::getId, Function.identity()));
        Map<Integer, ProcessMethod> pmMap = pms.stream()
                .collect(Collectors.toMap(ProcessMethod::getId, Function.identity()));

        // 4. 填充并批量插入
        items.forEach(c -> {
            Material mat = matMap.get(c.getMaterialId());
            ProcessMethod pm = pmMap.get(c.getMethodId());
            c.setMaterialCategory(mat.getMaterialCategory());
            c.setMaterialName(mat.getMaterialName());
            c.setMaterialPrice(mat.getPrice());
            c.setMethod(pm.getMethod());
            c.setMethodPrice(pm.getPrice());
            if (c.getQuantity() == null) c.setQuantity(1);
        });
        return cartMapper.insertBatch(items);
    }


    public int removeById(Integer id) {
        return cartMapper.deleteById(id);
    }

    public int removeByOrderId(String orderId) {
        return cartMapper.deleteByOrderId(orderId);
    }

    public int removeByIds(List<Integer> ids) {
        return cartMapper.deleteBatchIds(ids);
    }

    public int removeByOrderIds(List<String> orderIds) {
        return cartMapper.deleteBatchOrderIds(orderIds);
    }

    public PageInfo<Cart> getByOrderId(String orderId, Integer page, Integer size) {
        PageHelper.startPage(page, size);
        List<Cart> list = cartMapper.selectByOrderId(orderId);
        return new PageInfo<>(list);
    }

    public PageInfo<Cart> getByMaterialOrMethod(String material, String method, int page, int size) {
        PageHelper.startPage(page, size);
        List<Cart> list = cartMapper.selectByMaterialOrMethod(material, method);
        return new PageInfo<>(list);
    }

    /**
     * 导出指定 orderId 的购物车数据为 Excel
     * @param orderId 订单号
     * @return List<CartExportModel> 用于写 Excel
     */
    public List<CartExportModel> exportByOrder(String orderId) {
        List<Cart> carts = cartMapper.selectByOrderId(orderId);
        return carts.stream().map(c -> new CartExportModel(
                c.getMaterialCategory(),
                c.getMaterialName(),
                c.getMaterialPrice(),
                c.getUseplace(),
                c.getMethod(),
                c.getMethodPrice(),
                c.getQuantity(),
                c.getTotalPrice(),
                c.getOrderId()
        )).collect(Collectors.toList());
    }

    /**
     * 计算指定 orderId 下购物车所有项的总价
     */
    public Double calculateOrderTotal(String orderId) {
        return cartMapper.sumTotalPriceByOrder(orderId);
    }
}

