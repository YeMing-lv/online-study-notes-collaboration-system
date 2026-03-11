package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.User;
import org.apache.ibatis.annotations.Mapper;

/**
 * 系统用户Mapper
 * 对应表：user（系统用户表）
 * @author osnc
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // 基础CRUD由BaseMapper提供，可在此声明自定义SQL方法（如复杂联查、批量操作等）
}