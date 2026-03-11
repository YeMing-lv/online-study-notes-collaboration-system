package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.NoteTag;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记标签主表Mapper
 * 对应表：note_tag（笔记标签主表）
 * @author osnc
 */
@Mapper
public interface NoteTagMapper extends BaseMapper<NoteTag> {

    // 可扩展自定义SQL：如按标签名称模糊查询、统计标签关联笔记数等
}