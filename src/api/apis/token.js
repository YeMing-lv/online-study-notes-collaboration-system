import { http } from "../http";

// 检查Token
export const checkToken = () => http.post('checkToken'); // token放在请求头了

