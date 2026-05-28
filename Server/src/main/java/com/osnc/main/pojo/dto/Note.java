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
 * 学习笔记核心表
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "note")
public class Note {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("title")
    private String title;

    @TableField("content")
    private String content;

    @TableField("user_id")
    private Long userId;

    @TableField("folder_id")
    private Long folderId;

    @TableField("category_id")
    private Long categoryId;

    @TableField("cover_url")
    private String coverUrl;

    @TableField("is_public")
    private Integer isPublic;

    @TableField("is_star")
    private Integer isStar;

    @TableField("read_count")
    private Integer readCount;

    @TableField("is_recycle")
    private Integer isRecycle;

    @TableField("delete_time")
    private LocalDateTime deleteTime;

    @TableField("recycle_expire_time")
    private LocalDateTime recycleExpireTime;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}