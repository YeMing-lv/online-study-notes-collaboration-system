import { http } from "../http";

// 获取用户文件夹
export const listFolderByUserId = (user) => http.post('folder/userId', user);

// 更新
export const updateFolder = (folder) => http.post('folder/update', folder);

// 新建
export const createFolder = (folder) => http.post('folder/create', folder);

// 删除
export const deleteFolder = (id) => http.post('folder/delete', id);