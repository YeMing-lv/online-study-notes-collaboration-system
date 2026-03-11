package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.NoteTag;

import java.util.List;

/**
 * 笔记标签主表服务接口（MyBatis-Plus版）
 * 对应表：note_tag（笔记标签主表）
 * @author osnc
 */
public interface NoteTagService extends IService<NoteTag> {

    // 自定义方法示例：根据用户ID查询未删除的标签
    List<NoteTag> listValidByUserId(Long userId);
}