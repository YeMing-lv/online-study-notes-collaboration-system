<template>
    <el-menu ref="sideBarRef" :collapse="isCollapse" @select="handleMenuSelection">
        <user-avatar v-if="!isCollapse"></user-avatar>
        <div class="side-bar-content">
            <el-menu-item index="newFile"><el-icon>
                    <Menu />
                </el-icon>
                <template #title>最新</template>
            </el-menu-item>
            <el-sub-menu index="folder">
                <template #title>
                    <el-icon>
                        <Folder />
                    </el-icon>
                    <span>我的文件夹</span>
                    <el-icon :size="12" class="more-icon" :ref="(el) => collectRef(el, 0, 'more')"
                        @click.stop="handleMoreClick({ id: 0, name: '' })">
                        <More />
                    </el-icon>
                </template>
                <el-tree ref="treeFoldersRef" :data="myFolders" node-key="id" @node-click="handleFolderClick">
                    <template #default="{ node, data }">
                        <div class="tree-content">
                            <el-input :ref="(el) => collectRef(el, data.id, 'rename')"
                                v-if="currentFolder.id === data.id && isRename" v-model="currentFolder.name"
                                @keyup.enter="renameInputCompl" @click.stop="" v-click-outside="renameInputCompl">
                            </el-input>
                            <div v-else>
                                {{ data.name }}
                            </div>
                            <div class="tree-func-icon">
                                <el-icon :size="12" :ref="(el) => collectRef(el, data.id, 'more')"
                                    @click.stop="handleMoreClick(data)">
                                    <More />
                                </el-icon>
                            </div>
                        </div>
                    </template>
                </el-tree>
            </el-sub-menu>
            <el-menu-item index="myShare"><el-icon>
                    <Share />
                </el-icon>
                <template #title>与我分享</template>
            </el-menu-item>
            <el-menu-item index="important"><el-icon>
                    <Star />
                </el-icon><template #title>重要</template></el-menu-item>
            <el-menu-item index="trash"><el-icon>
                    <Delete />
                </el-icon><template #title>回收站</template></el-menu-item>
            <el-menu-item index="group"><el-icon>
                    <Cloudy />
                </el-icon><template #title>云协作</template></el-menu-item>
        </div>
        <!-- <el-divider style="margin: 0;padding: 0;"></el-divider> -->
        <!-- <div class="side-bar-footer">
            <el-button type="text" @click="isCollapse = !isCollapse">
                <el-icon>
                    <ArrowLeftBold v-if="isCollapse" />
                    <arrow-right-bold v-if="!isCollapse" />
                </el-icon>
            </el-button>
        </div> -->
        <el-popover popper-class="side-bar-folder-more" :visible="hideMorePopover.more" :show-arrow="false"
            placement="right-start" :virtual-ref="moreRefs[currentFolder.id]" virtual-triggering>
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
                <li v-if="currentFolder.id !== 0" @click="renameFolder">重命名</li>
                <li v-if="currentFolder.id !== 0" @click="deleteFolder">删除</li>
            </ul>
        </el-popover>
    </el-menu>
</template>
<script setup>
import { ElMessage, ElMessageBox, ClickOutside as vClickOutside } from 'element-plus';
import userAvatar from './components/userAvatar.vue';
import { nextTick, onMounted, reactive, ref, watch } from 'vue';

/**
 * data
 * ----------------------------------------------------
 */
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

// 弹框模板引用
const currentFolder = ref({ id: 0, name: '' });
const hideMorePopover = reactive({
    more: false,
    create: false,
});
const morePopperRef = ref();
const moreRefs = reactive({})

// 重命名模板引用
const isRename = ref(false);
const renameInputRefs = reactive({})

// 树形文件夹模板引用
const treeFoldersRef = ref();
const isCreateFolder = ref(false);
const sideBarRef = ref();

/**
 * 钩子函数
 * --------------------------------------------------
 */
onMounted(() => {
    // console.log(myFolders);
    // console.log(renameRef.value);
    // console.log(renameInputRefs.value);
})

/**
 * 侦听器
 * -------------------------------------------------------
 */
watch(() => myFolders, async (newV, oldV) => {
    // console.log(myFolders);
    if (isCreateFolder.value && isRename.value) {
        sideBarRef.value.open('folder');
        await nextTick();

        treeFoldersRef.value.setCurrentKey(currentFolder.value.id, true);
        await nextTick();

        await nextTick();
        focusNameInput(currentFolder.value.id);
    }
}, { deep: true })

