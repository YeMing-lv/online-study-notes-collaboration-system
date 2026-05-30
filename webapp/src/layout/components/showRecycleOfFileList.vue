<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-05-03 13:57:58
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-29 15:52:44
 * @FilePath: \webapp\src\layout\components\showRecycleOfFileList.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-dialog title="回收站" v-model="display" width="300" :append-to-body="true">
        <div style="display: flex;align-items: center;">
            <img style="width: 25px;margin-right: 5px;" :src="getImgUrl('folder.png')">
            <span style="font-size: 16px;">{{ props.folder.name }}</span>
        </div>
        <div class="folders-wrapper">
            <div class="file" v-for="file in fileList" :key="file.id">
                <img style="height: 20px;" :src="file.type === 1 ? getImgUrl('folder.png') : getImgUrl('markdown.png')"
                    :alt="getImgUrl('markdown.png')">
                <span class="title">{{ file.name || file.title || '未命名' }}</span>
            </div>
        </div>
    </el-dialog>
</template>

<script setup>
import { onMounted, ref, watch } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';
import { updateFolder } from '../../api/apis/folder';
import { ElMessage } from 'element-plus';
import { updateNote } from '../../api/apis/note';
import { useFolderStore } from '../../store/folder';
import { useUserStore } from '../../store/user';
import { listFileByFolderId } from '../../api/apis/file';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['folder']);

// 分页
const pageParam = ref({
    current: 1,
    size: 10,
    pages: 0,
    total: 0
})
const fileList = ref([]);
const folderStore = useFolderStore();
const userStore = useUserStore();

const searchFile = async () => {

    try {
        const query = {
            userId: userStore.user.id,
            folderId: props.folder.id,
            ...pageParam.value
        };
        const result = await listFileByFolderId(query);
        if (result.code === 200) {
            const { records, ...page } = result.data;
            fileList.value = records;
            pageParam.value = page;
        }
    } catch (error) {
        console.log('获取文件夹的文件列表出错 :>> ', error);
    }
}

onMounted(() => {
    searchFile();
})

watch(() => props.folder, (newV, oldV) => {
    if (newV.id) {
        searchFile();
    }
})

</script>

<style lang="scss" scoped>
.folders-wrapper {
    margin: 10px 0px;
    padding-top: 5px;
    padding-left: 10px;
    border: 1px solid #efefef;
    height: 300px;
    overflow-y: scroll;
    display: flex;
    flex-direction: column;


    .file {
        display: flex;
        align-items: center;
        margin-bottom: 5px;

    }
}
</style>