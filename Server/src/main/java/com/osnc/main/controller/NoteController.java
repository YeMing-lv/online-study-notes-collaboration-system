package com.osnc.main.controller;

import com.osnc.main.common.PageParam;
import com.osnc.main.common.Result;
import com.osnc.main.pojo.dto.Note;
import com.osnc.main.pojo.dto.NoteVersion;
import com.osnc.main.pojo.dto.Star;
import com.osnc.main.pojo.vo.NoteQuery;
import com.osnc.main.service.impl.NoteServiceImpl;
import com.osnc.main.service.impl.NoteVersionServiceImpl;
import com.osnc.main.service.impl.ShareServiceImpl;
import com.osnc.main.service.impl.StarServiceImpl;
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

    @Autowired
    private ShareServiceImpl shareService;

    @Autowired
    private StarServiceImpl starService;

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
    public Result saveOrUpdateNote(@RequestBody Note note) {
        return noteService.updateNote(note);
    }

    /**
     * 获取指定种类笔记列表
     * @param noteQuery
     * @return
     */
    @PostMapping("/list")
    public Result listNote(@RequestBody NoteQuery noteQuery) {
        if (noteQuery.getType().equals("")) return Result.failure("Failed listType!");

        switch (noteQuery.getType()) {
            case "new":
                return noteService.listNewNote(noteQuery.getUserId());
            case "share":
                return shareService.listShareNote(noteQuery.getUserId());
            case "star":
                return starService.listStarNote(noteQuery.getUserId());
        }
        return Result.success("");
    }

    /**
     * 获取笔记版本列表
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

    @DeleteMapping
    public Result deleteNote(@RequestParam Long id) {
        boolean result = noteService.removeById(id);
        if (result) {
            return Result.success("");
        }
        return Result.failure();
    }

    @PostMapping("/star")
    public Result starNote(@RequestParam Long userId, @RequestParam Long noteId) {
        Star star = new Star(2, userId, noteId);
        boolean result = starService.saveOrUpdate(star);
        if (result) {
            return Result.success(result);
        }
        return Result.failure();
    }

}