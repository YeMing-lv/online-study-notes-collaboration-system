package com.osnc.main.controller;

import com.osnc.main.service.impl.StarServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * 通用星标控制器（文件/笔记）
 * 对应表：star（通用星标表）
 * @author osnc
 */
@RestController
@RequestMapping("/star")
public class StarController {

    @Autowired
    private StarServiceImpl starService;


}