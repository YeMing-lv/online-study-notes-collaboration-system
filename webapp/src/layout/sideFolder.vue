<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-03-11 14:36:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-29 16:00:44
 * @FilePath: \webapp\src\layout\sideFolder.vue
 * @Description: 
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
                <el-icon v-if="route.params.type == 'folder' && fileList.length != 0"
                    style="padding-top: 5px;cursor: pointer;" @click="backToParentFolder()">
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
            <div class="file" :style="getFileStyle(file)" v-else v-for="file in fileList" :key="file.id"
                @click="handleFileClick(file)">
                <div class="file-title">
                    <img :src="file.type === 1 ? getImgUrl('folder.png') : getImgUrl('markdown.png')" alt="">
                    <span class="title">{{ file.name || file.title || '未命名' }}</span>
                    <el-icon v-if="file?.isStar == 1 && route.params.type != 'share' && route.params.type != 'recycle'"
                        style="margin-right: 10px;" @click.stop="handleUnStar(file)">
                        <StarFilled />
                    </el-icon>
                    <popover placement="right">
                        <el-icon class="more" v-if="route.params.type !== 'recycle'">
                            <MoreFilled />
                        </el-icon>
                        <template #content>
                            <ul>
                                <li @click="toTheFolder(file)" style="border-bottom: 1px solid #9f9f9f;"
                                    v-if="route.params.type == 'new' || route.params.type == 'star'">打开所在文件夹</li>
                                <!-- <li @click="renameFile(file.title || file.name)">重命名</li> -->
                                <li @click="showHistoryVersion(file)" v-if="file?.title">历史版本</li>
                                <li @click="toMoveFile(file)">移动到</li>
                                <li @click="deleteFile(file)" style="border-bottom: 1px solid #9f9f9f;">删除</li>
                                <li @click="shareNoteDisplay = true"
                                    v-if="route.params.type == 'new' || route.params.type == 'folder'">分享</li>
                                <li @click="toStarNote(file)">加星</li>
                            </ul>
                        </template>
                    </popover>
                    <el-icon class="more" v-if="route.params.type == 'recycle'" @click.stop="recoverFile(file)">
                        <Refresh />
                    </el-icon>
                    <el-icon class="more" v-if="route.params.type == 'recycle'" @click.stop="deleteComplete(file)">
                        <Delete />
                    </el-icon>
                </div>
                <div class="file-content" v-if="file.content">
                    {{ file.content.replace(/<[^>]+>/g, '') }}
                </div>
                <div class="file-other">
                    <span>{{ formatTime(file.updateTime, 'YYYY-MM-DD') }}</span>
                    <span v-if="route.params.type == 'share'">
                        {{ file.share?.sharePermission == 1 ? '只读' : '可编辑' }}</span>
                </div>
            </div>
        </div>
        <el-divider direction="horizontal" content-position="center">总共{{ pageParam.total }}项</el-divider>
        <note-history v-model:display="noteVersionDisplay" v-model:version-list="versionList" :note="historyNote"
            @cover-note="handleCoverNote" @edit-version="handleEditVersion(currentNote)"></note-history>
        <share-note v-model:display="shareNoteDisplay" :noteId="currentEdit.id"
            :userId="userStore.user.id"></share-note>
        <move-file v-model:display="moveDialogDisplay" :folders="folderStore.folders" :handle-type="'move'"
            :file-name="moveFileData?.name || moveFileData?.title" :folder-or-note="moveFileData"
            :file-type="moveFileType"></move-file>
        <ShowRecycleOfFileList v-model:display="recycleFolderListDisplay" :folder="selectRecycleFolderData">
        </ShowRecycleOfFileList>
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
import { Refresh, Search, StarFilled } from '@element-plus/icons-vue';
import { getNoteById, deleteNote, listNote, getNoteVersionList } from '@/api/apis/note';
import { useRoute, useRouter } from 'vue-router';
import noteHistory from './components/noteHistory.vue';
import shareNote from './components/shareNote.vue';
import { starNote, updateNote } from '../api/apis/note.js';
import moveFile from './components/moveFile.vue';
import { listRecycleFileByUserId } from '../api/apis/file.js';
import { deleteFolder, updateFolder } from '../api/apis/folder.js';
import ShowRecycleOfFileList from './components/showRecycleOfFileList.vue';

