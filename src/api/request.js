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
        // 发请求前做的一些处理，数据转化，配置请求头，!!设置token,设置loading等，根据需求去添加
        //注意使用token的时候需要引入cookie方法或者用本地localStorage等方法，推荐js-cookie
        config.signal = newAbortSignal(config.timeout || 5000)

        const needToken = !(config.params?.noToken || config.data?.noToken)

        // 示例1：添加token
        const userStore = useUserStore();
        const token = userStore.token;
        if (needToken) {
            config.headers.Authorization = token;
            config.headers.token = token;
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
        // 2xx范围内的状态码

        // 统一处理数据结构
        const res = response.data

        // 自定义业务状态码处理
        if (res.code !== 200) {
            // 超出2xx范围的状态码 映射
            // const ERROR_MAP = {
            //     400: '请求参数错误',
            //     401: '认证过期，请重新登录',
            //     403: '无权访问该资源',
            //     500: '服务器开小差了，请稍后再试',
            //     // 其他状态码
            // }
            // ????????????????????????????????????????????????????????
            // 错误信息解构
            const { code, message } = res
            // HTTP状态码处理
            // const mess = ERROR_MAP[code] || message || '请求处理失败'
            const mess = message || '请求处理失败'
            console.warn(mess) // 错误信息提示
        } else {
            return res
        }
    },
    error => {
        console.error('响应错误:', error);
        return Promise.reject(error);
    }
);

export default apiClient;