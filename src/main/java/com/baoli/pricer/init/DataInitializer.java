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
        for (int i = 1; i <= 10; i++) {
            String username = String.format("admin%02d", i); // 格式化为admin01、admin02...admin10
            if (userService.getByUsername(username) == null) {
                User user = new User();
                user.setUsername(username);
                user.setPassword("Poly@001"); // createUser方法会自动BCrypt加密
                userService.createUser(user);
                System.out.printf("初始化用户 id=%s, username=%s%n", user.getId(), username);
            }
        }

    }
}

