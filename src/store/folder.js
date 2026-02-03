/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-31 11:42:29
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-27 10:44:22
 * @FilePath: \online-study-notes-collaboration-system\src\store\folder.js
 * @Description: 文件夹状态管理，记录当前查看的文件夹、文件夹路径
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import { defineStore } from "pinia";

export const useFolderStore = defineStore('folderStore', {
    state: () => ({
        currentFolder: {},
        folderRoutes: []
    }),
    actions: {
        init() {
            this.currentFolder = {};
            this.folderRoutes = []
        }

    },
})