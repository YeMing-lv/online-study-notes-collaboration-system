import { http } from "../http";

// 查询笔记
export const getNoteById = (id) => { return http.get(`/note?id=${id}`) };

// 创建或更新笔记
export const updateNote = (note) => { return http.post('/note', note) };

// 保存或更新旧笔记
export const saveNoteVersion = (noteVersion) => { return http.post('/note/version/saveNoteVersion', noteVersion) };

// 查询笔记的历史版本
export const getNoteVersionList = (id) => { return http.get(`/note/version/getNoteVersionList?noteId=${id}`) };

// 删除
export const deleteNote = (id) => { return http.delete(`/note?id=${id}`) };

// 获取指定种类笔记列表
export const listNote = (noteQuery) => { return http.post(`/note/list`, noteQuery) };

// 加星笔记
export const starNote = (userId, noteId) => { return http.post(`/note/star?userId=${userId}&noteId=${noteId}`) };

// 分享笔记
export const shareNote = (share) => { return http.post(`/note/share`, share) };