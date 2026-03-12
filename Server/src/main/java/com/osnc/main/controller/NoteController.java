package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.NoteVersion;
import com.osnc.main.service.impl.NoteServiceImpl;
import com.osnc.main.service.impl.NoteVersionServiceImpl;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习笔记核心控制器
 * 对应表：note（学习笔记核心表）
 * @author osnc
 */
@Slf4j
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteServiceImpl noteService;

    @Autowired
    private NoteVersionServiceImpl noteVersionService;

    /**
     * 查询笔记
     * @param id
     * @return
     */
    @GetMapping
    public Result getNoteById(@RequestParam Long id) {
        return noteService.getNoteById(id);
    }

    @PostMapping
    public Result updateNote(@RequestBody Note note) {
        return noteService.updateNote(note);
    }

    /**
     * 获取笔记列表
     */
    @GetMapping("/version/getNoteVersionList")
    public Result getNoteVersionList(@RequestParam Long noteId) {
        return noteVersionService.listByNoteIdOrderByVersionNumDesc(noteId);
    }

    /**
     * 保存旧笔记
     * @param noteVersion
     * @return
     */
    @PostMapping("/version/saveNoteVersion")
    public Result saveNoteVersion(@RequestBody NoteVersion noteVersion) {
        log.info(String.valueOf(noteVersion));
        return noteVersionService.saveNoteVersion(noteVersion);
    }

}