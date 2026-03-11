/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-10 17:14:53
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-21 09:04:51
 * @FilePath: \online-study-notes-collaboration-system\src\api\request.js
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import axios from "axios";
import { useUserStore } from "../store/user";

// 实例化axios
const apiClient = axios.create({
    baseURL: 'http://localhost:8082/',
    timeout: 10000,
    headers: { 'Content-Type': 'application/json' },
});

function newAbortSignal(timeoutMs) {
    const abortController = new AbortController()
    setTimeout(() => abortController.abort(), timeoutMs || 0);
    return abortController.signal
}


// 请求拦截器
apiClient.interceptors.request.use(
    config => {
        config.signal = newAbortSignal(config.timeout || 5000)

        const needToken = !(config.params?.noToken || config.data?.noToken)

        // 示例1：添加token
        const userStore = useUserStore();
        const token = userStore.token;
        if (needToken) {
            config.headers.Authorization = token;
        }

        //示例2：不验证token
        // delete config.params.noToken
        // delete config.params.signal

        // 示例3：Content-Type动态处理
        if (config.data instanceof FormData) {
            config.headers['Content-Type'] = 'multipart/form-data' // 文件上传
        }

        // 示例4：请求超时
        config.metaData = { startTime: Date.now() }

        return config
    },
    error => {
        return Promise.reject(error);
    }
);
// 响应拦截器
apiClient.interceptors.response.use(
    response => {
        const res = response.data
        // 如果返回的是文件流，直接返回
        if (response.config.responseType === 'blob') {
            return res
        }
        // 正常返回数据
        if (res.code === 200) {
            return res
        }

        // 处理特定错误码
        if (res.code === 401) {
            ElMessage.error('登录已过期，请重新登录');
            router.push('/login');
        } else {
            ElMessage.error(res.message || '请求失败')
        }

        return res;
    },
    error => {
        console.error('响应错误:', error)

        // 处理401错误
        if (error.response && error.response.status === 401) {
            ElMessage.error('登录已过期，请重新登录');
            router.push('/login');
        } else {
            const errorMsg = error.response?.data?.message || error.message || '请求失败，请稍后重试'
            ElMessage.error(errorMsg)
        }

        return {
            success: false,
            data: null,
            message: error.message || '请求失败，请稍后重试'
        }
    }
);

export default apiClient;