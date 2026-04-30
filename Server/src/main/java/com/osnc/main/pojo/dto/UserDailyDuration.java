package com.osnc.main.pojo.dto;

import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;

@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "user_daily_duration")
public class UserDailyDuration {
    private Long id;
    private Long userId;
    private LocalDate statDate;
    private Integer totalSeconds;
    private LocalDateTime createTime;
    private LocalDateTime updateTime;
}
