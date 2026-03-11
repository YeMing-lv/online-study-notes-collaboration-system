package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.NoteTagRelation;
import org.apache.ibatis.annotations.Mapper;

/**
 * 笔记-标签关联Mapper
 * 对应表：note_tag_relation（笔记-标签关联表）
 * @author osnc
 */
@Mapper
public interface NoteTagRelationMapper extends BaseMapper<NoteTagRelation> {

    // 可扩展自定义SQL：如批量绑定标签、根据标签ID查询关联笔记等
}