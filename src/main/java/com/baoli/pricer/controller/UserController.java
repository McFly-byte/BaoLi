package com.baoli.pricer.controller;

import com.baoli.pricer.service.UserService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Slf4j
@RestController
@RequestMapping("/api/user")
public class UserController {
    @Autowired
    private UserService userService;

    /**
     * 创建用户，密码自动 BCrypt 加密后入库。
     */
    @PostMapping("/create")
    public ResponseEntity<String> createUser(
            @RequestParam String username,
            @RequestParam String password) {
        try {
            userService.createUser(username, password);
            log.info("用户 {} 创建成功", username);
            return ResponseEntity.ok().build();
        } catch (IllegalArgumentException e) {
            log.error("创建用户失败: {}", e.getMessage());
            return ResponseEntity.badRequest().body(e.getMessage());
        } catch (Exception e) {
            log.error("创建用户时发生异常: {}", e.getMessage());
            return ResponseEntity.status(500).body("服务器内部错误");
        }

    }

}
