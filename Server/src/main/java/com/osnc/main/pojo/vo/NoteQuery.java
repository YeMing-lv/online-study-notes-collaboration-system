package com.osnc.main.pojo.vo;

import com.osnc.main.common.PageParam;
import lombok.Data;
import lombok.EqualsAndHashCode;

@Data
@EqualsAndHashCode(callSuper = true)
public class NoteQuery extends PageParam {
    private Long userId;
    private String type;
    private String keyword;
}
