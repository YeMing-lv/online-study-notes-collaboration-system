package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.User;
import com.osnc.main.pojo.dto.UserDailyDuration;
import com.osnc.main.service.impl.UserDailyDurationImpl;
import com.osnc.main.service.impl.UserServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

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

    @Autowired
    private UserDailyDurationImpl userDailyDurationService;

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

    /**
     * 上报使用时长（秒）
     */
    @PostMapping("/report")
    public Result reportDuration(@RequestParam Long userId,@RequestParam Integer seconds) {
        return userDailyDurationService.reportDuration(userId, seconds);
    }

    /**
     * 查询近一年使用时长（日历热图用）
     */
    @GetMapping("/year")
    public Result yearDuration(@RequestParam Long userId) {
        return userDailyDurationService.getYearDuration(userId);
    }
}