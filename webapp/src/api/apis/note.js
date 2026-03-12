import { http } from "../http";

// 查询笔记
export const getNoteById = (id) => { return http.get(`/note?id=${id}`) };

// 更新笔记
export const updateNote = (note) => { return http.post('/note', note) };

// 保存旧笔记
export const saveNoteVersion = (noteVersion) => { return http.post('/note/version/saveNoteVersion', noteVersion) };

// 查询笔记的历史版本
export const getNoteVersionList = (id) => { return http.get(`/note/version/getNoteVersionList${id}`) };