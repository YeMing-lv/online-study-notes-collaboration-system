<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-03-11 14:36:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-03 14:58:00
 * @FilePath: \webapp\src\layout\sideBar.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 11:09:12
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-03 13:11:25
 * @FilePath: \webapp\src\layout\sideBar.vue
 * @Description: 侧边文件夹导航栏，包含了个人用户管理、新建文件、文件夹导航
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-menu ref="sideBarRef" :collapse="isCollapse" @select="handleMenuSelection">
        <user-avatar v-if="!isCollapse" @create="handleCreate"></user-avatar>
        <div class="side-bar-content">
            <el-menu-item index="userCenter"><el-icon>
                    <Avatar />
                </el-icon>
                <template #title>个人中心</template>
            </el-menu-item>
            <el-menu-item index="newFile"><el-icon>
                    <Menu />
                </el-icon>
                <template #title>最新</template>
            </el-menu-item>
            <el-sub-menu index="myFolder" @click="handleMenuSelection('myFolder')">
                <template #title>
                    <el-icon>
                        <Folder />
                    </el-icon>
                    <span>我的文件夹</span>
                    <el-icon :size="12" class="more-icon" :ref="(el) => collectRef(el, 0, 'more')"
                        @click.stop="handleMoreClick($event, { id: 0, name: '' })">
                        <More />
                    </el-icon>
                </template>
                <el-tree ref="treeFoldersRef" :data="myFolders" node-key="id" :indent="8"
                    :default-expanded-keys="defaultExpandedKeys" :highlight-current="true"
                    @node-click="handleFolderClick">
                    <template #default="{ node, data }">
                        <div class="tree-content">
                            <el-input :ref="(el) => collectRef(el, data.id, 'rename')"
                                v-if="handleFolder.id === data.id && isRename" v-model="handleFolder.name"
                                @keyup.enter="renameInputCompl" @click.stop v-click-outside="renameInputCompl">
                            </el-input>
                            <div v-else>
                                {{ data.name }}
                            </div>
                            <el-icon class="tree-func-icon" :size="12" :ref="(el) => collectRef(el, data.id, 'more')"
                                @click.stop="handleMoreClick($event, data)">
                                <More />
                            </el-icon>
                        </div>
                    </template>
                </el-tree>
            </el-sub-menu>
            <el-menu-item index="share"><el-icon>
                    <Share />
                </el-icon>
                <template #title>与我分享</template>
            </el-menu-item>
            <el-menu-item index="important"><el-icon>
                    <Star />
                </el-icon><template #title>重要</template></el-menu-item>
            <el-menu-item index="recycle"><el-icon>
                    <Delete />
                </el-icon><template #title>回收站</template></el-menu-item>
            <!-- <el-menu-item index="group"><el-icon>
                    <Cloudy />
                </el-icon><template #title>云协作</template></el-menu-item> -->
        </div>
        <!-- TODO 侧边栏折叠 -->
        <!-- <el-divider style="margin: 0;padding: 0;"></el-divider> -->
        <!-- <div class="side-bar-footer">
            <el-button type="text" @click="isCollapse = !isCollapse">
                <el-icon>
                    <ArrowLeftBold v-if="isCollapse" />
                    <arrow-right-bold v-if="!isCollapse" />
                </el-icon>
            </el-button>
        </div> -->
        <move-file v-model:display="moveDialogDisplay" :folders="myFolders" :file-name="currentFolder.name"
            :file-type="'folder'"></move-file>
        <el-popover popper-class="side-bar-folder-more" :visible="hideMorePopover.more" :show-arrow="false"
            placement="right-start" :virtual-ref="moreRefs[handleFolder?.id]" virtual-triggering>
            <ul class="folder-more-list" v-click-outside="handleHideMorePop">
                <el-popover popper-class="side-bar-folder-more" placement="right" :visible="hideMorePopover.create"
                    trigger="hover">
                    <template #reference>
                        <li style="display: flex;align-items: center;" @click.stop="hideMorePopover.create = true">
                            <span>新建</span>
                            <el-icon>
                                <ArrowRight />
                            </el-icon>
                        </li>
                    </template>
                    <ul class="folder-more-list" v-click-outside="() => hideMorePopover.create = false">
                        <li @click="createFile('Folder')">文件夹</li>
                    </ul>
                </el-popover>
                <li v-if="handleFolder.id !== 0" @click="renameFolder">重命名</li>
                <li v-if="handleFolder.id !== 0" @click="deleteFolder">删除</li>
                <li v-if="handleFolder.id !== 0" @click="moveFolder">移动到</li>
                <li v-if="handleFolder.id !== 0">加星</li>
            </ul>
        </el-popover>
    </el-menu>
