package com.baoli.pricer.controller;

import com.baoli.pricer.service.AuthService;
import lombok.Data;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/auth")
public class AuthController {
    private final AuthService authService;

    public AuthController(AuthService authService) {
        this.authService = authService;
    }

    @PostMapping("/login")
    public ResponseEntity<LoginOut> login(@RequestBody LoginIn in) {
        String token = authService.login(in.getUsername(), in.getPassword());
        return ResponseEntity.ok(new LoginOut(token, "Bearer"));
    }

    @Data
    public static class LoginIn {
        private String username;
        private String password;
    }

    @Data
    public static class LoginOut {
        private final String token;
        private final String tokenType;
    }
}