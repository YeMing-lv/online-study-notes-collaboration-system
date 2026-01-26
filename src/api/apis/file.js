/*
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-01-15 15:53:33
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-16 11:29:02
 * @FilePath: \online-study-notes-collaboration-system\src\api\apis\file.js
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
 */
import { http } from "../http";

// 获取文件夹下的文件列表
export const listFileByFolderId = (queryByFileList) => http.post('file/folder/fileList', queryByFileList);