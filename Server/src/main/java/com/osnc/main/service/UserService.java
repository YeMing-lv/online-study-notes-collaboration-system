package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.User;

/**
 * 系统用户服务接口（MyBatis-Plus版）
 * 对应表：user（系统用户表）
 * @author osnc
 */
public interface UserService extends IService<User> {

    // 基础CRUD方法已由IService提供，此处可声明自定义业务方法
    // 示例：根据用户名查询用户
    User getByUsername(String username);

    User getByUserId(Long id);

    Result register(User user);

    Result login(User user);

    Result updateUserById(User user);


}