</template>
<script setup>
import { ElMessage, ElMessageBox, ClickOutside as vClickOutside } from 'element-plus';
import userAvatar from './components/userAvatar.vue';
import { computed, nextTick, onMounted, reactive, ref, watch } from 'vue';
import { useRouter } from 'vue-router';
import { useFolderStore } from '../store/folder';
import { useUserStore } from '../store/user';
import { useCurrEditStore } from '../store/currentEdit';
import { updateNote } from '@/api/apis/note';
import moveFile from './components/moveFile.vue';

// ==================================data===================================
const router = useRouter();
// 是否折叠
const isCollapse = ref(false);
// 文件夹数组
const { myFolders } = defineProps({
    myFolders: {
        type: Array,
        default: () => []
    }
})
// 事件触发
const treeEmits = defineEmits(['create-file', 'rename-file', 'delete-file'])

// 当前文件夹状态管理
const folderStore = useFolderStore();
// 弹框模板引用
const currentFolder = computed(() => folderStore.currentFolder);
const hideMorePopover = reactive({
    more: false,
    create: false,
});
const moreRefs = reactive({});

// 重命名模板引用
const isRename = ref(false);
const renameInputRefs = reactive({})

// 树形文件夹模板引用
const treeFoldersRef = ref();
const isCreateFolder = ref(false);
const sideBarRef = ref();

// 更多操作的文件夹，不用currentFolder
const handleFolder = ref(folderStore.currentFolder);

// 记录树的展开状态
const defaultExpandedKeys = computed(() => folderStore.defaultExpandedKeys);

const userStore = useUserStore();

const currEditStore = useCurrEditStore();

// 控制moveFile对话框显示
const moveDialogDisplay = ref(false);

// ==================================钩子函数========================================
onMounted(() => {
    if (Object.keys(currentFolder.value).length != 0) {
        sideBarRef.value.open('myFolder');
        treeFoldersRef.value.setCurrentKey(currentFolder.value.id, true);
    }
})

// ==================================侦听器===========================================
watch(() => myFolders, async (newV, oldV) => {
    handleHideMorePop();
}, { deep: true })
// 监听store的currentFolder
watch(currentFolder, (newValue) => {
    // 修改elTree选中状态
    if (currentFolder.value && currentFolder.value.id != 0) {
        treeFoldersRef.value.setCurrentKey(currentFolder.value.id, true);
    } else {
        treeFoldersRef.value.setCurrentKey();
    }
})

// =================================== methods ======================================
// TODO 选择导航栏 文件夹
const handleMenuSelection = async (key, keyPath) => {
    switch (key) {
        case 'userCenter':
            router.push({ name: 'User' });
            break;
        case 'newFile':
            router.push({
                name: 'Note',
                params: { type: 'new' },
            });
            break;
        case 'important':
            router.push({
                name: 'Note',
                params: { type: 'star' }
            })
            break;
        case 'recycle':
            router.push({
                name: 'Note',
                params: { type: 'recycle' }
            })
            break;
        case 'share':
            router.push({
                name: 'Note',
                params: { type: 'share' }
            })
            break;
        default:
            router.push({
                name: 'Note',
                params: { type: 'folder' }
            });
            sideBarRef.value.updateActiveIndex('myFolder');
            break;
    }
    folderStore.setCurrentFolder({
        id: 0,
        name: key
    });
}

/**
 * @description: 点击el-tree的文件夹
 * @param {*} data
 * @param {*} node
 * @param {*} tree
 * @param {*} event
 * @return {*}
 */
