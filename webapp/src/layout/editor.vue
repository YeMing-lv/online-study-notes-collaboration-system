<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-31 14:47:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-03-13 16:27:38
 * @FilePath: \webapp\src\layout\editor.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-container class="editor-container">
        <el-empty v-if="Object.keys(currentEdit).length == 0" style="width: 100%;" description="快创建笔记吧"
            image="../src/assets/note.png" image-size="120"></el-empty>
        <div class="ed-container" v-else>
            <el-header class="title-header">
                <input id="th-input" class="th-input" type="text" v-model="title" autocomplete="off">
                <!-- <span class="th-title">{{ title }}</span> -->
                <el-row class="th-right-container" :gutter="20">
                    <el-col :span="8">
                        <el-button type="primary" size="default" @click="saveNote('active')">保存</el-button>
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
        </div>
    </el-container>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { onBeforeUnmount, ref, reactive, shallowRef, onMounted, watch, computed } from 'vue';

import { extractImagePath } from '../utils/imageUtils';
import { useUserStore } from '../store/user';
import { deleteImage } from '../api/apis/image';
import { useCurrEditStore } from '../store/currentEdit';
import { ElMessage, ElScrollbar } from 'element-plus';
import { updateNote, saveNoteVersion } from '../api/apis/note';

const userStore = useUserStore();
const currentEditStore = useCurrEditStore();
const currentEdit = computed(() => currentEditStore.currentEdit);

const editorRef = shallowRef(); // 编辑器实例，必须用 shallowRef，重要!
const title = ref(JSON.parse(JSON.stringify(currentEdit.value.title)) || '');
const valueHtml = ref(JSON.parse(JSON.stringify(currentEdit.value.content)) || ''); // 内容 HTML
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

//===============================侦听器=============================
// 读取选中笔记内容
watch(currentEdit, (newValue, oldV) => {
    console.log(newValue);
    console.log(oldV);
    if (Object.keys(oldV).length != 0) {
        saveNote
    }
    
    if (Object.keys(currentEdit.value).length != 0) {
        valueHtml.value = currentEdit.value.content;
        title.value = currentEdit.value.title;
    }
})

// 监听Ctrl+S快捷键，保存笔记
document.addEventListener("keydown", (event) => {    
    if (event.ctrlKey && event.code == 'KeyS') {
        saveNote('active');
        event.returnValue = false;
    }
})

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
};

const handleChange = (editor) => {
    editorRef.value = editor;
};

// 编辑器销毁之前 进行保存
const handleDestroyed = (editor) => {
    saveNote();
    deleteNotusedImage();
    editorRef.value = editor; // 记录 editor 实例，重要！！！！
};

//==============================编辑器管理===========================
// TODO 主动保存、自动保存、快捷键保存、切换笔记前保存
// 保存
const saveNote = async (type, note) => {
    if (!ifNewInput()) return;

    // 先保存note_version笔记版本草稿
    const result = await saveNoteVersion({
        noteId: currentEdit.value.id,
        title: currentEdit.value.title,
        content: currentEdit.value.content
    });
    if (result.code == 200) {
        // 保存新笔记
        const newNote = {
            ...currentEdit.value,
            content: valueHtml.value,
            title: title.value,
            updateTime: new Date()
        };
        console.log(newNote);
        // const result = await updateNote(newNote);
        // if (result.code == 200) {
        //     if (type == 'active') {
        //         ElMessage.success("保存成功！");
        //     }
        //     currentEditStore.setCurrentEdit(newNote);
        // }
    }
}

// 判断是否有新内容
const ifNewInput = () => {
    if (currentEdit.value.title == title.value && currentEdit.value.content == valueHtml.value) {
        return false;
    } else {
        if (title.value === '' || valueHtml.value === '') {
            ElMessage.warning("标题或内容不能为空！");
            return false;
        }
        return true;
    }
}

// 初始化编辑器内容
function initEditContent() {
    title.value = '';
    valueHtml.value = '';
    imageList1.values = [];
}

// ===========================图片管理============================
// 删除未使用图片
async function deleteNotusedImage() {
    const editor = editorRef.value;
    if (editor == null) return;

    // 对比图片列表
    const imageList2 = editor.getElemsByType('image');
    const result = compareImageList(imageList1, imageList2);
    if (result != null || result != []) { // 删除未使用图片
        for (let i = 0; i < result.length; i++) {
            const element = result[i];
            const deletePath = extractImagePath(element.src); // 提取相对路径
            await deleteImage(deletePath);
        }
    }
}
// 图片列表对比处理
function compareImageList(list1, list2) {
    const srcInList1 = new Set(list1.map(item => item.src));
    const srcInList2 = new Set(list2.map(item => item.src));

    const list = [
        ...list2.filter(item => !srcInList1.has(item.src)),
        ...list1.filter(item => !srcInList2.has(item.src))
    ];

    return list;
}
</script>

<style lang="scss" scoped>
@use "css/editor.scss";
</style>