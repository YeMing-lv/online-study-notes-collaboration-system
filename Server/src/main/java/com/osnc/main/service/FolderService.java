package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Folder;

import java.util.List;

/**
 * 文件夹服务接口（MyBatis-Plus版）
 * 对应表：folder（文件夹表）
 * @author osnc
 */
public interface FolderService extends IService<Folder> {

    // 自定义方法示例：查询用户的文件夹树（含父子结构）
    Result listFolderTreeByUserId(Long userId);

    Result updateFolder(Folder folder);

    Result createFolder(Folder folder);

    Result deleteFolder(Long id);

    Result getFolderByID(Long id);
}