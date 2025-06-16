package com.baoli.pricer.controller;

import com.baoli.pricer.dto.PageResult;
import com.baoli.pricer.pojo.Material;
import com.baoli.pricer.pojo.ProcessMethod;
import com.baoli.pricer.service.MethodService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.List;

@Slf4j
@RestController
@RequestMapping("/api/method")
@RequiredArgsConstructor
public class MethodController {

    private final MethodService service;

    /**
     * 上传 .xlsx 并导入到 baoli.process_method
     * POST /api/method/import
     */
    @PostMapping(value = "/import", consumes = MediaType.MULTIPART_FORM_DATA_VALUE)
    public ResponseEntity<String> importMethods(@RequestParam("file") MultipartFile file) {
        try {
            int count = service.importMethods(file);
            return ResponseEntity.ok("成功导入 " + count + " 条工艺记录");
        } catch (Exception e) {
            log.error("导入工艺失败", e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("导入失败：" + e.getMessage());
        }
    }

    /**
     * 分页获取材料列表
     */
    @GetMapping(value="/page")
    public ResponseEntity<PageResult<ProcessMethod>> page(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageResult<ProcessMethod> result = service.list(page, size);
        return ResponseEntity.ok(result);
    }

    /**
     * 模糊查询接口
     * GET /api/method/blurSearch?keyword=xxx
     */
    @GetMapping("/blurSearch")
    public ResponseEntity<List<ProcessMethod>> searchMethods(@RequestParam("keyword") String keyword) {
        List<ProcessMethod> results = service.searchMethods(keyword);
        return ResponseEntity.ok(results);
    }
}
