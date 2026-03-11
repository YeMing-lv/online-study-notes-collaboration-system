package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.Note;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习笔记核心Mapper
 * 对应表：note（学习笔记核心表）
 * @author osnc
 */
@Mapper
public interface NoteMapper extends BaseMapper<Note> {

    // 可扩展自定义SQL：如按用户+分类+时间范围查询笔记、统计笔记阅读量等
}