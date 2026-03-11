package com.osnc.main.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.osnc.main.mapper.NoteCategoryMapper; // 需自行创建Mapper接口
import com.osnc.main.pojo.dto.NoteCategory;
import com.osnc.main.service.NoteCategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 笔记分类服务实现类（MyBatis-Plus版）
 * 对应表：note_category（笔记分类表）
 * @author osnc
 */
@Service
public class NoteCategoryServiceImpl extends ServiceImpl<NoteCategoryMapper, NoteCategory> implements NoteCategoryService {

    @Autowired
    private NoteCategoryMapper noteCategoryMapper;

    @Override
    public List<NoteCategory> listCategoryTreeByUserId(Long userId) {
        // 先查询顶级分类，再递归组装子分类（示例逻辑）
        List<NoteCategory> topCategories = lambdaQuery()
                .eq(NoteCategory::getUserId, userId)
                .eq(NoteCategory::getParentId, 0)
                .eq(NoteCategory::getIsDeleted, 0)
                .orderByAsc(NoteCategory::getSort)
                .list();

        // 此处可补充递归查询子分类的逻辑
        return topCategories;
    }
}