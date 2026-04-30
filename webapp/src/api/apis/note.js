import { http } from "../http";

// 查询笔记
export const getNoteById = (id) => { return http.get(`/note?id=${id}`) };

// 创建或更新笔记
export const updateNote = (note) => { return http.post('/note', note) };

// 保存旧笔记
export const saveNoteVersion = (noteVersion) => { return http.post('/note/version/saveNoteVersion', noteVersion) };

// 查询笔记的历史版本
export const getNoteVersionList = (id) => { return http.get(`/note/version/getNoteVersionList?noteId=${id}`) };

// 删除
export const deleteNote = (id) => { return http.delete(`/note?id=${id}`) };

// 获取指定种类笔记列表
export const listNewNote = (noteQuery) => { return http.get(`/note/list`, noteQuery) }