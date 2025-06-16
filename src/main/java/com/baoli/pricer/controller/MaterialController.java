package com.baoli.pricer.controller;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.service.MaterialService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;


@Slf4j
@RestController
@RequestMapping("/api/files")
public class MaterialController {

    private final MaterialService materialService;

    public MaterialController(MaterialService materialService) {
        this.materialService = materialService;
    }

    /**
     * 上传 Excel 并解析、存库、上传图片
     */
    @PostMapping(value = "/materials/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<?> importMaterials(@RequestParam("file") MultipartFile file) {
        try {
            int count = materialService.importMaterials(file);
            return ResponseEntity.ok("成功导入 " + count + " 条材料");
        } catch (Exception e) {
            log.error("导入失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("导入失败：" + e.getMessage());
        }
    }

    /**
     * 分页获取材料列表
     * GET /api/files?page=1&size=20
     */
    @GetMapping(value="/page")
    public ResponseEntity<PageResult<Material>> page(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResult<Material> result = materialService.list(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 模糊查询接口
     */
    @GetMapping("/blurSearch")
    public ResponseEntity<List<Material>> searchMethods(@RequestParam("keyword") String keyword) {
        List<Material> results = materialService.searchMethods(keyword);
        return ResponseEntity.ok(results);
    }


//    // 接收并上传单张图片
//    @PostMapping("/upload")
//    public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
//        try {
//            String fileUrl = fileService.uploadFile(file);
//            return ResponseEntity.ok(fileUrl);
//        } catch (Exception e) {
//            return ResponseEntity.status(500).body("上传失败：" + e.getMessage());
//        }
//    }
}

