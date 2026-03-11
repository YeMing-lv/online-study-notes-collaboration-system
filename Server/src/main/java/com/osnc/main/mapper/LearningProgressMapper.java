package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.LearningProgress;
import org.apache.ibatis.annotations.Mapper;

/**
 * 学习进度Mapper
 * 对应表：learning_progress（学习进度表）
 * @author osnc
 */
@Mapper
public interface LearningProgressMapper extends BaseMapper<LearningProgress> {

    // 可扩展自定义SQL：如统计用户学习总进度、按目标类型分组查询进度等
}