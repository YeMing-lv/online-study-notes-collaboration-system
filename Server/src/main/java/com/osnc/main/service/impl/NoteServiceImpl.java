package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 学习笔记核心服务实现类（MyBatis-Plus版）
 * 对应表：note（学习笔记核心表）
 * @author osnc
 */
@Service
public class NoteServiceImpl extends ServiceImpl<NoteMapper, Note> implements NoteService {

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public List<Note> listByUserIdAndCategoryId(Long userId, Long categoryId) {
        return lambdaQuery()
                .eq(Note::getUserId, userId)
                .eq(Note::getCategoryId, categoryId)
                .eq(Note::getIsDeleted, 0) // 排除逻辑删除的笔记
                .list();
    }

    @Override
    public List<Note> listRecycleByUserId(Long userId) {
        return lambdaQuery()
                .eq(Note::getUserId, userId)
                .eq(Note::getIsRecycle, 1) // 仅查回收站中的笔记
                .list();
    }

    @Override
    public Result getNoteById(Long id) {
        Note result = noteMapper.selectById(id);
        if (result == null) {
            return Result.failure("没有改笔记");
        }
        return Result.success(result);
    }
}