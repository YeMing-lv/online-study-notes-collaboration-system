<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-31 14:47:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-02-02 16:56:30
 * @FilePath: \online-study-notes-collaboration-system\src\layout\editor.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-container class="editor-container">
        <el-header class="title-header">
            <span class="th-title">title</span>
            <el-row class="th-right-container" :gutter="20">
                <el-col :span="8">
                    <el-button type="primary" size="default" @click="">保存</el-button>
                </el-col>
                <el-col :span="12">
                    <el-row :gutter="10">
                        <el-col :span="12">
                            <el-icon>
                                <Star />
                            </el-icon>
                        </el-col>
                        <el-col :span="12">
                            <el-icon>
                                <Share />
                            </el-icon>
                        </el-col>
                    </el-row>
                </el-col>
            </el-row>
        </el-header>
        <el-divider style="margin: 0;" />
        <Toolbar class="toolbar-header" :editor="editorRef" :default-config="toolbarConfig" />
        <el-divider style="margin: 0;" />
        <Editor class="content-main" :default-config="editorConfig" v-model="valueHtml" @onCreated="handleCreated"
            @onChange="handleChange" @onDestroyed="handleDestroyed()" />
    </el-container>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { onBeforeUnmount, ref, reactive, shallowRef, onMounted } from 'vue';

import { extractImagePath } from '../utils/imageUtils';
import { useUserStore } from '../store/user';

const userStore = useUserStore();

const editorRef = shallowRef(); // 编辑器实例，必须用 shallowRef，重要！
const valueHtml = ref('<p>hello</p>'); // 内容 HTML
const imageList1 = reactive([]); // 图片列表 所有插入的图片 包括编辑器里删除的图片

//============================生命周期钩子=======================
// 退出页面 确认是否保存草稿
// 组件销毁前，要及时销毁编辑器，重要！
onBeforeUnmount(() => {
    initEditContent();
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
});

//=============================编辑器配置========================
const toolbarConfig = { 
    // 要清除掉的工具
    excludeKeys: [
        'group-video', // 视频工具组
        'group-undo', // 撤销工具组
        'undo', // 撤销
        'redo', // 重做
        'fullScreen', // 全屏
        'insertImage'
    ]
};
const editorConfig = {
    placeholder: '请输入内容...',
    MENU_CONF: {
        uploadImage: { // 图片上传
            fieldName: 'file',
            // server: `http://localhost:8082/image/upload?type=editor`,
            headers: {
                Authorization: userStore.token
            },
            maxFileSize: 2 * 1024 * 1024,
        },
        // 用来存储所有上传过的图片的
        insertImage: {
            onInsertedImage(imageNode) {
                if (imageNode == null) return;
                const { src, alt, url, href } = imageNode;
                // console.log(imageList1);
                // 记录所有上传到后端的图片 用于后续比对 删除未存于 valueHtml 的图片
                imageList1.push({
                    src,
                    alt,
                    url,
                    href
                });
            }
        }
    }
};

// ================================编辑器钩子===================================
const handleCreated = (editor) => {
    editorRef.value = editor; // 记录 editor 实例，重要！！！！
    // console.log(editorRef.value);
    // console.log(editorRef.value.getAllMenuKeys());
    // console.log(editorRef.value.getMenuConfig('uploadImage'));
    // console.log(editorRef.value.getMenuConfig('insertImage'));
    // console.log(editorRef.value.getMenuConfig('deleteImage'));
    // console.log(editorRef.value.getMenuConfig('editImage'));
};

const handleChange = (editor) => {
    editorRef.value = editor;
};

// 编辑器销毁之前 进行保存
const handleDestroyed = (editor) => {
    if (ifNewInput()) { // 编辑器已有输入 并且还未保存过
        ElMessageBox.confirm('是否要保存草稿?', '有未保存的修改',
            {
                confirmButtonText: '确认',
                cancelButtonText: '拒绝',
                type: 'warning'
            }
        ).then(() => {
            saveWrite();
        }).catch(() => {
            // 删除没有保存的图片资源
            deleteNotusedImage();
        });
    }
    editorRef.value = editor; // 记录 editor 实例，重要！！！！
};

//==============================编辑器管理===========================
// 保存
const saveContent = async () => {
    // if (!ifNewInput()) { // 是否有新输入
    //     ElMessage.info("请先输入新内容！")
    //     return;
    // }
}

// 判断是否有新内容
const ifNewInput = (title, content) => {
    return true;
}

// 初始化编辑器内容
function initEditContent() {
    editDraft.value = null;
    valueHtml.value = '<p>hello</p>';
    imageList1.values = [];
}

// ===========================图片管理============================
// 删除未使用图片
async function deleteNotusedImage() {
    // const editor = editorRef.value;
    // if (editor == null) return;

    // // 对比图片列表
    // const imageList2 = editor.getElemsByType('image');
    // const result = compareImageList(imageList1, imageList2);
    // console.log(result);
    // if (result != null || result != []) { // 删除未使用图片
    //     for (let i = 0; i < result.length; i++) {
    //         const element = result[i];
    //         const deletePath = extractImagePath(element.src); // 提取相对路径
    //         await deleteImage(deletePath);
    //     }
    // }
}
// 图片列表对比处理
function compareImageList(list1, list2) {
    // const srcInList1 = new Set(list1.map(item => item.src));
    // const srcInList2 = new Set(list2.map(item => item.src));

    // const list = [
    //     ...list2.filter(item => !srcInList1.has(item.src)),
    //     ...list1.filter(item => !srcInList2.has(item.src))
    // ];
    // return list;
}
</script>

<style lang="scss" scoped>
@import "css/editor.scss";
</style>