package com.osnc.main.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.math.BigDecimal;
import java.time.LocalDateTime;

/**
 * 学习进度表
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "learning_progress")
public class LearningProgress {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("target_type")
    private Integer targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("progress")
    private String progress;

    @TableField("progress_num")
    private BigDecimal progressNum;

    @TableField("update_time")
    private LocalDateTime updateTime;
}