//=======================================data===================================
const route = useRoute();
const router = useRouter();
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

const noteVersionDisplay = ref(false);
const versionList = ref([]);

const shareNoteDisplay = ref(false);

// 加星笔记数据
const starNoteList = ref([]);

const moveDialogDisplay = ref(false)
// 要移动的文件数据
const moveFileData = ref({ name: '' });
const moveFileType = ref();
// 被查看历史版本的笔记数据
const historyNote = ref();

const recycleFolderListDisplay = ref(false);

const selectRecycleFolderData = ref({ id: 10 });

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
        handleNoteType(route.params.type);
        folderStore.isRefreshFolder = false;
    }
})

// 判断需要什么类型的
watch(() => route.params.type, async (newV, oldV) => {
    handleNoteType(newV);
})

//========================================methods======================================
// 根据不同分类的列表获取
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
            // console.log(result)
            if (result?.code === 200) {
                fileList.value = result.data.slice(0, 6);
                pageParam.value.total = fileList.value.length;
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
                type: 'star',
                keyword: '',
                ...pageParam.value
            }
            const result1 = await listNote(newListQuery1);
            if (result1?.code === 200) {
                fileList.value = result1.data.slice(0, 6);
                pageParam.value.total = fileList.value.length;
            }
            break;
        case 'recycle': {
            const query = {
                userId: userStore.user.id,
                folderId: 0,
                ...pageParam.value,
                size: 15,
            };
            // 添加关键词搜索
            if (searchInput.value != null) {
                query.keyword = searchInput.value;
            }
            const result = await listRecycleFileByUserId(query);
            if (result.code === 200) {
                const { records, ...page } = result.data;
                fileList.value = records;
                pageParam.value = page;
            }
            break;
        }
        case 'share':
            const newListQuery2 = {
                userId: userStore.user.id,
                type: 'share',
                keyword: '',
                ...pageParam.value
            }
            const result2 = await listNote(newListQuery2);
            if (result2?.code === 200) {
                fileList.value = result2.data.slice(0, 4);
                pageParam.value.total = 4;
            }
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
    if (route.params.type == 'folder') {
        switch (file.type) {
            case 1:
                ElMessageBox.confirm('确认要删除该文件夹吗？', '删除文件夹')
                    .then(async () => {
                        const result = await deleteFolder(file.id);
                        if (result.code === 200) {
                            searchFile();
                            currentEditStore.currentEdit = {};
                        }
                    }).catch((err) => console.error('Failed to delete Note: ' + err))
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
    } else {
        ElMessageBox.confirm('确认要删除该笔记吗？', '删除笔记')
            .then(async () => {
                const result = await deleteNote(file.id);
                if (result.code === 200) {
                    searchFile();
                    currentEditStore.currentEdit = {};
                }
            }).catch((err) => console.error('Failed to delete Note: ' + err))
    }
}

// TODO 文件列表的移动
// 移动
const toMoveFile = (file) => {
    moveFileData.value = file;
    if (file?.title) {
        moveFileType.value = 'note';
    } else {
        moveFileType.value = 'folder';
    }
    moveDialogDisplay.value = true;
}

