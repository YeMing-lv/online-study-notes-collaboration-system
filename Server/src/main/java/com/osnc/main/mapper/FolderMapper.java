package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.Folder;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 文件夹Mapper（管理笔记/文件）
 * 对应表：folder（文件夹表）
 * @author osnc
 */
@Mapper
public interface FolderMapper extends BaseMapper<Folder> {

    // 可扩展自定义SQL：如查询用户文件夹树（递归）、批量移动文件夹等
}