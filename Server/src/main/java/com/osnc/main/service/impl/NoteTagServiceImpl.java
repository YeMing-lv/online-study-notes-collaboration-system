package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.NoteTagMapper;
import com.osnc.main.pojo.dto.NoteTag;
import com.osnc.main.service.NoteTagService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 笔记标签主表服务实现类（MyBatis-Plus版）
 * 对应表：note_tag（笔记标签主表）
 * @author osnc
 */
@Service
public class NoteTagServiceImpl extends ServiceImpl<NoteTagMapper, NoteTag> implements NoteTagService {

    @Autowired
    private NoteTagMapper noteTagMapper;

    @Override
    public List<NoteTag> listValidByUserId(Long userId) {
        return lambdaQuery()
                .eq(NoteTag::getUserId, userId)
                .eq(NoteTag::getIsDeleted, 0) // 排除逻辑删除的标签
                .list();
    }
}