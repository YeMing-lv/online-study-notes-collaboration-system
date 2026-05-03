<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-03-11 14:36:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-03 17:52:58
 * @FilePath: \webapp\src\layout\sideFolder.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 11:17:14
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-03 15:32:10
 * @FilePath: \webapp\src\layout\sideFolder.vue
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
        <el-row justify="center" :gutter="20">
            <el-col :span="2">
                <el-icon style="padding-top: 5px;cursor: pointer;" @click="backToParentFolder()">
                    <ArrowLeftBold />
                </el-icon>
            </el-col>
            <el-col :span="22">
                <span v-if="currentFolder.id !== 0">{{ currentFolder.name }}</span>
            </el-col>
        </el-row>
        <div class="files">
            <div class="default-img" v-if="fileList.length === 0">
                <img src="../assets/file-background.png">
            </div>
            <div class="file" v-else v-for="file in fileList" :key="file.id" @click="handleFileClick(file)">
                <div class="file-title">
                    <img :src="file.type === 1 ? getImgUrl('folder.png') : getImgUrl('markdown.png')" alt="">
                    <span class="title">{{ file.name || file.title || '未命名' }}</span>
                    <!-- <el-icon style="margin-right: 10px;">
                        <StarFilled />
                    </el-icon> -->
                    <popover placement="right">
                        <el-icon class="more">
                            <MoreFilled />
                        </el-icon>
                        <template #content>
                            <ul>
                                <li style="border-bottom: 1px solid #9f9f9f;">打开所在文件夹</li>
                                <li @click="renameFile(file.title || file.name)">重命名</li>
                                <li @click="showHistoryVersion(file.id)">历史版本</li>
                                <li>移动到</li>
                                <li @click="deleteFile(file)" style="border-bottom: 1px solid #9f9f9f;">删除</li>
                                <li @click="shareNoteDisplay = true">分享</li>
                                <li>加星</li>
                            </ul>
                        </template>
                    </popover>
                </div>
                <div class="file-content" v-if="file.content">
                    {{ file.content.replace(/<[^>]+>/g, '') }}
                </div>
                <div class="file-other">
                    <span>{{ formatTime(file.updateTime, 'YYYY-MM-DD') }}</span>
                    <span>只读</span>
                </div>
            </div>
        </div>
        <el-divider direction="horizontal" content-position="center">总共{{ pageParam.total }}项</el-divider>
        <note-history v-model:display="noteVersionDisplay" :version-list="versionList"></note-history>
        <share-note v-model:display="shareNoteDisplay" :noteId="currentEdit.id"
            :userId="userStore.user.id"></share-note>
    </div>
</template>
<script setup>
import { ref, reactive, computed, onMounted, watch, version } from 'vue';
import { ElMessage, ElMessageBox, ClickOutside as VClickOutside } from 'element-plus';
import { useFolderStore } from '@/store/folder';
import { useUserStore } from '@/store/user.js';
import { useCurrEditStore } from '@/store/currentEdit';
import { listFileByFolderId } from '@/api/apis/file.js';
import { getFolderByID } from '@/api/apis/folder.js';
import { formatTime } from '@/utils/timeHandle.js';
import { getImgUrl } from '@/utils/assetsImport.js';
import popover from '@/components/popover.vue';
import { Search, StarFilled } from '@element-plus/icons-vue';
import { getNoteById, deleteNote, listNote, getNoteVersionList } from '@/api/apis/note';
import { useRoute } from 'vue-router';
import noteHistory from './components/noteHistory.vue';
import shareNote from './components/shareNote.vue';

//=======================================data===================================
const route = useRoute();
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
const isRefreshFolder = computed(() => folderStore.isRefreshFolder);
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

// 回收站的数据
const recycleFolder = {
    createTime: "2025-12-30T15:53:22",
    deleteTime: null,
    id: 30045,
    isDeleted: 0,
    isRecycle: 0,
    name: "学习资料11",
    parentId: 0,
    type: 1,
    recycleExpireTime: null,
    sort: 0,
    updateTime: "2026-05-03T13:53:28",
    userId: 10003,
}

const noteVersionDisplay = ref(false);
const versionList = ref([]);

const shareNoteDisplay = ref(false);

// 分享的笔记数据
const testNoteData = {
    type: 2,
    categoryId
        :
        null,
    content
        :
        "<p>12313</p>",
    coverUrl
        :
        null,
    createTime
        :
        "2026-04-30T17:12:02",
    deleteTime
        :
        null,
    folderId
        :
        30045,
    id
        :
        20017,
    isDeleted
        :
        0,
    isPublic
        :
        0,
    isRecycle
        :
        0,
    readCount
        :
        0,
    recycleExpireTime
        :
        null,
    title
        :
        "123",
    updateTime
        :
        "2026-05-02T07:13:07",
    userId
        :
        10003,
    permission: '1'
}

