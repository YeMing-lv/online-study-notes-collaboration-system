package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.NoteTagRelationMapper; // 需自行创建Mapper接口
import com.osnc.main.pojo.dto.NoteTagRelation;
import com.osnc.main.service.NoteTagRelationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.stream.Collectors;

/**
 * 笔记-标签关联服务实现类（MyBatis-Plus版）
 * 对应表：note_tag_relation（笔记-标签关联表）
 * @author osnc
 */
@Service
public class NoteTagRelationServiceImpl extends ServiceImpl<NoteTagRelationMapper, NoteTagRelation> implements NoteTagRelationService {

    @Autowired
    private NoteTagRelationMapper noteTagRelationMapper;

    @Override
    public List<Long> listTagIdsByNoteId(Long noteId) {
        List<NoteTagRelation> relations = lambdaQuery()
                .eq(NoteTagRelation::getNoteId, noteId)
                .list();

        // 提取标签ID列表
        return relations.stream()
                .map(NoteTagRelation::getTagId)
                .collect(Collectors.toList());
    }
}