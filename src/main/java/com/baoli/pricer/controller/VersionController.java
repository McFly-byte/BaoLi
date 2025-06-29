package com.baoli.pricer.controller;

import com.baoli.pricer.mapper.VersionMapper;
import com.baoli.pricer.pojo.Version;
import com.baoli.pricer.service.VersionService;
import com.github.pagehelper.PageInfo;
import jakarta.servlet.http.HttpSession;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/version")
public class VersionController {

    @Autowired
    private VersionMapper versionMapper;

    @Autowired
    private VersionService versionService;

    @PostMapping("/activate/{versionId}")
    public ResponseEntity<Void> activateVersion(@PathVariable int versionId, HttpSession session) {
        // 先校验该 versionId 是否存在于 version 表中
        Version version = versionMapper.getById(versionId);
        if (version == null) {
            return ResponseEntity.badRequest().build(); // 或返回带错误信息的 ResponseEntity
        }

        session.setAttribute("versionId", versionId);
        return ResponseEntity.ok().build();
    }

    @PostMapping("/insert")
    public ResponseEntity<Integer> insert(
            @RequestParam byte flag,
            @RequestParam String versionName,
            @RequestParam String description) {
        // 调用 mapper 插入新版本
        int newVersionId = versionService.insert(flag, versionName, description);
        if (newVersionId > 0) {
            return ResponseEntity.ok(newVersionId);
        } else {
            return ResponseEntity.status(500).build(); // 插入失败
        }
    }

    @GetMapping("/getById/{id}")
    public ResponseEntity<Version> getById(@PathVariable int id) {
        Version version = versionService.getById(id);
        if (version != null) {
            return ResponseEntity.ok(version);
        } else {
            return ResponseEntity.notFound().build(); // 未找到版本
        }
    }

    @GetMapping("/getByVersionName")
    public ResponseEntity<Version> getByVersionName(@RequestParam String versionName) {
        Version version = versionService.getByVersionName(versionName);
        if (version != null) {
            return ResponseEntity.ok(version);
        } else {
            return ResponseEntity.notFound().build(); // 未找到版本
        }
    }

    @GetMapping("/getByFlag")
    public ResponseEntity<PageInfo<Version>> getByFlag(
            @RequestParam int page,
            @RequestParam int size,
            @RequestParam byte flag) {
        PageInfo<Version> versions = versionService.getByFlag(page, size, flag);
        return ResponseEntity.ok(versions);
    }

    @GetMapping("/getAll")
    public ResponseEntity<PageInfo<Version>> getAll(
            @RequestParam(value = "page", defaultValue = "1") int page,
            @RequestParam(value = "size", defaultValue = "10") int size) {
        PageInfo<Version> versions = versionService.getAll(page, size);
        return ResponseEntity.ok(versions);
    }

    @PutMapping("/updateById")
    public ResponseEntity<Boolean> updateById(
            @RequestParam int id,
            @RequestParam String versionName,
            @RequestParam String description) {
        boolean updated = versionService.updateById(id, versionName, description);
        if (updated) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(500).body(false); // 更新失败
        }
    }

    @DeleteMapping("/deleteById/{id}")
    public ResponseEntity<Boolean> deleteById(@PathVariable int id) {
        boolean deleted = versionService.deleteById(id);
        if (deleted) {
            return ResponseEntity.ok(true);
        } else {
            return ResponseEntity.status(500).body(false); // 删除失败
        }
    }



}
