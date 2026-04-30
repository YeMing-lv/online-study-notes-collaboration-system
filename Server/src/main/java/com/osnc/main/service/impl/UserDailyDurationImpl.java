package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.UserDailyDurationMapper;
import com.osnc.main.pojo.dto.UserDailyDuration;
import com.osnc.main.service.UserDailyDurationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Repository;
import org.springframework.stereotype.Service;

import java.time.LocalDate;

@Service
public class UserDailyDurationImpl extends ServiceImpl<UserDailyDurationMapper, UserDailyDuration> implements UserDailyDurationService {

    @Autowired
    private UserDailyDurationMapper userDailyDurationMapper;

    @Override
    public Result reportDuration(Long userId, Integer seconds) {
        LocalDate today = LocalDate.now();

        LambdaQueryWrapper<UserDailyDuration> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(UserDailyDuration::getUserId, userId)
                .eq(UserDailyDuration::getStatDate, today);

        UserDailyDuration exist = userDailyDurationMapper.selectOne(wrapper);
        if (exist == null) {
            UserDailyDuration entity = new UserDailyDuration();
            entity.setUserId(userId);
            entity.setStatDate(today);
            entity.setTotalSeconds(seconds);
            save(entity);
        } else {
            exist.setTotalSeconds(exist.getTotalSeconds() + seconds);
            updateById(exist);
        }
        return null;
    }

    @Override
    public Result getYearDuration(Long userId) {
        return null;
    }
}
