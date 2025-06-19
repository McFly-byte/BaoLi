package com.baoli.pricer.controller;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.service.MaterialService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/files")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    /**
     * 上传 Excel 并解析、存库、上传图片
     * @return 直接返回任务ID
     */
    @PostMapping(value = "/materials/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String,String>> importMaterials(@RequestParam("file") MultipartFile file) {
        // 1. 生成任务 ID
        String taskId = UUID.randomUUID().toString();
        // 2. 异步执行
        service.asyncImportMaterials(taskId, file);
        // 3. 立即返回给前端
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    /**
     * 分页获取材料列表
     * GET /api/files?page=1&size=20
     */
    @GetMapping(value="/page")
    public  ResponseEntity<PageInfo<Material>> page(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageInfo<Material> result = service.findALLByPage(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 模糊查询接口
     */
    @GetMapping("/blurSearch")
    public ResponseEntity<PageInfo<Material>> getByKeyword(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("keyword") String keyword) {
        PageInfo<Material> results = service.getByKeyword(page, size, keyword);
        return ResponseEntity.ok(results);
    }

    /**
     * GET /api/files/by-category?category=木饰面
     * 按材料品类查询列表
     */
    @GetMapping("/by-category")
    public ResponseEntity<PageInfo<Material>> getByCategory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("category") String category ) {
        PageInfo<Material> result = service.getByCategory(page, size, category);
        return ResponseEntity.ok(result);
    }

    /**
     *  查找所有不同的材料品类
     */
    @GetMapping("/categories")
    public ResponseEntity<PageInfo<String>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
            ) {
        PageInfo<String> result = service.getAllCategories(page, size);
        return ResponseEntity.ok(result);
    }

}

