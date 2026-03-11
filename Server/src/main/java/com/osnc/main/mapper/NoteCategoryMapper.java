package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.NoteCategory;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记分类Mapper
 * 对应表：note_category（笔记分类表）
 * @author osnc
 */
@Mapper
public interface NoteCategoryMapper extends BaseMapper<NoteCategory> {

    // 可扩展自定义SQL：如查询用户分类树（递归查询子分类）
}