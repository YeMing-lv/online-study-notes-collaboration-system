package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.NoteVersion;

import java.util.List;

/**
 * 笔记版本服务接口（MyBatis-Plus版）
 * 对应表：note_version（笔记版本表）
 * @author osnc
 */
public interface NoteVersionService extends IService<NoteVersion> {

    // 自定义方法示例：根据笔记ID查询版本历史（按版本号降序）
    List<NoteVersion> listByNoteIdOrderByVersionNumDesc(Long noteId);
}