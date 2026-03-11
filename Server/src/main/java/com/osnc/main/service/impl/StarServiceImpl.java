package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.StarMapper;
import com.osnc.main.pojo.dto.Star;
import com.osnc.main.service.StarService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
}