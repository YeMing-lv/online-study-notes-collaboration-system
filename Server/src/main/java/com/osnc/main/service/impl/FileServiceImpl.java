package com.osnc.main.service.impl;
import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.osnc.main.mapper.FileMapper;
import com.osnc.main.mapper.FolderMapper;
import com.osnc.main.mapper.NoteMapper;
import com.osnc.main.pojo.dto.File;
import com.osnc.main.pojo.dto.Folder;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.vo.FolderNoteQuery;
import com.osnc.main.service.FileService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import java.time.LocalDateTime;
import java.util.*;
import java.util.regex.Pattern;
import java.util.stream.Collectors;
import java.util.stream.Stream;

@Slf4j
@Service
public class FileServiceImpl extends ServiceImpl<FileMapper, File> implements FileService {

    @Autowired
    private FolderMapper folderMapper;
    @Autowired
    private NoteMapper noteMapper;

    // 类型常量 全局统一
    private static final Integer TYPE_FOLDER = 1;
    private static final Integer TYPE_NOTE = 2;
    // 对象转Map工具，用于追加type属性
    private final ObjectMapper objectMapper = new ObjectMapper();

    @Override
    public Page<Map<String, Object>> getFolderAndNoteList(FolderNoteQuery query) {
        // 从查询条件实体类中获取所有参数
        Long folderId = query.getFolderId();
        Long userId = query.getUserId();
        String keyword = query.getKeyword();
        List<Map<String, Object>> resultList = new ArrayList<>();

        // ============ 1. 查询【文件夹】原生数据 + 筛选条件 ============
        if (query.getType() == null || TYPE_FOLDER.equals(query.getType())) {
            LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
            // 固定过滤条件：用户ID+父文件夹ID+未删除+未回收 (Lambda语法，正确)
            wrapper.eq(Folder::getParentId, folderId)
                    .eq(Folder::getUserId, userId)
                    .eq(Folder::getIsDeleted, 0)
                    .eq(Folder::getIsRecycle, 0);
            if (StringUtils.hasText(keyword)) {
                wrapper.like(Folder::getName, keyword);
            }

            List<Folder> folderList = folderMapper.selectList(wrapper);
            List<Map<String, Object>> folderMapList = folderList.stream().map(folder -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", folder.getId());
                map.put("parentId", folder.getParentId());
                map.put("name", folder.getName());
                map.put("userId", folder.getUserId());
                map.put("sort", folder.getSort());
                map.put("createTime", folder.getCreateTime());
                map.put("updateTime", folder.getUpdateTime());
                map.put("isDeleted", folder.getIsDeleted());
                map.put("isRecycle", folder.getIsRecycle());
                map.put("type", TYPE_FOLDER); // 追加文件夹类型标识
                return map;
            }).collect(Collectors.toList());
            resultList.addAll(folderMapList);
        }

        // ============ 2. 查询【笔记】原生数据 + 筛选条件  ============
        if (query.getType() == null || TYPE_NOTE.equals(query.getType())) {
            LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
            // 固定过滤条件：用户ID+所属文件夹ID+未删除+未回收 (Lambda语法，正确)
            wrapper.eq(Note::getFolderId, folderId)
                    .eq(Note::getUserId, userId)
                    .eq(Note::getIsDeleted, 0)
                    .eq(Note::getIsRecycle, 0);
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
            }

            String contentRegexp = "<[^>]+>";

