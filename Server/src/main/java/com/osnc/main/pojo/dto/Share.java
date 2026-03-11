package com.osnc.main.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.time.LocalDateTime;

/**
 * 通用分享表（文件/笔记）
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "share")
public class Share {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("target_type")
    private Integer targetType;

    @TableField("target_id")
    private Long targetId;

    @TableField("share_from_user_id")
    private Long shareFromUserId;

    @TableField("share_to_user_id")
    private Long shareToUserId;

    @TableField("share_permission")
    private Integer sharePermission;

    @TableField("share_time")
    private LocalDateTime shareTime;

    @TableField("share_expire_time")
    private LocalDateTime shareExpireTime;

    @TableField("is_share_valid")
    private Integer isShareValid;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}