//========================================钩子函数========================================
onMounted(async () => {
    const listType = route.params.type;
    handleNoteType(listType);
})

//======================================侦听器============================================
watch(currentFolder, (newV, oldV) => {
    if (currentFolder.value && route.params.type === 'folder') {
        searchFile();
    }
    // console.log(currentFolder.value)
})

watch(isRefreshFolder, (newV) => {
    if (isRefreshFolder) {
        searchFile();
        folderStore.isRefreshFolder = false;
    }
})

// 判断需要什么类型的
watch(() => route.params.type, async (newV, oldV) => {
    handleNoteType(newV);
})

//========================================methods======================================

const handleNoteType = async (type) => {
    switch (type) {
        case 'new':
            const newListQuery = {
                userId: userStore.user.id,
                type: 'new',
                keyword: '',
                ...pageParam.value
            }
            const result = await listNote(newListQuery);
            if (result.code === 200) {
                fileList.value = result.data.slice(0, 6);
                pageParam.value.total = 6;
            }
            break;
        case 'folder':
            if (Object.keys(currentFolder.value).length != 0) {
                searchFile();
            }
            break;
        case 'star':
            const newListQuery1 = {
                userId: userStore.user.id,
                type: 'new',
                keyword: '',
                ...pageParam.value
            }
            const result1 = await listNote(newListQuery1);
            if (result1.code === 200) {
                fileList.value = result1.data.slice(0, 4);
                pageParam.value.total = 4;
            }
            break;
        case 'recycle':
            fileList.value = [];
            fileList.value.push(recycleFolder);
            pageParam.value.total = 1;
            break;
        case 'share':
            fileList.value = [];
            fileList.value.push(testNoteData);
            pageParam.value.total = 1;
            break;
    }
}

/**
 * @description: 搜索文件列表
 * @return {*}
 */
const searchFile = async () => {
    try {
        const query = {
            userId: userStore.user.id,
            folderId: currentFolder.value.id,
            ...pageParam.value
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
const deleteFile = async (file) => {
    switch (file.type) {
        case 1:

            break;
        case 2:
            ElMessageBox.confirm('确认要删除该笔记吗？', '删除笔记')
                .then(async () => {
                    const result = await deleteNote(file.id);
                    if (result.code === 200) {
                        searchFile();
                        currentEditStore.currentEdit = {};
                    }
                }).catch((err) => console.error('Failed to delete Note: ' + err))
            break;
    }
}

// TODO 文件列表的移动
// 移动
const moveFile = (id) => {
    // 修改文件的parentId父类ID。改完再重新搜索下。
}

// 处理文件的点击
const handleFileClick = async (data) => {
    if (route.params.type === 'new') {
        const result = await getNoteById(data.id);
        if (result?.code === 200) {
            currentEditStore.setCurrentEdit(result.data);
        } else {
            console.error("GetNoteByID Failed: " + result.message);
            ElMessage.warning("查询笔记失败！");
        }
    }
    if (!data.type || !data.id) return;
    switch (data.type) {
        // 文件夹
        case 1:
            folderStore.setCurrentFolder(data);
            break;
        // 笔记
        case 2:
            const result = await getNoteById(data.id);
            if (result?.code === 200) {
                currentEditStore.setCurrentEdit(result.data);
            } else {
                console.error("GetNoteByID Failed: " + result.message);
                ElMessage.warning("查询笔记失败！");
            }
            break;
    }
}

// 回到上一级文件夹
const backToParentFolder = async () => {
    if (currentFolder.value.parentId != 0) {
        const result = await getFolderByID(currentFolder.value.parentId);
        if (result.code == 200) {
            folderStore.setCurrentFolder(result.data);
        } else {
            console.error("getFolderById Faile: " + result.message);
        }
    } else {
        folderStore.setCurrentFolder({
            id: 0,
            name: ''
        })
    }
}

const showHistoryVersion = async (noteId) => {
    const result = await getNoteVersionList(noteId);
    if (result.code === 200) {
        versionList.value = result.data;
        noteVersionDisplay.value = true;
    }
}


</script>

<style lang="scss">
@use './css/sideFolderOfPop.scss';
</style>

<style lang="scss" scoped>
@use "css/sideFolder.scss";
</style>