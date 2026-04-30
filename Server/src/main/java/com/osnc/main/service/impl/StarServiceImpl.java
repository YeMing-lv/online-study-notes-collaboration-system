package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.mapper.StarMapper;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.Share;
import com.osnc.main.pojo.dto.Star;
import com.osnc.main.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.List;

/**
 * 通用星标服务实现类（MyBatis-Plus版）
 * 对应表：star（通用星标表）
 * @author osnc
 */
@Service
public class StarServiceImpl extends ServiceImpl<StarMapper, Star> implements StarService {

    @Autowired
    private StarMapper starMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public List<Star> listValidByUserIdOrderByStarTimeDesc(Long userId) {
        return lambdaQuery()
                .eq(Star::getUserId, userId)
                .eq(Star::getIsCanceled, 0) // 仅查有效星标
                .orderByDesc(Star::getStarTime)
                .list();
    }

    @Override
    public boolean isStarred(Long userId, Integer targetType, Long targetId) {
        return lambdaQuery()
                .eq(Star::getUserId, userId)
                .eq(Star::getTargetType, targetType)
                .eq(Star::getTargetId, targetId)
                .eq(Star::getIsCanceled, 0)
                .exists();
    }

    @Override
    public Result listStarNote(Long userId) {
        Page<Star> page = new Page<>(1, 10);
        LambdaQueryWrapper<Star> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Star::getUserId, userId)
                .eq(Star::getIsCanceled, 0)
                .orderByDesc(Star::getUpdateTime);
        List<Star> stars = starMapper.selectList(page, wrapper);
        if (stars.isEmpty()) {
            return Result.success("");
        }

        List<Note> starList = new ArrayList<>();
        for (int i = 0; i < stars.size(); i++) {
            Note note = noteMapper.selectById(stars.get(i).getTargetId());
            if (note != null) starList.add(note);
        }

        return Result.success(starList);
    }
}