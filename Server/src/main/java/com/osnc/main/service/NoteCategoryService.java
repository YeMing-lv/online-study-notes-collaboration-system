package com.osnc.main.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.osnc.main.pojo.dto.NoteCategory;

import java.util.List;

/**
 * 笔记分类服务接口（MyBatis-Plus版）
 * 对应表：note_category（笔记分类表）
 * @author osnc
 */
public interface NoteCategoryService extends IService<NoteCategory> {

    // 自定义方法示例：查询用户的分类树（含父子结构）
    List<NoteCategory> listCategoryTreeByUserId(Long userId);
}