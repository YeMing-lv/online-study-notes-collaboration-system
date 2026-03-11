package com.osnc.main.pojo.dto;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.Date;
import java.util.List;

/**
 * 文件夹表（管理笔记/文件）
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "folder")
public class Folder {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("parent_id")
    private Long parentId;

    @TableField("sort")
    private Integer sort;

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

    @TableField(exist = false)
    private List<Folder> children;

    public void DeteleFolder() {
        this.isRecycle = 1;
        this.deleteTime = LocalDateTime.now();
        this.recycleExpireTime = LocalDateTime.now().plusDays(30);
        this.updateTime = LocalDateTime.now();
    }
}