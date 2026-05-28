package com.osnc.main.pojo.vo;

import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.Share;
import lombok.Data;

@Data
public class ShareNoteVO extends Note {
    private Share share;

    public ShareNoteVO(Note note, Share share) {
        super(note.getId(),note.getTitle(),note.getContent(),note.getUserId(),note.getFolderId(),note.getCategoryId(),note.getCoverUrl(),note.getIsPublic(),note.getIsStar(),note.getReadCount(),note.getIsRecycle(),note.getDeleteTime(),note.getRecycleExpireTime(),note.getIsDeleted(),note.getCreateTime(),note.getUpdateTime());
        this.share = share;
    }

}
