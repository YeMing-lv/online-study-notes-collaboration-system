import { defineStore } from "pinia";

export const useUserStore = defineStore('user', {
    state: () => ({
        user: null,
        token: null,
        verify: {
            failVcodeNum: 0,
            lastVcodeTime: null,
            enableVcodeTime: null
        }
    }),
    actions: {
        /** 初始化数据 */
        initUser() {
            this.user = null;
            this.token = null;
        },
        initState() {
            this.user = null;
            this.token = null;
            this.verify = {
                failVcodeNum: 0,
                lastVcodeTime: null,
                enableVcodeTime: null
            }
        },
        initVerify() {
            this.verify = {
                failVcodeNum: 0,
                lastVcodeTime: null,
                enableVcodeTime: null
            }
        },

        /**
         * 保存用户 验证信息
         * @param  {...any} value 
         */
        saveVerify(failVcodeNum, lastVcodeTime, enableVcodeTime) {
            this.verify = {
                failVcodeNum: failVcodeNum,
                lastVcodeTime: lastVcodeTime,
                enableVcodeTime: enableVcodeTime,
            };
        }
    },
    persist: true,
})