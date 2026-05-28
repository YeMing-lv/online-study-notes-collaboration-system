package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.common.Result;
import com.osnc.main.mapper.FolderMapper;
import com.osnc.main.pojo.dto.Folder;
import com.osnc.main.pojo.dto.User;
import com.osnc.main.service.FolderService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.stream.Collectors;

/**
 * 文件夹服务实现类（MyBatis-Plus版）
 * 对应表：folder（文件夹表）
 * @author osnc
 */
@Service
public class FolderServiceImpl extends ServiceImpl<FolderMapper, Folder> implements FolderService {

    @Autowired
    private FolderMapper folderMapper;

    @Override
    public Result listFolderTreeByUserId(Long userId) {
        // 查询所有文件夹
        List<Folder> allFolder = lambdaQuery()
                .eq(Folder::getUserId, userId)
                .eq(Folder::getIsDeleted, 0)
                .eq(Folder::getIsRecycle, 0)
                .orderByDesc(Folder::getId)
                .list();

        // 查询根目录文件夹，再递归组装子文件夹（示例逻辑）
        List<Folder> rootFolders = allFolder.stream()
                .filter(folder -> folder.getParentId() == 0)
                .collect(Collectors.toList());

        rootFolders.forEach(root -> buildChildren(root, allFolder));

        // 此处可补充递归查询子文件夹的逻辑
        return Result.success(rootFolders);
    }

    @Override
    public Result updateFolder(Folder folder) {
        folder.setUpdateTime(LocalDateTime.now());
        int row = folderMapper.updateById(folder);
        if (row > 0) {
            return Result.success(row);
        }
        return Result.failure("更新文件夹失败");
    }

    @Override
    public Result createFolder(Folder folder) {
        int row = folderMapper.insert(folder);
        if (row > 0) {
            return Result.success(row);
        }
        return Result.failure();
    }

    @Override
    public Result deleteFolder(Long id) {
        Folder folder = folderMapper.selectById(id);
        if (folder != null) {
            folder.DeteleFolder();
        }
        int row = folderMapper.updateById(folder);
        if (row > 0) {
            return Result.success(row);
        }
        return Result.failure();
    }

    @Override
    public Result getFolderByID(Long id) {
        Folder folder = folderMapper.selectById(id);
        if (folder != null) {
            return Result.success(folder);
        }
        return Result.failure(0);
    }

    private void buildChildren(Folder parentFolder, List<Folder> allFolder) {
        List<Folder> children = allFolder.stream()
                .filter(folder -> folder.getParentId().equals(parentFolder.getId()))
                .collect(Collectors.toList());

        children.forEach(child -> buildChildren(child, allFolder));

        parentFolder.setChildren(children);
    }

}