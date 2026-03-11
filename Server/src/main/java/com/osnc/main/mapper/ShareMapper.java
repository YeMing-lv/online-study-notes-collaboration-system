package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.Share;
import org.apache.ibatis.annotations.Mapper;

/**
 * 通用分享Mapper（文件/笔记）
 * 对应表：share（通用分享表）
 * @author osnc
 */
@Mapper
public interface ShareMapper extends BaseMapper<Share> {

    // 可扩展自定义SQL：如批量失效分享、查询用户有效分享数等
}