/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-31 11:42:29
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-02-03 17:17:33
 * @FilePath: \online-study-notes-collaboration-system\src\store\folder.js
 * @Description: 文件夹状态管理，记录当前查看的文件夹、文件夹路径
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import { defineStore } from "pinia";

export const useFolderStore = defineStore('folderStore', {
    state: () => ({
        currentFolder: { id: 0, name: '' },
        folderRoutes: []
    }),
    actions: {
        init() {
            this.currentFolder = { id: 0, name: '' };
            this.folderRoutes = []
        },

        /**
         * @description: 设置当前文件夹
         * @param {*} folder
         * @return {*}
         */
        setCurrentFolder(folder) {
            this.currentFolder = folder;
        }

    },
})