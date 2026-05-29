package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.service.NoteService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.Date;
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

    @Override
    public Result updateNote(Note note) {
        boolean result = noteMapper.insertOrUpdate(note);
        if (!result) {
            return Result.failure(result);
        }
        // 判断是否为插入
        if (note.getCreateTime() == null) {
            LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
            wrapper.eq(Note::getId, note.getId());
            Note note1 = noteMapper.selectOne(wrapper);
            if (note1 != null) {
                return Result.success(note1);
            }
            return Result.failure("");
        }
        return Result.success(result);
    }

    @Override
    public Result listNewNote(Long userId) {
        Page<Note> notePage = new Page<>(1, 10);
        LambdaQueryWrapper<Note> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Note::getUserId, userId)
                .orderByDesc(Note::getUpdateTime);
        List noteList = noteMapper.selectList(notePage, lambdaQueryWrapper);
        if (!noteList.isEmpty()) {
            return Result.success(noteList);
        }
        return Result.failure("");
    }

    @Override
    public Result listStarNote(Long userId) {
        Page<Note> notePage = new Page<>(1, 10);
        LambdaQueryWrapper<Note> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.eq(Note::getUserId, userId)
                .eq(Note::getIsStar, 1)
                .orderByDesc(Note::getUpdateTime);
        List noteList = noteMapper.selectList(notePage, lambdaQueryWrapper);
        if (!noteList.isEmpty()) {
            return Result.success(noteList);
        }
        return null;
    }

    @Override
    public Result recycleNote(Long id) {
        Note note = noteMapper.selectById(id);
        if (note == null) {
            return Result.failure("笔记不存在");
        }

        // 2. 设置回收站状态（LocalDateTime 格式）
        note.setIsRecycle(1);           // 标记进入回收站
        note.setDeleteTime(LocalDateTime.now()); // 当前时间
        note.setRecycleExpireTime(LocalDateTime.now().plusDays(30)); // 30天后过期

        // 3. 执行更新（必须加！）
        noteMapper.updateById(note);
        return Result.success(1);
    }
}