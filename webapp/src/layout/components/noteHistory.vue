<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-05-03 15:36:08
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-28 17:05:56
 * @FilePath: \webapp\src\layout\components\noteHistory.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-dialog title="历史版本" v-model="display" width="80%" :append-to-body="true">
        <el-table style="margin: 10px 0px;" :data="versionList" height="300" border stripe>
            <el-table-column label="保存时间" width="160">
                <template #default="scope">
                    {{ formatTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip></el-table-column>
            <el-table-column label="内容" show-overflow-tooltip>
                <template #default="scope">
                    {{ scope.row?.content?.replace(/<[^>]+>/g, '') }}
                </template>
            </el-table-column>
            <el-table-column prop="editRemark" label="备注" width="180" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" size="default" @click="toSaveVersion(scope.row)">保存</el-button>
                    <el-button size="default" @click="toCoverVersion(scope.row)">覆盖</el-button>
                    <el-button size="default" @click="toEdit(scope.row)">编辑</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="编辑笔记历史版本" v-model="editDisplay" width="80%" :append-to-body="true">
            <el-form :model="form" ref="formRef" label-width="120px" :inline="false" size="normal">
                <el-row :gutter="20">
                    <el-col :span="12" :offset="0">
                        <el-form-item label="创建时间：">
                            <el-input :value="formatTime(form.createTime)" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :offset="0">
                        <el-form-item label="备注：">
                            <el-input v-model="form.editRemark" clearable></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="标题：">
                    <el-input v-model="form.title" disabled></el-input>
                </el-form-item>
                <el-form-item label="内容：">
                    <el-input :value="form.content?.replace(/<[^>]+>/g, '')" disabled type="textarea"></el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="onSubmit">保存</el-button>
                    <el-button @click="cancelEdit">取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
        <move-file v-model:display="moveDialogDisplay" :folders="folderStore.folders" :handle-type="'save'"
            :folder-or-note="form" :file-name="form.title" :file-type="'note'"></move-file>
    </el-dialog>
</template>

<script setup>
import { nextTick, ref } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';
import { formatTime } from '../../utils/timeHandle';
import moveFile from './moveFile.vue';
import { useFolderStore } from '../../store/folder.js';
import { ElMessage, ElMessageBox } from 'element-plus';
import { saveNoteVersion, updateNote } from '../../api/apis/note.js';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['versionList', 'note'])
const emits = defineEmits(['cover-note'])

const editDisplay = ref(false)

const form = ref({});

const moveDialogDisplay = ref(false);

const folderStore = useFolderStore();

const coverDisplay = ref(false);

const toEdit = (version) => {
    display.value = false;
    form.value = JSON.parse(JSON.stringify(version));
    editDisplay.value = true;
}

const toSaveVersion = async (version) => {
    display.value = false;
    form.value = JSON.parse(JSON.stringify(version));
    moveDialogDisplay.value = true;
}

// 取消版本编辑
const cancelEdit = () => {
    editDisplay.value = false;
    nextTick();
    form.value = {};
    display.value = true;
}

// 覆盖现版本笔记
const toCoverVersion = async (version) => {
    ElMessageBox.confirm(
        '确认要覆盖现版本笔记吗？覆盖后，现版本仍会保存在版本表格中。',
        '覆盖现版本笔记',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(async () => {
        const result1 = await saveNoteVersion({
            noteId: props.note.id,
            title: props.note.title,
            content: props.note.content,
        });
        if (result1?.code === 200) {
            const newNote = JSON.parse(JSON.stringify(props.note));
            newNote.title = version.title;
            newNote.content = version.content;
            const result = await updateNote(newNote);
            if (result?.code === 200) {
                ElMessage.success("笔记已覆盖！");
                display.value = false;
                folderStore.isRefreshFolder = true;
                emits('cover-note');
            }
        }
    }).catch(() => { })
}

</script>

<style lang="scss" scoped></style>