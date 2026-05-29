package com.osnc.main.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.vo.FolderNoteQuery;
import com.osnc.main.service.impl.FileServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.Map;

/**
 * 文件控制器（含回收站功能）
 * 对应表：file（文件表）
 * @author osnc
 */
@RestController
@RequestMapping("/file")
public class FileController {

    @Autowired
    private FileServiceImpl fileService;

    @PostMapping("/folder/fileList")
    public Result getFolderNoteList(@RequestBody FolderNoteQuery query) {
        // 调用业务层，获取最终合并排序后的结果
        Page<Map<String, Object>> resultList = fileService.getFolderAndNoteList(query);
        return Result.success(resultList);
    }

    @PostMapping("/recycleList")
    public Result getRecycleList(@RequestBody FolderNoteQuery query) {
        Page<Map<String, Object>> resultList = fileService.getRecycleList(query);
        return Result.success(resultList);
    }

}