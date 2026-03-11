package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.LearningProgress;

/**
 * 学习进度服务接口（MyBatis-Plus版）
 * 对应表：learning_progress（学习进度表）
 * @author osnc
 */
public interface LearningProgressService extends IService<LearningProgress> {

    // 自定义方法示例：根据用户ID、目标类型、目标ID查询进度
    LearningProgress getByUserAndTarget(Long userId, Integer targetType, Long targetId);
}