const handleFolderClick = (data, node, tree, event) => {
    // 废弃：文件夹路径
    // 参数 data node tree event
    // let parentNode = node.parent;
    // const parentDatas = [];
    // parentDatas.unshift(node.data);
    // while (parentNode !== null) {
    //     if (!(Array.isArray(parentNode.data))) {
    //         parentDatas.unshift(parentNode.data); // 从孙节点开始，到每层父节点，再到根节点，用unshift
    //     }
    //     if (parentNode.parent !== null) {
    //         parentNode = parentNode.parent;
    //     } else {
    //         parentNode = null;
    //     }
    // }
    // folderStore.folderRoutes = parentDatas;
    folderStore.setCurrentFolder(data);
    router.push({
        name: 'Note',
        params: { type: 'folder' }
    });
    // console.log(tree);
    // console.log(node);

    // 记录展开的节点
    if (node.expanded) {
        folderStore.defaultExpandedKeys.push(data.id);
    } else {
        folderStore.defaultExpandedKeys = defaultExpandedKeys.value.filter((item) => item !== data.id);
    }
}

/**
 * @description: 点击more更多操作按钮
 * @param {*} event
 * @param {*} data
 * @return {*}
 */
const handleMoreClick = (event, data) => {
    handleFolder.value = data;
    hideMorePopover.more = true;
}

/**
 * @description: 新建文件
 * @param {*} type
 * @return {*}
 */
const createFile = async (type) => {
    switch (type) {
        case 'Folder':
            // 隐藏操作弹框
            handleHideMorePop();
            await nextTick();
            isCreateFolder.value = true;
            isRename.value = true;

            // 1、初始化文件夹数据
            const tempFolder = {
                id: new Date().getTime() + Math.floor(Math.random() * 100),
                name: '新建文件夹',
                parentId: handleFolder.value.id,
            };
            folderStore.setCurrentFolder(tempFolder);

            // 2、临时在树中新建一个文件夹
            treeEmits('create-file', { type: 'Folder', data: tempFolder });
            handleFolder.value = tempFolder;
    }
}

/**
 * @description: 开始重命名文件夹
 * @param {*} event
 * @return {*}
 */
const renameFolder = async (event) => {
    isRename.value = true;
    handleHideMorePop();
    isCreateFolder.value = false;
    await nextTick();

    focusNameInput(handleFolder.value.id);
}

/**
 * @description: 输入框失焦，判断是否重命名成功
 * @return {*}
 */
const renameInputCompl = () => {
    // 新建文件夹时进行命名
    if (isCreateFolder.value) {
        if (handleFolder.value.name) {
            treeEmits('rename-file', handleFolder.value);
        } else {
            treeEmits('delete-file', handleFolder.value.id);
        }
        isCreateFolder.value = false;
    } else if (handleFolder.value.name) { // 文件夹重命名
        treeEmits('rename-file', handleFolder.value);
    }
    isRename.value = false;
}

/**
 * @description: 删除文件夹
 * @return {*}
 */
const deleteFolder = () => {
    ElMessageBox.confirm(
        '确认要删除文件夹吗，这会连带着其中的文件一并放入回收站？',
        '是否删除文件夹',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        ElMessage.success('文件夹已放入回收站!');
        treeEmits('delete-file', handleFolder.value.id);
    }).catch(() => { })
}

// 建立ref
const collectRef = (el, id, type) => {
    if (el) {
        type === 'rename' ? renameInputRefs[id] = el : moreRefs[id] = el;
    } else {
        type === 'rename' ? delete renameInputRefs[id] : delete moreRefs[id];
    }
}

// 聚焦输入框
const focusNameInput = (id) => {
    const inputRef = renameInputRefs[id];
    if (!inputRef) console.error('没有改变ID的模板引用!');
    if (inputRef) {
        inputRef.focus();
        inputRef.select();
    }
}

// 隐藏所有弹框
const handleHideMorePop = () => {
    hideMorePopover.more = false;
    hideMorePopover.create = false;
}

// 新建文件
const handleCreate = async (type) => {
    switch (type) {
        case "folder":
            createFile();
            break;
        case "note":
            const result = await updateNote({
                userId: userStore.user.id,
                folderId: currentFolder.value.id
            });
            console.log(result);
            if (result.code === 200) {
                // 刷新文件夹列表
                folderStore.isRefreshFolder = true;
            }
    }
}

// 移动文件夹
const moveFolder = () => {
    handleHideMorePop();
    moveDialogDisplay.value = true;
    console.log(moveDialogDisplay.value)
}

</script>

<style lang="scss" scoped>
@use './css/sideBar.scss';
</style>

<style lang="scss">
@use './css/sideBarOfPop.scss';
</style>