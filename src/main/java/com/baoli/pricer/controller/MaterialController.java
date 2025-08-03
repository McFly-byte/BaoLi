package com.baoli.pricer.controller;

import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.service.MaterialService;
import com.github.pagehelper.PageInfo;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;
import java.util.UUID;


@Slf4j
@RestController
@RequestMapping("/api/materials")
public class MaterialController {

    private final MaterialService service;

    public MaterialController(MaterialService service) {
        this.service = service;
    }

    /**
     * 上传 Excel 并解析、存库、上传图片
     * @return 直接返回任务ID
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<Map<String, String>> importMaterials(@RequestParam("file") MultipartFile file) throws IOException {
        // 1. 生成任务 ID
        String taskId = UUID.randomUUID().toString();

        // 2. 在一个临时目录里，以原始文件名创建临时文件
        String originalFilename = file.getOriginalFilename();
        if (originalFilename == null || originalFilename.isBlank()) {
            originalFilename = "材料表.xlsx";
        }
        // .createTempDirectory 保证了隔离，不会和别的进程/线程冲突
        Path tempDir = Files.createTempDirectory("import_");
        // .resolve 会把原始文件名当作最终文件名
        Path tempFile = tempDir.resolve(originalFilename);
        file.transferTo(tempFile.toFile());

        System.out.println("临时文件路径: " + tempFile.toAbsolutePath());
        // 3. 异步执行，Service 层拿到的 tempFile.getFileName() 就是 “原始文件名.xlsx”
        service.asyncImportMaterials(taskId, tempFile);

        // 4. 立即返回
        return ResponseEntity.ok(Map.of("taskId", taskId));
    }

    /**
     * 分页获取材料列表
     */
    @GetMapping(value = "/getAll")
    public ResponseEntity<PageInfo<Material>> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size
    ){
        PageInfo<Material> result = service.getAll(page, size);
        return ResponseEntity.ok(result);
    }

    /** 根据材料名称模糊查询 */
    @GetMapping("/by-name")
    public ResponseEntity<PageInfo<Material>> getByMaterialName(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("name") String name
    ) {
        if (!StringUtils.hasText(name)) { // 如果名称为空，返回400错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        PageInfo<Material> result = service.getByMaterialName(page, size, name);
        return ResponseEntity.ok(result);
    }

    /** 根据材料品类模糊查询 */
    @GetMapping("/by-category")
    public ResponseEntity<PageInfo<Material>> getByMaterialCategory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("category") String category) {
        if (!StringUtils.hasText(category)) { // 如果品类为空，返回400错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        PageInfo<Material> result = service.getByMaterialCategory(page, size, category);
        return ResponseEntity.ok(result);
    }

    /**
     * 确定了<材料品类>就模糊查找<材料名>，否则就模糊查找<材料品类>
     */
    @GetMapping("/by-keyword")
    public ResponseEntity<PageInfo<Material>> getByKeyword(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "keyword", required = false) String keyword,
            @RequestParam(value = "category", required = false) String category) {
        PageInfo<Material> result = service.getByKeyword(page, size, keyword, category);
        return ResponseEntity.ok(result);
    }

    /**
     * 大类 + 小类 + 材料名 查询
     */
    @GetMapping("/by-triple")
    public ResponseEntity<PageInfo<Material>> getByTriple(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam(value = "bigCategory", required = false) String bigCategory,
            @RequestParam(value = "category",required = false) String category,
            @RequestParam(value = "name",required = false) String name) {
        PageInfo<Material> result = service.getByTriple(page, size, bigCategory, category, name);
        return ResponseEntity.ok(result);
    }


    /**
     *  查找所有不同的材料品类
     */
    @GetMapping("/categories")
    public ResponseEntity<PageInfo<String>> getAllCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageInfo<String> categories = service.getAllCategories(page, size);
        return ResponseEntity.ok(categories);
    }

    /**
     * 根据材料大类查找所有不同的材料品类
     */
    @GetMapping("/categories/by-big-category")
    public ResponseEntity<PageInfo<String>> getAllCategoriesByBigCategory(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("bigCategory") String bigCategory) {
        if (!StringUtils.hasText(bigCategory)) { // 如果大类为空，返回400错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        PageInfo<String> categories = service.getAllCategoriesByBigCategory(page, size, bigCategory);
        return ResponseEntity.ok(categories);
    }

    /**
     * 查找所有不同的材料大类
     */
    @GetMapping("/big-categories")
    public ResponseEntity<PageInfo<String>> getAllBigCategories(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageInfo<String> bigCategories = service.getAllBigCategories(page, size);
        return ResponseEntity.ok(bigCategories);
    }

    /**
     * 在description字段中模糊查询
     */
    @GetMapping("/by-description")
    public ResponseEntity<PageInfo<Material>> getByDescription(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size,
            @RequestParam("keyword") String keyword) {

        log.info("keyword: {}", keyword);
        if (!StringUtils.hasText(keyword)) { // 如果描述为空，返回400错误
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
        PageInfo<Material> result = service.getByDescription(page, size, keyword);
        return ResponseEntity.ok(result);
    }

}

