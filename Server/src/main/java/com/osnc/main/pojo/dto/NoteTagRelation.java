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
 * 笔记-标签关联表
 *
 * @author 系统自动生成
 * @date 2025-12-11
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@TableName(value = "note_tag_relation")
public class NoteTagRelation {
    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("note_id")
    private Long noteId;

    @TableField("tag_id")
    private Long tagId;

    @TableField("create_time")
    private LocalDateTime createTime;
}