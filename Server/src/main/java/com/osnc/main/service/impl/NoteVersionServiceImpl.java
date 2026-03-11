package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.NoteVersionMapper;
import com.osnc.main.pojo.dto.NoteVersion;
import com.osnc.main.service.NoteVersionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 笔记版本服务实现类（MyBatis-Plus版）
 * 对应表：note_version（笔记版本表）
 * @author osnc
 */
@Service
public class NoteVersionServiceImpl extends ServiceImpl<NoteVersionMapper, NoteVersion> implements NoteVersionService {

    @Autowired
    private NoteVersionMapper noteVersionMapper;

    @Override
    public List<NoteVersion> listByNoteIdOrderByVersionNumDesc(Long noteId) {
        return lambdaQuery()
                .eq(NoteVersion::getNoteId, noteId)
                .orderByDesc(NoteVersion::getVersionNum)
                .list();
    }
}