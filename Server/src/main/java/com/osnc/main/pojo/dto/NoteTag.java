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
 * 笔记标签主表
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "note_tag")
public class NoteTag {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("user_id")
    private Long userId;

    @TableField("name")
    private String name;

    @TableField("color")
    private String color;

    @TableField("is_deleted")
    private Integer isDeleted;

    @TableField("create_time")
    private LocalDateTime createTime;
}