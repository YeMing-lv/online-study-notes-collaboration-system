package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.Star;

import java.util.List;

/**
 * 通用星标服务接口（MyBatis-Plus版）
 * 对应表：star（通用星标表）
 * @author osnc
 */
public interface StarService extends IService<Star> {

    // 自定义方法示例：查询用户有效星标的资源（按加星时间降序）
    List<Star> listValidByUserIdOrderByStarTimeDesc(Long userId);

    // 自定义方法示例：判断用户是否已给目标资源加星
    boolean isStarred(Long userId, Integer targetType, Long targetId);
}