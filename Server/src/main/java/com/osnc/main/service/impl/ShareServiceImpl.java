package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.mapper.ShareMapper;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.Share;
import com.osnc.main.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * 通用分享服务实现类（MyBatis-Plus版）
 * 对应表：share（通用分享表）
 * @author osnc
 */
@Service
public class ShareServiceImpl extends ServiceImpl<ShareMapper, Share> implements ShareService {

    @Autowired
    private ShareMapper shareMapper;

    @Autowired
    private NoteMapper noteMapper;

    @Override
    public List<Share> listValidReceivedByUserId(Long userId) {
        return lambdaQuery()
                .eq(Share::getShareToUserId, userId)
                .eq(Share::getIsShareValid, 1) // 仅查有效分享
                .and(wrapper -> wrapper
                        .isNull(Share::getShareExpireTime) // 永久有效
                        .or()
                        .gt(Share::getShareExpireTime, LocalDateTime.now()) // 未过期
                )
                .list();
    }

    @Override
    public List<Share> listByShareFromUserId(Long userId) {
        return lambdaQuery()
                .eq(Share::getShareFromUserId, userId)
                .eq(Share::getIsShareValid, 1)
                .list();
    }

    @Override
    public Result listShareNote(Long userId) {
        Page<Share> page = new Page<>(1, 10);
        LambdaQueryWrapper<Share> wrapper = new LambdaQueryWrapper<>();
        wrapper.eq(Share::getShareToUserId, userId)
                .orderByDesc(Share::getUpdateTime);
        List<Share> shares = shareMapper.selectList(page, wrapper);
        if (shares.isEmpty()) {
            return Result.success("");
        }

        List<Note> shareList = new ArrayList<>();
        for (int i = 0; i < shares.size(); i++) {
            Note note = noteMapper.selectById(shares.get(i).getTargetId());
            if (note != null) shareList.add(note);
        }

        return Result.success(shareList);
    }
}