// 处理文件的点击
const handleFileClick = async (data) => {
    if (data.id === currentEdit.value.id || !data.id) {
        return;
    }
    // debugger
    if (route.params.type == 'share') {
        currentEditStore.setCurrentEdit(data);
    } else if (route.params.type == 'recycle' && data?.type == 1) {
        recycleFolderListDisplay.value = true;
        selectRecycleFolderData.value = data;
    } else if (route.params.type !== 'folder' && route.params.type !== 'recycle') {
        const result = await getNoteById(data.id);
        if (result?.code === 200) {
            currentEditStore.setCurrentEdit(result.data);
        } else {
            console.error("GetNoteByID Failed: " + result.message);
            ElMessage.warning("查询笔记失败！");
        }
    } else {
        if (!data.type) return;
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

// 查看历史版本
const showHistoryVersion = async (note) => {
    const result = await getNoteVersionList(note.id);
    if (result.code === 200) {
        versionList.value = result.data;
        historyNote.value = note;
        noteVersionDisplay.value = true;
    }
}

// 加星笔记
const toStarNote = async (note) => {
    if (note.isStar == 1) {
        ElMessage.info("已加星");
        return;
    }
    const newNote = {
        ...note,
        isStar: 1,
    }
    const result = await updateNote(newNote);
    if (result.code === 200) {
        searchFile();
        ElMessage.success("加星笔记成功！");
    } else {
        ElMessage.warning("加星笔记失败！");
    }
}

// 取消加星笔记
const handleUnStar = async (note) => {
    const newNote = {
        ...note,
        isStar: 0
    }
    const result = await updateNote(newNote);
    if (result.code === 200) {
        handleNoteType(route.params.type);
        // ElMessage.success("取消加星笔记成功！");
    } else {
        ElMessage.warning("取消加星笔记失败！");
    }
}

// 跳转笔记所在文件夹
const toTheFolder = async (file) => {
    router.push({
        name: 'Note',
        params: { type: 'folder' }
    });
    const result = await getFolderByID(file.folderId);
    if (result.code == 200) {
        folderStore.setCurrentFolder(result.data);
    } else {
        console.error("getFolderById Faile: " + result.message);
    }
}

// 判断笔记覆盖
const handleCoverNote = async () => {
    debugger
    if (historyNote.value.id == currentEdit.value.id) {
        const result = await getNoteById(data.id);
        if (result?.code === 200) {
            currentEditStore.setCurrentEdit(result.data);
        }
    }
}

// 版本编辑成功，刷新表格
const handleEditVersion = async (currentNote) => {
    const result = await getNoteVersionList(currentNote.id);
    if (result.code === 200) {
        versionList.value = result.data;
    }
}

// 恢复回收站文件
const recoverFile = async (file) => {
    const newFile = {
        ...file,
        isRecycle: 0
    };
    if (file.type == 0) {
        const result = await updateNote(newFile);
        if (result?.code === 200) {
            ElMessage.success("恢复笔记成功！");
            handleNoteType('recycle');
        }
    } else if (file.type == 1) {
        const result = await updateFolder(newFile);
        if (result?.code === 200) {
            ElMessage.success("恢复文件夹成功！");
            handleNoteType('recycle');
        }
    }
}

// 彻底删除文件
const deleteComplete = async (file) => {
    const newFile = {
        ...file,
        isDeleted: 1,
    }
    ElMessageBox.confirm('确认要彻底删除该文件吗？', '彻底删除文件')
        .then(async () => {
            switch (file.type) {
                case 0: {
                    const result = await updateNote(newFile);
                    if (result.code === 200) {
                        ElMessage.success("已彻底删除笔记！");
                        handleNoteType('recycle');
                    }
                    break;
                }
                case 1: {
                    const result = await updateFolder(newFile);
                    if (result.code === 200) {
                        ElMessage.success("已彻底删除文件夹！");
                        handleNoteType('recycle');
                    }
                    break;
                }
            }
        }).catch((err) => console.error('Failed to delete Note: ' + err))
}

// 工具：去掉 HTML 标签，获取纯文本
const getPlainText = (html) => {
    if (!html) return ''
    return html.replace(/<[^>]+>/g, '').trim()
}

// 工具：计算文本大概多少行（根据容器宽度 220px 估算）
const getLineCount = (text) => {
    if (!text) return 0
    const len = text.length
    // 侧边栏宽度约 220px，中文字符每行大约 32 个字
    return Math.ceil(len / 32)
}

// 动态控制文件高度（按笔记内容行数）
const getFileStyle = (file) => {
    // 文件夹：不设置高度
    if (file?.type === 1) return {}

    // 笔记：根据内容行数动态设置高度
    const text = getPlainText(file.content)
    const line = getLineCount(text)

    if (line <= 1) {
        return { height: '70px' }    // 1行：很矮
    } else if (line <= 2) {
        return { height: '90px' }   // 2行：中等
    } else {
        return { height: '110px' }  // 3行以上：最高
    }
}

</script>

<style lang="scss">
@use './css/sideFolderOfPop.scss';
</style>

<style lang="scss" scoped>
@use "css/sideFolder.scss";
</style>