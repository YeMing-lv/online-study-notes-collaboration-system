package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.NoteVersion;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记版本Mapper（回溯编辑历史）
 * 对应表：note_version（笔记版本表）
 * @author osnc
 */
@Mapper
public interface NoteVersionMapper extends BaseMapper<NoteVersion> {

    // 可扩展自定义SQL：如查询笔记最新版本、批量回滚版本等
}