package com.osnc.main.pojo.vo;
import com.osnc.main.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

import java.time.LocalDateTime;

/**
 * 文件夹+笔记 专属查询条件实体类【你需要的核心类】
 * 所有筛选条件、排序条件、必填参数全部封装在这里
 * 所有排序字段为Boolean：true=升序 false=降序 null=不排序
 * 所有时间/筛选字段为null则忽略该条件
 */
@Data
@EqualsAndHashCode(callSuper = true) // 继承PageParam必须加此注解
public class FolderNoteQuery extends PageParam {
    private Long folderId;
    private Long userId;
    private Integer type;
    private String keyword;
    private Boolean orderByTypeDesc;
    private Boolean orderByCreateDesc;
    private Boolean orderByUpdateDesc;
}
