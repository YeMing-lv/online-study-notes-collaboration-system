<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 11:17:14
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-02-05 14:31:49
 * @FilePath: \online-study-notes-collaboration-system\src\layout\sideFolder.vue
 * @Description: 侧边的文件夹导航栏，能进行条件搜索、文件夹导航、显示文件夹内的文件
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="folder-container">
        <div class="search">
            <el-col :span="18">
                <el-input v-model="searchInput" placeholder="搜索笔记" clearable @keydown.enter="searchFile()"
                    @clear="searchFile()">
                    <template #append>
                        <el-button type="primary" @click="searchFile()">
                            <el-icon>
                                <Search />
                            </el-icon>
                        </el-button>
                    </template>
                </el-input>
            </el-col>
            <popover>
                <el-icon :size="22">
                    <Operation />
                </el-icon>
                <template #content>
                    <ul>
                        <li v-for="item in orderCondList" :key="item.name" @click="selectOrderCond(item.value)">
                            <span>{{ item.name }}</span>
                            <el-icon v-if="currentOrderCond === item.value">
                                <SortDown v-if="orderByDESC" />
                                <SortUp v-else />
                            </el-icon>
                        </li>
                    </ul>
                </template>
            </popover>
        </div>
        <!-- TODO 文件夹面包屑，有点难搞，宽度不够，等后面把布局拖拽做了再来弄 -->
        <!-- <el-breadcrumb separator=">">
            <el-breadcrumb-item v-for="item in folderRoutes" :key="item.id" :to="{ path: '', name: '' }">
                {{ item.name }}
            </el-breadcrumb-item>
        </el-breadcrumb> -->
        <span class="folder-title" v-if="currentFolder.id !== 0">{{ currentFolder.name }}</span>
        <div class="files">
            <div class="default-img" v-if="fileList.length === 0">
                <img src="../assets/file-background.png">
            </div>
            <div class="file" v-else v-for="file in fileList" :key="file.id"
                @click="handleFileClick(file)">
                <div class="file-title">
                    <img :src="file.type === 1 ? getImgUrl('folder.png') : getImgUrl('markdown.png')" alt="">
                    <span class="title">{{ file.name || file.title }}</span>
                    <popover placement="right">
                        <el-icon class="more">
                            <MoreFilled />
                        </el-icon>
                        <template #content>
                            <ul>
                                <li @click="renameFile(file.title || file.name)">重命名</li>
                                <li>删除</li>
                            </ul>
                        </template>
                    </popover>
                </div>
                <div class="file-content" v-if="file.content">
                    {{ file.content }}
                </div>
                <div class="file-other">{{ formatTime(file.createTime, 'YYYY-MM-DD') }}</div>
            </div>
        </div>
    </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted, watch } from 'vue';
import { ClickOutside as VClickOutside } from 'element-plus';
import { useFolderStore } from '../store/folder';
import { useUserStore } from '../store/user.js';
import { useCurrEditStore } from '@/store/currentEdit';
import { listFileByFolderId } from '@/api/apis/file.js';
import { formatTime } from '@/utils/timeHandle.js';
import { getImgUrl } from '../utils/assetsImport.js';
import popover from '../components/popover.vue';
import { Search } from '@element-plus/icons-vue';

//=======================================data===================================
// 搜索栏
const searchInput = ref();
// 当前排序条件
const currentOrderCond = ref();
// 排序条件列表
const orderCondList = reactive([
    {
        name: '创建时间',
        value: 'orderByCreateDesc'
    },
    {
        name: '更新时间',
        value: 'orderByUpdateDesc'
    },
    {
        name: '文件类型',
        value: 'orderByTypeDesc'
    }
]);
// 是否降序
const orderByDESC = ref(true);

// 当前文件夹
const folderStore = useFolderStore();
const currentFolder = computed(() => folderStore.currentFolder);
// 用户
const userStore = useUserStore();
// 编辑文件
const currentEditStore = useCurrEditStore();
const currentEdit = computed(() => currentEditStore.currentEdit);
// 分页
const pageParam = ref({
    current: 1,
    size: 10,
    pages: 0,
    total: 0
})
// 文件列表
const fileList = ref([]);


//========================================钩子函数========================================

//======================================侦听器============================================
watch(currentFolder, (newV, oldV) => {
    // console.log(newV, oldV);
    if (currentFolder.value) {
        searchFile();
    }
})

//========================================methods======================================

/**
 * @description: 搜索文件列表
 * @return {*}
 */
const searchFile = async () => {
    try {
        const query = {
            userId: userStore.user.id,
            folderId: currentFolder.value.id,
            ...pageParam
        };
        // 添加排序条件
        if (currentOrderCond.value !== null) {
            query[currentOrderCond.value] = orderByDESC.value;
        }
        // 添加关键词搜索
        if (searchInput.value != null) {
            query.keyword = searchInput.value;
        }
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

// 选择排序条件
const selectOrderCond = (orderCond) => {
    if (currentOrderCond.value === orderCond) {
        orderByDESC.value = !orderByDESC.value;
    } else {
        currentOrderCond.value = orderCond;
    }
    searchFile();
}

// TODO 文件列表的重命名
// 重命名 
const renameFile = (name) => {

}

// TODO 文件列表的删除
// 删除
const deleteFile = (id) => {

}

// 处理文件的点击
const handleFileClick = (data) => {
    if (!data.type || !data.id) return;
    switch (data.type) {
        // 文件夹
        case 1:
            folderStore.setCurrentFolder(data);
        // 笔记
        case 2:
            currentEditStore.setCurrentEdit(data);
    }
}

</script>

<style lang="scss">
@use './css/sideFolderOfPop.scss';
</style>

<style lang="scss" scoped>
@use "css/sideFolder.scss";
</style>