package com.osnc.main.controller;

import com.osnc.main.common.Result;
import com.osnc.main.service.impl.NoteServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

/**
 * 学习笔记核心控制器
 * 对应表：note（学习笔记核心表）
 * @author osnc
 */
@RestController
@RequestMapping("/note")
public class NoteController {

    @Autowired
    private NoteServiceImpl noteService;

    @GetMapping
    public Result getNoteById(@RequestParam Long id) {
        return noteService.getNoteById(id);
    }

}