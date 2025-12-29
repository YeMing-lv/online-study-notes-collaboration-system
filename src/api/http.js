import axios from "axios";
import apiClient from "./request";

export const http = {
    get(url, params) {
        return apiClient({
            url,
            method: 'get',
            params
        }).catch((err) => {
            if (axios.isCancel(err)) {
                console.log('请求超时自动取消', err.message)
            }
        });
    },
    post(url, data) {
        return apiClient({
            url,
            method: 'post',
            data,
        }).catch((err) => {
            if (axios.isCancel(err)) {
                console.log('请求超时自动取消', err.message)
            }
        });
    }
};