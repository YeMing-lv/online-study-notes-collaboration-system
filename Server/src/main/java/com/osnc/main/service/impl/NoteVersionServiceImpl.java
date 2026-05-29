package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.mapper.NoteVersionMapper;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.NoteVersion;
import com.osnc.main.service.NoteVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class NoteVersionServiceImpl extends ServiceImpl<NoteVersionMapper, NoteVersion> implements NoteVersionService {

    @Autowired
    private NoteVersionMapper noteVersionMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public Result listByNoteIdOrderByVersionNumDesc(Long noteId) {
        List<NoteVersion> resultList = lambdaQuery().eq(NoteVersion::getNoteId, noteId)
                .orderByDesc(NoteVersion::getCreateTime)
                .list();
        return Result.success(resultList);
    }

    @Override
    public Result saveNoteVersion(NoteVersion noteVersion) {
        Note note = noteMapper.selectById(noteVersion.getNoteId());
        if ( note == null ) {
            return Result.failure("错误的笔记ID");
        }
        boolean result = noteVersionMapper.insertOrUpdate(noteVersion);
        if (!result) {
            return Result.failure(result);
        }
        return Result.success(result);
    }
}