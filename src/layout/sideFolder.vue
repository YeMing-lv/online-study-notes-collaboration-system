<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 11:17:14
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-26 16:57:47
 * @FilePath: \online-study-notes-collaboration-system\src\layout\sideFolder.vue
 * @Description: 侧边的文件夹导航栏，能进行条件搜索、文件夹导航、显示文件夹内的文件
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="folder-container">
        <div class="search">
            <el-col :span="18">
                <el-input prefix-icon="Search" v-model="searchInput" placeholder="搜索笔记" clearable></el-input>
            </el-col>
            <el-popover popper-class="search-cond-pop" :visible="displaySearchCondPop" placement="bottom">
                <template #reference>
                    <el-icon :size="22" @click="displaySearchCondPop = true">
                        <Operation />
                    </el-icon>
                </template>
                <div class="search-cond__switch-wrapper" v-click-outside="() => displaySearchCondPop = false">
                    <ul>
                        <li v-for="item in orderCondList" :key="item.name" @click="selectOrderCond(item.value)">
                            <span>{{ item.name }}</span>
                            <el-icon v-if="currentOrderCond === item.value">
                                <SortDown v-if="orderByDESC" />
                                <SortUp v-else />
                            </el-icon>
                        </li>
                    </ul>
                </div>
            </el-popover>
        </div>
        <div class="menu">
            menu
        </div>
        <div class="files">
            <div class="default-img" v-if="fileList.length === 0">
                <img src="../assets/file-background.png">
            </div>
            <div class="file" v-else v-for="file in fileList" :key="file.id">
                <div class="file-title">
                    <img :src="file.type === 1 ? getImgUrl('folder.png') : getImgUrl('markdown.png')" alt="">
                    <span class="title">{{ file.name || file.title }}</span>
                    <!-- <el-icon class="more">
                        <MoreFilled />
                    </el-icon> -->
                    <popover>
                        <el-icon class="more">
                            <MoreFilled />
                        </el-icon>
                    </popover>
                </div>
                <div class="file-content" v-if="file.content">
                    {{ changeHTMLToText(file.content) }}
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
import { listFileByFolderId } from '@/api/apis/file.js';
import { formatTime } from '@/utils/timeHandle.js';
import { getImgUrl } from '../utils/assetsImport.js';
import popover from '../components/popover.vue';

/**
 * data
 * -----------------------------------------------------------------
 */
// 搜索栏
const searchInput = ref();
// 显示筛选条件弹框
const displaySearchCondPop = ref(false);
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
const userStore = useUserStore();
// 分页
const pageParam = ref({
    current: 1,
    size: 10,
    pages: 0,
    total: 0
})
// 文件列表
const fileList = ref([]);


/**
 * 钩子函数
 * ————————————————————————————————————————————————————————————————————————————
 */

/**
 * 侦听器
 * ————————————————————————————————————————————————————————————————————————————————————————
 */
watch(currentFolder, (newV, oldV) => {
    // console.log(newV, oldV);
    if (currentFolder.value) {
        searchFile();
    }
})
watch([currentOrderCond, orderByDESC], ([newOrder, newDesc], [oldOrder, oldDesc]) => {
    if (currentOrderCond) {
        if (oldOrder) {

        }
    }
})

/**
 * methods
 * ————————————————————————————————————————————————————————————————————————————————————————————————————————
 */
// TODO 搜索文件夹内 文件
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
    // TODO 完成排序
    if (currentOrderCond.value === orderCond) {
        orderByDESC.value = !orderByDESC.value;
    } else {
        currentOrderCond.value = orderCond;
    }
    searchFile();
}

// HTML改为纯文字
const changeHTMLToText = (html) => {
    return html.replace(/<[^>]+>/g, '');
}

</script>

<style lang="scss" scoped>
.folder-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 250px;
    border: 1px solid var(--el-border-color);

    .search {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 10px;

        .el-input {
            flex-grow: 2;
        }

        .el-icon:hover {
            cursor: pointer;
            background-color: #efefef;
        }

    }

    .files {
        display: flex;
        height: 100%;
        flex-direction: column;
        align-items: center;
        overflow: hidden;
        overflow-y: scroll;

        &::-webkit-scrollbar {
            width: 0 !important;
            height: 0 !important;
            background: transparent !important;
            border: none !important;
        }

        &::-webkit-scrollbar-track {
            background-color: transparent;
        }

        .default-img {
            display: flex;
            flex-direction: column;
            align-items: center;
            flex-grow: 2;
            padding: 200px 0px;

            img {
                width: 60%;
            }
        }

        .file {
            width: 100%;
            margin-bottom: 5px;
            min-height: 50px;
            padding: 5px;
            cursor: pointer;
            overflow: hidden;

            &:hover {
                background: #efefef;
            }

            .file-title {
                display: flex;
                align-items: center;
                font-size: 14px;
                margin-bottom: 5px;

                img {
                    height: 20px;
                    margin-right: 5px;
                }

                .title {
                    flex-grow: 2;
                }

                .more {
                    margin-right: 10px;

                    &:hover {
                        background-color: #afafaf;
                        border-radius: 3px;
                    }
                }
            }

            .file-content {
                display: -webkit-box;
                -webkit-line-clamp: 3;
                -webkit-box-orient: vertical;
                overflow: hidden;
                word-break: break-all;
                font-size: 12px;
                color: #808080;
                padding-left: 20px;
                margin-bottom: 5px;
                margin-right: 5px;
            }

            .file-other {
                font-size: 12px;
                margin: 0px 5px;
            }

        }
    }
}
</style>

<style lang="scss">
.search-cond-pop {
    padding: 0px !important;

    ul {
        padding: 5px 0px;
        list-style-type: none;
        user-select: none;

        li {
            display: flex;
            justify-content: space-between;
            padding: 5px;
            cursor: pointer;
            font-size: small;
        }

        li:hover {
            background-color: #efefef;
        }
    }
}
</style>