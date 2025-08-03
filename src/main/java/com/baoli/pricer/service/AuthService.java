package com.baoli.pricer.service;

import com.baoli.pricer.mapper.UserMapper;
import com.baoli.pricer.pojo.User;
import com.baoli.pricer.utils.JwtUtil;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Service
@RequiredArgsConstructor
public class AuthService {
    private final UserMapper userMapper;
    private final JwtUtil jwtUtil;
    private final BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public String login(String username, String rawPassword) {
        Assert.hasText(username, "用户名不能为空");
        Assert.hasText(rawPassword, "密码不能为空");
        User user = userMapper.getByUsername(username);
        if (user == null || !passwordEncoder.matches(rawPassword, user.getPassword())) {
            System.out.println("user:"+user);
            System.out.println("rawPassword:"+rawPassword);
            if ( user != null) {
                System.out.println("encodedPassword:"+user.getPassword());
            }
            throw new IllegalArgumentException("用户名或密码错误");
        }
        return jwtUtil.generateToken(user.getId(), user.getUsername());
    }
}
