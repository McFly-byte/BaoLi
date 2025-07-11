package com.baoli.pricer.controller;

import com.alibaba.excel.EasyExcel;
import com.baoli.pricer.dto.CartExportModel;
import com.baoli.pricer.pojo.Cart;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.service.CartService;
import com.baoli.pricer.utils.SummaryCellStyleHandler;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jetbrains.annotations.NotNull;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.io.IOException;
import java.io.Serializable;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.List;
import java.util.Map;

@Slf4j
@RestController
@RequestMapping("/api/cart")
@RequiredArgsConstructor
public class CartController {

    private final CartService cartService;

    /** 单条添加 **/
    @PostMapping("/item")
    public ResponseEntity<Integer> addItem( @RequestBody Cart cart ) {
//        System.out.println(cart.toString());
        log.info( cart.toString() );
        cartService.addItem(cart);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 单条添加 (测试用) **/
    @PostMapping("/item-test")
    public ResponseEntity<Integer> addItemTest( @RequestParam Integer materialId,
                                             @RequestParam Integer methodId,
                                             @RequestParam Integer quantity,
                                             @RequestParam String orderId ) {
        // 1. 检查参数是否有效
        if (materialId == null || methodId == null || quantity == null || orderId == null) {
            return ResponseEntity.badRequest().body(null);
        }
        cartService.addItem(materialId, methodId, quantity, orderId);
        return ResponseEntity.status(HttpStatus.CREATED).build();
    }

    /** 批量添加 **/
    @PostMapping("/batch")
    public ResponseEntity<Integer> addBatch(@RequestBody List<Cart> items) {
        int inserted = cartService.addCartItems(items);
        return ResponseEntity.ok(inserted);
    }

    /** 单条删除 by id **/
    @DeleteMapping("/item/{id}")
    public ResponseEntity<Integer> deleteById(@PathVariable Integer id) {
        return ResponseEntity.ok(cartService.removeById(id));
    }

    /** 单条删除 by orderId **/
    @DeleteMapping("/order/{orderId}")
    public ResponseEntity<Integer> deleteByOrder(@PathVariable String orderId) {
        return ResponseEntity.ok(cartService.removeByOrderId(orderId));
    }

    /** 批量删除 by ids **/
    @DeleteMapping("/batch/ids")
    public ResponseEntity<Integer> deleteByIds(@RequestBody List<Integer> ids) {
        return ResponseEntity.ok(cartService.removeByIds(ids));
    }

    /** 批量删除 by orderIds **/
    @DeleteMapping("/batch/orders")
    public ResponseEntity<Integer> deleteByOrderIds(@RequestBody List<String> orderIds) {
        return ResponseEntity.ok(cartService.removeByOrderIds(orderIds));
    }

    /** 查询 by orderId **/
    @GetMapping("/order/{orderId}")
    public ResponseEntity<PageInfo<Cart>> getByOrderId(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @PathVariable String orderId) {
        PageInfo<Cart> results = cartService.getByOrderId(orderId, page, size);
        return ResponseEntity.ok(results);
    }

    /** 查询 by materialName 或 method **/
    @GetMapping("/search")
    public ResponseEntity<PageInfo<Cart>> search(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "material", required = false) String material,
            @RequestParam(value = "method", required = false) String method) {
        PageInfo<Cart> result = cartService.getByMaterialOrMethod(material, method, page, size);

        return ResponseEntity.ok(
                result
        );
    }

    /**
     * 导出 Excel：根据 orderId 下载 .xlsx 文件
     */
    // 2. CartController 中的导出方法
    @GetMapping("/export/{orderId}")
    public void exportByOrder(@PathVariable String orderId,
                              HttpServletResponse response) throws IOException {

        // 1) 生成带时间戳的文件名
        String timestamp = LocalDateTime.now()
                .format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String filename = URLEncoder.encode(
                "Cart_" + orderId + "_" + timestamp + ".xlsx",
                StandardCharsets.UTF_8
        );
        response.setContentType(
                "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet"
        );
        response.setCharacterEncoding("utf-8");
        response.setHeader(
                "Content-Disposition",
                "attachment;filename*=utf-8''" + filename
        );

        // 2) 拿到导出数据
        List<CartExportModel> data = cartService.exportByOrder(orderId);

        // 3) 计算总价和，追加“合计”行
        double sum = data.stream()
                .mapToDouble(CartExportModel::getTotalPrice)
                .sum();
        // 合计行：只在 totalPrice 字段显示数字，其它列留空或显示“合计”
        CartExportModel summary = new CartExportModel();
        summary.setMaterialCategory("合计");
        summary.setTotalPrice(sum);
//        summary.setOrderId(orderId);
        data.add(summary);

        // 4) 确定“合计”行的行号和“总价”列的索引
        //    Excel 首行是表头，EasyExcel 默认把表头计为 rowIndex=0
        int summaryRowIndex = data.size(); // 数据行数 + 1，因为 data 包含 summary
        //    CartExportModel 中 @ExcelProperty 的顺序决定列索引
        //    按示例：0=材料品类,...,6=总价,7=订单号  => totalPrice 在第 6 列
        int totalPriceColIndex = 6;

        // 5) 写入 Excel，并注册上色 Handler
        EasyExcel.write(response.getOutputStream(), CartExportModel.class)
                .sheet("购物车-" + orderId)
                .registerWriteHandler(
                        new SummaryCellStyleHandler(summaryRowIndex, totalPriceColIndex)
                )
                .doWrite(data);
    }

    /**
     * GET /api/cart/total/{orderId}
     * 返回指定 orderId 下购物车的总价
     */
    @GetMapping("/total/{orderId}")
    public ResponseEntity<Map<String, Serializable>> getOrderTotal(@PathVariable String orderId) {
        Double total = cartService.calculateOrderTotal(orderId);
        return ResponseEntity.ok(Map.of("orderId", orderId, "totalPrice", total));
    }


}

