package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.NoteTagRelation;

import java.util.List;

/**
 * 笔记-标签关联服务接口（MyBatis-Plus版）
 * 对应表：note_tag_relation（笔记-标签关联表）
 * @author osnc
 */
public interface NoteTagRelationService extends IService<NoteTagRelation> {

    // 自定义方法示例：根据笔记ID查询关联的标签ID列表
    List<Long> listTagIdsByNoteId(Long noteId);
}