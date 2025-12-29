<template>
    <el-menu :collapse="isCollapse" @select="handleMenuSelection">
        <user-avatar v-if="!isCollapse"></user-avatar>
        <div class="side-bar-content">
            <el-menu-item index="newFile"><el-icon>
                    <Menu />
                </el-icon>
                <template #title>最新</template>
            </el-menu-item>
            <el-sub-menu index="file">
                <template #title>
                    <el-icon>
                        <Folder />
                    </el-icon>
                    <span>我的文件夹</span>
                    <el-icon :size="12" class="more-icon" ref="moreRef">
                        <More />
                    </el-icon>
                    <el-popover popper-class="side-bar-folder-more" ref="morePopperRef" :virtual-ref="moreRef"
                        trigger="click" :show-arrow="false" placement="right-start" virtual-triggering>
                        <ul class="folder-more-list">
                            <el-popover popper-class="side-bar-folder-more" placement="right" trigger="hover">
                                <template #reference>
                                    <li>
                                        <span>新建</span>
                                        <el-icon>
                                            <ArrowRight />
                                        </el-icon>
                                    </li>
                                </template>
                                <ul class="folder-more-list">
                                    <li @click="createFile('Folder', true)">文件夹</li>
                                </ul>
                            </el-popover>
                        </ul>
                    </el-popover>
                </template>
                <el-tree :data="myFolders" node-key="id" @node-click="handleFolderClick">
                    <template #default="{ node, data }">
                        <div class="tree-content">
                            <div v-if="currentRenameFolderID !== data.id">
                                {{ data.name }}
                            </div>
                            <el-input :ref="(el) => collectRenameInputRef(el, data.id)" v-else v-model="inputName"
                                @blur="renameComplete(data)" @keyup.enter.stop="renameComplete(data)" @click.stop="">
                            </el-input>
                            <div class="tree-func-icon">
                                <el-popover popper-class="side-bar-folder-more" trigger="click" :show-arrow="false"
                                    placement="right-start">
                                    <ul class="folder-more-list">
                                        <el-popover popper-class="side-bar-folder-more" placement="right"
                                            trigger="hover">
                                            <template #reference>
                                                <li>
                                                    <span>新建</span>
                                                    <el-icon>
                                                        <ArrowRight />
                                                    </el-icon>
                                                </li>
                                            </template>
                                            <ul class="folder-more-list">
                                                <li @click="createFile('Folder', false)">文件夹</li>
                                            </ul>
                                        </el-popover>
                                        <li @click="renameFolder($event, data)">重命名</li>
                                        <li @click="deleteFolder(data)">删除</li>
                                    </ul>
                                    <template #reference>
                                        <el-icon :size="12" @click.stop="">
                                            <More />
                                        </el-icon>
                                    </template>
                                </el-popover>
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

    </el-menu>
</template>
<script setup>
import { ArrowRightBold, Delete, Folder, Menu, More, Star } from '@element-plus/icons-vue';
import { ClickOutside as VClickOutside } from 'element-plus';
import userAvatar from './components/userAvatar.vue';
import { nextTick, onMounted, reactive, ref } from 'vue';

/**
 * data
 * ----------------------------------------------------
 */
const isCollapse = ref(false);
const { myFolders } = defineProps({
    myFolders: {
        type: Array,
        default: () => []
    }
})
const treeEmits = defineEmits(['create-file', 'rename-folder', 'delete-folder'])

const morePopperRef = ref();
const moreRef = ref();

// 重命名：新名字、当前文件夹ID、输入框的动态模板引用
const inputName = ref();
const currentRenameFolderID = ref();
const renameInputRef = reactive({});


/**
 * 钩子函数
 * --------------------------------------------------
 */
onMounted(() => {
    // console.log(myFolders);
    // console.log(renameRef.value);
})

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

// 新建
const createFile = (type, isMineFolder = false) => {
    let parent = '';
    if (isMineFolder === true) {
        parent = 'MainFolder';
    }
    switch (type) {
        case 'Folder':
    }
    emit('create-file', { type: type, isMineFolder: isMineFolder });
}

// 重命名文件夹
const renameFolder = async (event, folder) => {
    event.stopPropagation();
    inputName.value = folder.name;
    currentRenameFolderID.value = folder.id;
    const inputRef = renameInputRef[folder.id];
    await nextTick();

    if (inputRef) {
        /**
         * 聚焦输入框 失败
         * 第一次点击 未聚焦，后续就可以
         */
        setTimeout(() => {
            inputRef.focus();
            inputRef.$el.querySelector('input').focus();
        }, 100)
    }
}

// 建立输入框Ref
const collectRenameInputRef = (el, id) => {
    if (el) {
        renameInputRef[id] = el;
    } else {
        delete renameInputRef[id];
    }
}

// 输入框失焦
const renameComplete = (folder) => {
    const data = JSON.parse(JSON.stringify(folder));
    delete data.children;
    // console.log(data);
    data.name = inputName.value;
    currentRenameFolderID.value = null;
    inputName.value = '';
    treeEmits('rename-folder', data);
}

// 删除文件夹
const deleteFolder = (data) => {
    treeEmits('delete-folder', data.id);
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
                    background-color: #efefef;
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
                        background-color: #efefef;
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