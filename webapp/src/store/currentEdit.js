/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-01-29 15:04:57
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-02-03 15:43:11
 * @FilePath: \online-study-notes-collaboration-system\src\store\currentEdit.js
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import { defineStore } from "pinia";

export const useCurrEditStore = defineStore('currentEdit', {
    state: () => ({
        currentEdit: {},
    }),
    actions: {
        init() {
            this.currentEdit = {};
        },

        /**
         * @description: 设置当前编辑文件
         * @param {*} file
         * @return {*}
         */        
        setCurrentEdit(file) {
            this.currentEdit = file;
        }
    }
})