/**
 * methods
 * -------------------------------------------------
 */
// 选择导航栏
const handleMenuSelection = (key, keyPath) => {
    console.log(key);
    console.log(keyPath);
}

// 文件夹被点击
const handleFolderClick = () => {

}

// 点击更多操作
const handleMoreClick = (data) => {
    currentFolder.value = data;
    hideMorePopover.more = true;
}

// 新建
const createFile = (type) => {
    switch (type) {
        case 'Folder':
            handleHideMorePop();
            isCreateFolder.value = true;
            isRename.value = true;

            // 1、初始化文件夹数据
            const tempFolder = {
                id: new Date().getTime() + Math.floor(Math.random() * 100),
                name: '新建文件夹',
                parentId: currentFolder.value.id,
            };
            currentFolder.value = tempFolder;

            // 2、临时在树中新建一个文件夹
            treeEmits('create-file', { type: 'Folder', data: tempFolder });
            return;
    }
}

// 重命名文件夹
const renameFolder = async (event) => {
    isRename.value = true;
    handleHideMorePop();
    isCreateFolder.value = false;
    await nextTick();
    await nextTick();

    focusNameInput(currentFolder.value.id);
}

// 输入框失焦
const renameInputCompl = () => {
    debugger
    // 新建文件夹时进行命名
    if (isCreateFolder.value) {
        if (currentFolder.value.name) {
            treeEmits('rename-file', currentFolder.value);
        } else {
            treeEmits('delete-file', currentFolder.value.id);
        }
        isCreateFolder.value = false;
    } else if (currentFolder.value.name) { // 文件夹重命名
        treeEmits('rename-file', currentFolder.value);
    }
    isRename.value = false;
}

// 删除文件夹
const deleteFolder = () => {
    ElMessageBox.confirm(
        '确认要删除文件夹吗，会连带着其中的文件一并放入回收站？',
        '是否删除文件夹',
        {
            confirmButtonText: '确认',
            cancelButtonText: '取消',
            type: 'warning',
        }
    ).then(() => {
        ElMessage.success('文件夹已放入回收站!');
        treeEmits('delete-file', currentFolder.value.id);
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

</script>

<style lang="scss" scoped>
.el-menu {
    display: flex;
    flex-direction: column;
    line-height: 17px;
    height: auto;
    min-height: 100vh;
    width: auto;
    min-width: 120px;

    .side-bar-content {
        overflow-y: hidden;
        flex-grow: 2;

        .el-menu-item {
            display: flex;
            align-items: center;
            height: 20px;
            padding: 15px 10px;
            font-size: 12px;
        }

        .el-sub-menu {
            font-size: 12px;
            padding: 0px;

            :deep(.el-sub-menu__title) {
                font-size: 12px;
                padding: 15px 10px !important;
                height: 20px !important;

                .more-icon :hover {
                    background-color: #d6d6d6;
                }

            }

            .el-tree {

                :deep(.el-tree-node__content) {
                    height: 20px;
                    padding: 0px;
                }

                .tree-content {
                    display: flex;
                    justify-content: space-between;
                    font-size: smaller;
                    width: 100% !important;

                    .tree-func-icon {
                        opacity: 0;
                    }

                    .el-input {
                        width: 100px;
                        height: 20px;
                        font-size: 12px;
                    }
                }

                .tree-content:hover {

                    .tree-func-icon {
                        width: auto;
                        display: flex;
                        align-items: center;
                        opacity: 1;
                        transition: opacity 0.5s ease;
                    }

                    .tree-func-icon:hover {
                        background-color: #d6d6d6;
                    }
                }
            }

        }
    }

    .side-bar-footer {
        padding: 10px;

        .el-button {
            float: right;
        }
    }
}
</style>

<style lang="scss">
.side-bar-folder-more {
    padding: 0 !important;

    .folder-more-list {
        padding: 5px 0px;
        width: 100%;
        list-style-type: none;

        li {
            padding: 2px 10px;
            font-size: smaller;
            cursor: pointer;
            display: flex;
            justify-content: space-between;
        }

        :hover {
            background-color: #efefef;
        }
    }

}
</style>