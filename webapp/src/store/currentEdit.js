/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-01-29 15:04:57
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-03-12 15:27:00
 * @FilePath: \webapp\src\store\currentEdit.js
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
    },
    persist: true
})