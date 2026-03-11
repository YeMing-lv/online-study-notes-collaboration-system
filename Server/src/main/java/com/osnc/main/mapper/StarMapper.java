package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.Star;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用星标Mapper（文件/笔记）
 * 对应表：star（通用星标表）
 * @author osnc
 */
@Mapper
public interface StarMapper extends BaseMapper<Star> {

    // 可扩展自定义SQL：如取消用户所有星标、按星标备注模糊查询等
}