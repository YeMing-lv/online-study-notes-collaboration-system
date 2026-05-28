/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-31 11:42:29
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-04-30 16:40:42
 * @FilePath: \webapp\src\store\folder.js
 * @Description: 文件夹状态管理，记录当前查看的文件夹、文件夹路径
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import { defineStore } from "pinia";

export const useFolderStore = defineStore('folderStore', {
    state: () => ({
        currentFolder: { id: 0, name: '' },
        folders: [],
        // folderRoutes: [],
        defaultExpandedKeys: [], // 记录树形文件夹的展开状态
        isRefreshFolder: false,
        isRefreshTree: false,
    }),
    actions: {
        init() {
            this.currentFolder = { id: 0, name: '' };
            this.defaultExpandedKeys = [];
            // this.folderRoutes = []
        },

        /**
         * @description: 设置当前文件夹
         * @param {*} folder
         * @return {*}
         */
        setCurrentFolder(folder) {
            this.currentFolder = folder;
        },

    },
    persist: true
})