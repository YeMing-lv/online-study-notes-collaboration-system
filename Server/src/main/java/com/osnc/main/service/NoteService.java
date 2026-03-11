package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Note;

import java.util.List;

/**
 * 学习笔记核心服务接口（MyBatis-Plus版）
 * 对应表：note（学习笔记核心表）
 * @author osnc
 */
public interface NoteService extends IService<Note> {

    // 自定义方法示例：根据用户ID和分类ID查询笔记
    List<Note> listByUserIdAndCategoryId(Long userId, Long categoryId);

    // 自定义方法示例：查询用户回收站中的笔记
    List<Note> listRecycleByUserId(Long userId);

    Result getNoteById(Long id);
}