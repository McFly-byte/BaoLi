package com.baoli.pricer.service;


import com.baoli.pricer.mapper.UserMapper;
import com.baoli.pricer.pojo.User;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.Assert;

@Slf4j
@RequiredArgsConstructor
@Service
public class UserService {

    private final UserMapper userMapper;
    private final PasswordEncoder passwordEncoder;

    /**
     * 创建用户，密码自动 BCrypt 加密后入库。
     */
    public void createUser(User user) {
        Assert.hasText(user.getUsername(), "请输入用户名");
        Assert.hasText(user.getPassword(), "请输入密码");
        String encoded = passwordEncoder.encode(user.getPassword());
        user.setPassword(encoded);
        userMapper.insert(user);
    }

    public void createUser(String username, String password) {
        Assert.hasText(username, "用户名不能为空");
        Assert.hasText(password, "密码不能为空");
        User user = new User();
        user.setUsername(username);
        user.setPassword(passwordEncoder.encode(password));
        userMapper.insert(user);
        log.info("用户 {} 创建成功", username);
    }


    /**
     * 修改密码，密码自动加密更新。
     */
    public void changePassword(Long userId, String rawPassword) {
        Assert.notNull(userId, "userId required");
        Assert.hasText(rawPassword, "请输入新密码");
        String encoded = passwordEncoder.encode(rawPassword);
        User user = userMapper.getById(userId);
        user.setPassword(encoded);
        userMapper.updateById(user);
    }


    public User getByUsername(String username) {
        return userMapper.getByUsername(username);
    }
}
