package com.baoli.pricer.init;

import com.baoli.pricer.pojo.User;
import com.baoli.pricer.service.UserService;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

@Component
public class DataInitializer implements CommandLineRunner {

    private final UserService userService;

    public DataInitializer(UserService userService) {
        this.userService = userService;
    }

    @Override
    public void run(String... args) {
        final String username = "admin";
        if (userService.getByUsername(username) == null) {
            User user = new User();
            user.setUsername(username);
            user.setPassword("Poly@001"); // 明文，createUser 会自动 BCrypt 编码
            userService.createUser(user);
            System.out.printf("init user id=%s, username=%s%n", user.getId(), user.getUsername());
        }
    }
}

