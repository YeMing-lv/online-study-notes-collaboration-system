package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.User;
import com.osnc.main.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 系统用户控制器
 * 对应表：user（系统用户表）
 * @author osnc
 */
@RestController
@RequestMapping("/user")
@Slf4j
public class UserController {

    @Autowired
    private UserServiceImpl userService;

    /**
     * 注册方法
     * @param user
     */
    @PostMapping("/register")
    public Result registerUser(@RequestBody User user) {
        return userService.register(user);
    }

    /**
     * 登录方法
     * @param user
     */
    @PostMapping("/login")
    public Result userLogin(@RequestBody User user ) {
        return userService.login(user);
    }

    @PostMapping("/update")
    public Result updateUser(@RequestBody User user) {
        log.info(user+"");
        return userService.updateUserById(user);
    }
}