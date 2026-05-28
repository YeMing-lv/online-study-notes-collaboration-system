<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-05-03 13:57:58
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-28 16:36:18
 * @FilePath: \webapp\src\layout\components\moveFile.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-dialog :title="props.handleType == 'move' ? '移动文件' : '保存笔记'" v-model="display" width="300"
        :append-to-body="true">
        <div style="display: flex;align-items: center;">
            <img style="width: 20px;"
                :src="props.fileType === 'folder' ? getImgUrl('folder.png') : getImgUrl('markdown.png')">
            {{ props.fileName }}
        </div>
        <div style="margin-top: 10px;color: #7f7f7f;">移动到：</div>
        <div class="folders-wrapper">
            <el-tree ref="treeFoldersRef" :data="props.folders" node-key="id" :indent="8" :highlight-current="true"
                @node-click="handleFolderClick">
                <template #default="{ node, data }">
                    <div class="tree-content">
                        <span> {{ data.name }} </span>
                    </div>
                </template>
            </el-tree>
        </div>

        <span slot="footer">
            <el-button @click="display = false">取消</el-button>
            <el-button v-if="props.handleType == 'move'" type="primary" @click="handleMoveFile()">移动</el-button>
            <el-button v-if="props.handleType == 'save'" type="primary" @click="handleSaveNote()">保存</el-button>
        </span>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';
import { updateFolder } from '../../api/apis/folder';
import { ElMessage } from 'element-plus';
import { updateNote } from '../../api/apis/note';
import { useFolderStore } from '../../store/folder';
import { useUserStore } from '../../store/user';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['folders', 'handleType', 'fileType', 'fileName', 'folderOrNote']);

const selectFolder = ref({ id: 0, name: '' });

const folderStore = useFolderStore();
const userStore = useUserStore();

// 移动文件
const handleMoveFile = async () => {
    // debugger
    switch (props.fileType) {
        case 'folder':
            const newFolder = JSON.parse(JSON.stringify(props.folderOrNote));
            newFolder.parentId = selectFolder.value.id;
            const result = await updateFolder(newFolder);
            if (result?.code === 200) {
                display.value = false;
                ElMessage.success("移动成功！");
                folderStore.isRefreshFolder = true;
                folderStore.isRefreshTree = true;
            }
            break;
        case 'note':
            const newNote = JSON.parse(JSON.stringify(props.folderOrNote));
            newNote.folderId = selectFolder.value.id;
            const result1 = await updateNote(newNote);
            if (result1?.code === 200) {
                display.value = false;
                ElMessage.success("移动成功！");
                folderStore.isRefreshFolder = true;
                folderStore.isRefreshTree = true;
            }
            break;
    }
}

// 选择文件夹
const handleFolderClick = (data, node, tree, event) => {
    selectFolder.value = { ...data, children: [] };
}

// 保存笔记
const handleSaveNote = async () => {
    const newNote = {
        userId: userStore.user.id,
        folderId: selectFolder.value.id,
        title: props.folderOrNote.title,
        content: props.folderOrNote.content
    };
    const result = await updateNote(newNote);
    if (result?.code === 200) {
        display.value = false;
        ElMessage.success("保存成功！");
        folderStore.isRefreshFolder = true;
        folderStore.isRefreshTree = true;
    }
}
</script>

<style lang="scss" scoped>
.folders-wrapper {
    margin: 10px 0px;
    border: 1px solid #efefef;
    height: 250px;
    overflow-y: scroll;
}
</style>