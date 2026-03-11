package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.ShareMapper;
import com.osnc.main.pojo.dto.Share;
import com.osnc.main.service.ShareService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
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
}