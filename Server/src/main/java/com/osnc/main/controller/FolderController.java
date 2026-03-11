package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Folder;
import com.osnc.main.pojo.dto.User;
import com.osnc.main.pojo.vo.UserVO;
import com.osnc.main.service.FolderService;
import com.osnc.main.service.impl.FolderServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 文件夹控制器（管理笔记/文件）
 * 对应表：folder（文件夹表）
 * @author osnc
 */
@Slf4j
@RestController
@RequestMapping("/folder")
public class FolderController {

    @Autowired
    private FolderServiceImpl folderService;

    @PostMapping("/userId")
    public Result listFolderTreeByUserId(@RequestBody User user) {
        Long userId = user.getId();
        return folderService.listFolderTreeByUserId(userId);
    }

    @PostMapping("/update")
    public Result updateFolder(@RequestBody Folder folder) {
        return folderService.updateFolder(folder);
    }

    @PostMapping("/create")
    public Result createFolder(@RequestBody Folder folder) {
        return folderService.createFolder(folder);
    }

    @PostMapping("/delete")
    public Result deleteFolder(@RequestBody Long id) {
        return folderService.deleteFolder(id);
    }

}