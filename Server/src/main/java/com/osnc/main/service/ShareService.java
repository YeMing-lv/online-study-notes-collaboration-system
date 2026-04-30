package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Share;

import java.util.List;

/**
 * 通用分享服务接口（MyBatis-Plus版）
 * 对应表：share（通用分享表）
 * @author osnc
 */
public interface ShareService extends IService<Share> {

    // 自定义方法示例：查询用户接收的有效分享
    List<Share> listValidReceivedByUserId(Long userId);

    // 自定义方法示例：查询用户分享的资源列表
    List<Share> listByShareFromUserId(Long userId);

    Result listShareNote(Long userId);
}