            // 原生实体 → 转Map → 追加type=2 → 加入结果集
            List<Note> noteList = noteMapper.selectList(wrapper);
            List<Map<String, Object>> noteMapList = noteList.stream().map(note -> {
                // ========== 核心修复：HTML转纯文本+安全截取 ==========
                String originalContent = note.getContent();
                String shortText = ""; // 空值兜底

                // 非空时处理，空值直接用空字符串
                if (originalContent != null && !originalContent.isEmpty()) {
                    // HTML转纯文本（移除所有标签）
                    String plainText = originalContent.replaceAll("<[^>]+>", "");
                    int maxLength = 100;
                    // 安全截取：长度不足100取全部，否则截取+省略号
                    shortText = plainText.length() <= maxLength ? plainText : plainText.substring(0, maxLength) + "...";
                }
                note.setContent(shortText);

                Map<String, Object> map = new HashMap<>(16);
                map.put("id", note.getId());
                map.put("title", note.getTitle());
                map.put("content", note.getContent()); // 你的富文本内容正常返回
                map.put("userId", note.getUserId());
                map.put("folderId", note.getFolderId());
                map.put("categoryId", note.getCategoryId());
                map.put("coverUrl", note.getCoverUrl());
                map.put("isPublic", note.getIsPublic());
                map.put("isStar", note.getIsStar());
                map.put("readCount", note.getReadCount());
                map.put("createTime", note.getCreateTime());
                map.put("updateTime", note.getUpdateTime());
                map.put("isDeleted", note.getIsDeleted());
                map.put("isRecycle", note.getIsRecycle());
                map.put("type", TYPE_NOTE); // 追加笔记类型标识
                return map;
            }).collect(Collectors.toList());
            resultList.addAll(noteMapList);
        }

        // ============ 3. 合并完成后 → 统一执行【布尔值控制】的排序逻辑 ============
        if (!resultList.isEmpty()) {
            Collections.sort(resultList, (o1, o2) -> {
                int compare = 0;
                // 排序规则1：按type排序 (true降序/false升序/null不排序)
                if (query.getOrderByTypeDesc() != null) {
                    Integer t1 = (Integer) o1.get("type");
                    Integer t2 = (Integer) o2.get("type");
                    compare = query.getOrderByTypeDesc() ? t2.compareTo(t1) : t1.compareTo(t2);
                    if (compare != 0) return compare;
                }
                // 排序规则2：按创建时间排序
                if (query.getOrderByCreateDesc() != null && compare == 0) {
                    LocalDateTime c1 = (LocalDateTime) o1.get("createTime");
                    LocalDateTime c2 = (LocalDateTime) o2.get("createTime");
                    compare = query.getOrderByCreateDesc() ? c2.compareTo(c1) : c1.compareTo(c2);
                    if (compare != 0) return compare;
                }
                // 排序规则3：按更新时间排序（优先级最高）
                if (query.getOrderByUpdateDesc() != null && compare == 0) {
                    LocalDateTime u1 = (LocalDateTime) o1.get("updateTime");
                    LocalDateTime u2 = (LocalDateTime) o2.get("updateTime");
                    compare = query.getOrderByUpdateDesc() ? u2.compareTo(u1) : u1.compareTo(u2);
                } else {
                    LocalDateTime u1 = (LocalDateTime) o1.get("updateTime");
                    LocalDateTime u2 = (LocalDateTime) o2.get("updateTime");
                    compare = true ? u2.compareTo(u1) : u1.compareTo(u2);
                }
                return compare;
            });
        }


        // ========== 4. 核心新增：基于PageParam的内存分页逻辑 ==========
        // 4.1 构建Page对象（利用你的PageParam.toPage()方法）
        Page<Map<String, Object>> page = query.toPage();
        // 4.2 设置总记录数
        page.setTotal(resultList.size());
        // 4.3 计算分页起始/结束索引
        int current = query.getCurrent(); // 当前页（从1开始）
        int size = query.getSize();       // 每页条数
        int startIndex = (current - 1) * size;
        int endIndex = Math.min(startIndex + size, resultList.size());
        // 4.4 截取分页数据（注意：subList是左闭右开）
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (startIndex < endIndex && startIndex < resultList.size()) {
            pageList = resultList.subList(startIndex, endIndex);
        }
        // 4.5 设置分页数据到Page对象
        page.setRecords(pageList);

        return page;
    }

    @Override
    public Page<Map<String, Object>> getRecycleList(FolderNoteQuery query) {
        // 从查询条件实体类中获取所有参数
        Long userId = query.getUserId();
        String keyword = query.getKeyword();
        List<Map<String, Object>> resultList = new ArrayList<>();

        // ============ 1. 查询【文件夹】原生数据 + 筛选条件 ============
        if (query.getType() == null || TYPE_FOLDER.equals(query.getType())) {
            LambdaQueryWrapper<Folder> wrapper = new LambdaQueryWrapper<>();
            // 固定过滤条件：用户ID+父文件夹ID+未删除+未回收 (Lambda语法，正确)
            wrapper.eq(Folder::getUserId, userId)
                    .eq(Folder::getIsDeleted, 0)
                    .eq(Folder::getIsRecycle, 1);
            if (StringUtils.hasText(keyword)) {
                wrapper.like(Folder::getName, keyword);
            }

            List<Folder> folderList = folderMapper.selectList(wrapper);
            List<Map<String, Object>> folderMapList = folderList.stream().map(folder -> {
                Map<String, Object> map = new HashMap<>(16);
                map.put("id", folder.getId());
                map.put("parentId", folder.getParentId());
                map.put("name", folder.getName());
                map.put("userId", folder.getUserId());
                map.put("sort", folder.getSort());
                map.put("createTime", folder.getCreateTime());
                map.put("updateTime", folder.getUpdateTime());
                map.put("isDeleted", folder.getIsDeleted());
                map.put("isRecycle", folder.getIsRecycle());
                map.put("type", TYPE_FOLDER); // 追加文件夹类型标识
                return map;
            }).collect(Collectors.toList());
            resultList.addAll(folderMapList);
        }

        // ============ 2. 查询【笔记】原生数据 + 筛选条件  ============
        if (query.getType() == null || TYPE_NOTE.equals(query.getType())) {
            LambdaQueryWrapper<Note> wrapper = new LambdaQueryWrapper<>();
            // 固定过滤条件：用户ID+所属文件夹ID+未删除+未回收 (Lambda语法，正确)
            wrapper.eq(Note::getUserId, userId)
                    .eq(Note::getIsDeleted, 0)
                    .eq(Note::getIsRecycle, 1);
            if (StringUtils.hasText(keyword)) {
                wrapper.and(w -> w.like(Note::getTitle, keyword).or().like(Note::getContent, keyword));
            }

            String contentRegexp = "<[^>]+>";

            // 原生实体 → 转Map → 追加type=2 → 加入结果集
            List<Note> noteList = noteMapper.selectList(wrapper);
            List<Map<String, Object>> noteMapList = noteList.stream().map(note -> {
                // ========== 核心修复：HTML转纯文本+安全截取 ==========
                String originalContent = note.getContent();
                String shortText = ""; // 空值兜底

                // 非空时处理，空值直接用空字符串
                if (originalContent != null && !originalContent.isEmpty()) {
                    // HTML转纯文本（移除所有标签）
                    String plainText = originalContent.replaceAll("<[^>]+>", "");
                    int maxLength = 100;
                    // 安全截取：长度不足100取全部，否则截取+省略号
                    shortText = plainText.length() <= maxLength ? plainText : plainText.substring(0, maxLength) + "...";
                }
                note.setContent(shortText);

                Map<String, Object> map = new HashMap<>(16);
                map.put("id", note.getId());
                map.put("title", note.getTitle());
                map.put("content", note.getContent()); // 你的富文本内容正常返回
                map.put("userId", note.getUserId());
                map.put("folderId", note.getFolderId());
                map.put("categoryId", note.getCategoryId());
                map.put("coverUrl", note.getCoverUrl());
                map.put("isPublic", note.getIsPublic());
                map.put("isStar", note.getIsStar());
                map.put("readCount", note.getReadCount());
                map.put("createTime", note.getCreateTime());
                map.put("updateTime", note.getUpdateTime());
                map.put("isDeleted", note.getIsDeleted());
                map.put("isRecycle", note.getIsRecycle());
                map.put("type", TYPE_NOTE); // 追加笔记类型标识
                return map;
            }).collect(Collectors.toList());
            resultList.addAll(noteMapList);
        }

        // ============ 3. 合并完成后 → 统一执行【布尔值控制】的排序逻辑 ============
        if (!resultList.isEmpty()) {
            Collections.sort(resultList, (o1, o2) -> {
                int compare = 0;
                // 排序规则：按创建时间排序
                LocalDateTime c1 = (LocalDateTime) o1.get("createTime");
                LocalDateTime c2 = (LocalDateTime) o2.get("createTime");
                compare = c2.compareTo(c1);
                return compare;
            });
        }

        // ========== 4. 核心新增：基于PageParam的内存分页逻辑 ==========
        // 4.1 构建Page对象（利用你的PageParam.toPage()方法）
        Page<Map<String, Object>> page = query.toPage();
        // 4.2 设置总记录数
        page.setTotal(resultList.size());
        // 4.3 计算分页起始/结束索引
        int current = query.getCurrent(); // 当前页（从1开始）
        int size = query.getSize();       // 每页条数
        int startIndex = (current - 1) * size;
        int endIndex = Math.min(startIndex + size, resultList.size());
        // 4.4 截取分页数据（注意：subList是左闭右开）
        List<Map<String, Object>> pageList = new ArrayList<>();
        if (startIndex < endIndex && startIndex < resultList.size()) {
            pageList = resultList.subList(startIndex, endIndex);
        }
        // 4.5 设置分页数据到Page对象
        page.setRecords(pageList);

        return page;
    }
}