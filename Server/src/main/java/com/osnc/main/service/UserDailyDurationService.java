package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.UserDailyDuration;

public interface UserDailyDurationService extends IService<UserDailyDuration> {
    Result reportDuration(Long userId, Integer seconds);

    Result getYearDuration(Long userId);
}
