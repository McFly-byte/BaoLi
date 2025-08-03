package com.baoli.pricer.mapper;

import com.baoli.pricer.pojo.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.util.List;

@Mapper
public interface UserMapper {
    int insert(User user);
    List<User> getAll();
    User getById(@Param("id") Long id);
    User getByUsername(@Param("username") String username);
    int updateById(User user);
    int deleteById(@Param("id") Long id);
}
