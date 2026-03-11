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
 * 文件表（含回收站功能）
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "file")
public class File {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("file_name")
    private String fileName;

    @TableField("file_path")
    private String filePath;

    @TableField("file_size")
    private Long fileSize;

    @TableField("file_type")
    private String fileType;

    @TableField("upload_user_id")
    private Long uploadUserId;

    @TableField("upload_time")
    private LocalDateTime uploadTime;

    @TableField("note_id")
    private Long noteId;

    @TableField("is_recycle")
    private Integer isRecycle;

    @TableField("delete_time")
    private LocalDateTime deleteTime;

    @TableField("delete_user_id")
    private Long deleteUserId;

    @TableField("recycle_expire_time")
    private LocalDateTime recycleExpireTime;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_time")
    private LocalDateTime createTime;

    @TableField("update_time")
    private LocalDateTime updateTime;
}