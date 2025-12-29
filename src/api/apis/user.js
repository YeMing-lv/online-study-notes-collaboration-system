import { http } from "../http";

// 登录
export const login = (user) => http.post('user/login', user);

// 注册
export const register = (user) => http.post('user/register', user);

// 更新用户信息
export const updateUser = (user) => http.post('user/update', user);