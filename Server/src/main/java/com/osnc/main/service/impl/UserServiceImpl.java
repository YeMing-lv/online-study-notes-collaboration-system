package com.osnc.main.service.impl;

import cn.hutool.core.util.IdUtil;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.UserMapper;
import com.osnc.main.pojo.dto.User;
import com.osnc.main.pojo.vo.UserVO;
import com.osnc.main.service.UserService;
import com.osnc.main.utils.JWTUtils;
import jakarta.annotation.Resource;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;

import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.HashMap;
import java.util.Map;

/**
 * 系统用户服务实现类（MyBatis-Plus版）
 * 对应表：user（系统用户表）
 * @author osnc
 */
@Service
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService, UserDetailsService {

    @Autowired
    private UserMapper userMapper;

    @Autowired
    private PasswordEncoder passwordEncoder;

    @Resource
    AuthenticationManager authenticationManager;

    /**
     * 注册方法
     */
    @Override
    public Result register(User user) {
        // 先对密码加密
        String password = user.getPassword();
        String encryptedPassword = passwordEncoder.encode(password);
        user.setPassword(encryptedPassword);

        // 1.创建条件查询构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 2.构造查询条件
        queryWrapper.eq("username", user.getUsername());
        queryWrapper.eq("password", user.getPassword());
        // 3.查询 查询结果要么是user对象，要么为空
        User queryResult = userMapper.selectOne(queryWrapper);
        // 4.判空
        log.info("查询注册");
        if (queryResult != null) {
            return Result.failure("用户已存在");
        }
        // 5.雪花算法生成ID，执行插入
//        user.setId(IdUtil.getSnowflakeNextId());
        int row = userMapper.insert(user);
        if (row > 0) {
            return Result.success("用户注册成功", null);
        }
        return Result.failure();
    }

    /**
     * 登录方法
     *
     * @param user
     * @return
     */
    @Override
    public Result login(User user) {
        UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(user.getUsername(), user.getPassword());
        Authentication authentication = authenticationManager.authenticate(auth);

        // 存储验证成功的用户信息
        SecurityContextHolder.getContext().setAuthentication(authentication);

        UserDetails userDetails = (UserDetails) authentication.getPrincipal();

        // 1.创建条件查询构造器
        QueryWrapper<User> queryWrapper = new QueryWrapper();
        // 2.构造查询条件
        queryWrapper.eq("username", user.getUsername());
        //queryWrapper.eq("password", user.getPassword()); // 因为注册对密码加密过了 所以只根据姓名查询
        // 3.查询 查询结果要么是user对象，要么为空
        User queryResult = userMapper.selectOne(queryWrapper);
        // 4.判空
        if (queryResult == null) {
            return Result.failure("用户未注册");
        } else {
            //前台用户输入的明文密码
            String password = user.getPassword();
            //数据库中的密文密码
            String pwd = queryResult.getPassword();
            //第一个参数是前台的明文密码  第二个参数是数据库中的密文密码
            //它接收原始密码和已经加密过的密码作为参数，然后对原始密码进行加密，所以数据库表中 的密码一定要是密文 否则登录失败
            if (!passwordEncoder.matches(password, pwd)) {
                return Result.failure("密码不正确");
            }
        }
        // 5.生成token？？？
        String token = JWTUtils.createToken(user.getUsername());

        // 6.查询登录用户信息
        User logUser = userMapper.selectOne(queryWrapper);
        logUser.setPassword("");
        UserVO resultUser = new UserVO(logUser);
        Map resultMap = new HashMap<>();
        resultMap.put("user", resultUser);
        resultMap.put("token", token);
        return Result.success("登录成功", resultMap);
    }

    /**
     * 更新用户信息
     * @param user
     * @return
     */
    @Override
    public Result updateUserById(User user) {
        log.info("impl:"+user.getId());
        User findUser = userMapper.selectById(user.getId());
        if (findUser == null) {
            return Result.failure();
        }
        log.info(findUser+"");
        int row = userMapper.updateById(user);
        if (row > 0) {
            return Result.success(row);
        }
        return Result.failure();
    }

    /**
     * 根据用户名查询用户
     * @param username 登录用户名
     * @return 用户实体
     */
    @Override
    public User getByUsername(String username) {
        // 示例：使用MyBatis-Plus的LambdaQueryWrapper查询
        return lambdaQuery().eq(User::getUsername, username).one();
    }

    @Override
    public User getByUserId(Long id) {
        return lambdaQuery().eq(User::getId, id).one();
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        LambdaQueryWrapper<User> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(User::getUsername, username);
        try {
            User user = userMapper.selectOne(wrapper);//这里要确保userAccount是唯一的
            return user;
        } catch (Exception e) {
            log.error("没有该用户");
            return null;
        }
    }

}