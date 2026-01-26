<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 08:43:14
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-04 16:53:37
 * @FilePath: \online-study-notes-collaboration-system\src\views\File\file.vue
 * @Description: 主要页面，包含了侧边文件夹导航栏、侧边文件夹内容导航栏、文件编辑器
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="file-container">
        <side-bar :my-folders="folders" @create-file="handleCreateFile" @rename-file="handleRenameFile"
            @delete-file="handleDeleteFile"></side-bar>
        <side-folder></side-folder>
        <editor></editor>
    </div>
</template>
<script setup>
import sideBar from '@/layout/sideBar.vue';
import sideFolder from '@/layout/sideFolder.vue';
import editor from '../../layout/editor.vue';
import { listFolderByUserId } from '../../api/apis/folder';
import { onMounted, reactive } from 'vue';
import { useUserStore } from '../../store/user';
import { ElMessage } from 'element-plus';
import { updateFolder, createFolder, deleteFolder } from '../../api/apis/folder';

/**
 * data
 * ————————————————————————————————————————————————————————————————————————————————————
 */
const folders = reactive([]);
const userStore = useUserStore();

/**
 * 钩子函数
 * ——————————————————————————————————————————————————————————————————————————————
 */
onMounted(() => {
    getFolders();
})

/**
 * methods
 * ————————————————————————————————————————————————————————————————————————————————————
 */
// 获取用户个人文件夹
const getFolders = async () => {
    try {
        const result = await listFolderByUserId(userStore.user);
        if (result.code === 200) {
            folders.length = 0;
            folders.push(...result.data);
        } else {
            ElMessage.error('获取用户文件夹失败！');
        }
    } catch (err) {
        console.error('获取用户文件夹出错:' + err);
        ElMessage.error('获取用户文件夹失败！');
    }
}

// 重命名文件
const handleRenameFile = async (folder) => {
    try {
        const result = await updateFolder(folder);
        if (result.code === 200) {
            getFolders();
        }
    } catch (error) {
        console.error(error);
    }
}

// 新建文件
const handleCreateFile = async ({ type, data }) => {
    try {
        switch (type) {
            case 'Folder':
                const result = await createFolder({
                    id: data.id,
                    userId: userStore.user.id,
                    name: data.name,
                    parentId: data.parentId
                });
                if (result.code === 200) {
                    getFolders();
                }
        }
    } catch (error) {
        console.error(error);
    }
}

// 删除文件
const handleDeleteFile = async (id) => {
    try {
        const result = await deleteFolder(id);
        if (result.code === 200) {
            getFolders();
        }
    } catch (error) {
        console.error(error);
    }
}


</script>

<style lang="scss" scoped>
.file-container {
    display: flex;

}
</style>