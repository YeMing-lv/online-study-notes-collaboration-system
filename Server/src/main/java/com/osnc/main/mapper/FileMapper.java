package com.osnc.main.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.osnc.main.pojo.dto.File;
import org.apache.ibatis.annotations.Mapper;

/**
 * 文件Mapper（含回收站功能）
 * 对应表：file（文件表）
 * @author osnc
 */
@Mapper
public interface FileMapper extends BaseMapper<File> {

    // 可扩展自定义SQL：如按文件类型筛选、清理过期回收站文件等
}