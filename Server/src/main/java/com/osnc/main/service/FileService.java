package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.File;
import com.osnc.main.pojo.vo.FolderNoteQuery;

import java.util.List;
import java.util.Map;

/**
 * 文件服务接口（MyBatis-Plus版）
 * 对应表：file（文件表）
 * @author osnc
 */
public interface FileService extends IService<File> {
    /**
     * 核心查询方法：文件夹+笔记 合并查询
     * @param query 所有查询条件+排序条件的封装实体类
     * @return 合并后的数组，原生字段+追加type，已按条件排序完成
     */
    Page<Map<String, Object>> getFolderAndNoteList(FolderNoteQuery query);
}