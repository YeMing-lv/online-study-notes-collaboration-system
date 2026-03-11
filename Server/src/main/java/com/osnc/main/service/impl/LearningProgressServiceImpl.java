package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.LearningProgressMapper; // 需自行创建Mapper接口
import com.osnc.main.pojo.dto.LearningProgress;
import com.osnc.main.service.LearningProgressService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

/**
 * 学习进度服务实现类（MyBatis-Plus版）
 * 对应表：learning_progress（学习进度表）
 * @author osnc
 */
@Service
public class LearningProgressServiceImpl extends ServiceImpl<LearningProgressMapper, LearningProgress> implements LearningProgressService {

    @Autowired
    private LearningProgressMapper learningProgressMapper;

    @Override
    public LearningProgress getByUserAndTarget(Long userId, Integer targetType, Long targetId) {
        return lambdaQuery()
                .eq(LearningProgress::getUserId, userId)
                .eq(LearningProgress::getTargetType, targetType)
                .eq(LearningProgress::getTargetId, targetId)
                .one();
    }
}