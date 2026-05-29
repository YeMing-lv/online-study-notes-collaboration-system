<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-03-11 14:36:43
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-29 10:52:35
 * @FilePath: \webapp\src\layout\editor.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-container v-loading="refresh" class="editor-container">
        <el-empty v-if="Object.keys(currentEdit).length == 0" style="width: 100%;" description="快创建笔记吧"
            image="../src/assets/note.png" :image-size="120"></el-empty>
        <div class="ed-container" v-else-if="!refresh">
            <el-header class="title-header">
                <input id="th-input" class="th-input" type="text" v-model="title" autocomplete="off" placeholder="请输入标题"
                    :readonly="!handleIfEdit()">
                <!-- <span class="th-title">{{ title }}</span> -->
                <el-row class="th-right-container" :gutter="20">
                    <el-col :span="8">
                        <el-button v-if="handleIfEdit()" type="primary" size="default"
                            @click="saveNote('active', currentEdit)">保存</el-button>
                    </el-col>
                    <el-col :span="12">
                        <!-- <el-row :gutter="10">
                            <el-col :span="8" @click="handleStar()">
                                <el-icon>
                                    <Star />
                                </el-icon>
                            </el-col>
                            <el-col :span="8">
                                <el-icon>
                                    <Share />
                                </el-icon>
                            </el-col>
                            <el-col :span="8">
                                <el-icon>
                                    <MoreFilled />
                                </el-icon>
                            </el-col>
                        </el-row> -->
                    </el-col>
                </el-row>
            </el-header>
            <el-divider style="margin: 0;" />
            <Toolbar v-if="handleIfEdit()" class="toolbar-header" :editor="editorRef" :default-config="toolbarConfig" />
            <el-divider style="margin: 0;" />
            <Editor class="content-main" :default-config="editorConfig" v-model="valueHtml" @onCreated="handleCreated"
                @onChange="handleChange" @onDestroyed="handleDestroyed()" />
        </div>
    </el-container>
</template>

<script setup>
import '@wangeditor/editor/dist/css/style.css';
import { Editor, Toolbar } from '@wangeditor/editor-for-vue';
import { onBeforeUnmount, ref, reactive, shallowRef, onMounted, watch, computed, onUnmounted } from 'vue';
import { extractImagePath } from '@/utils/imageUtils';
import { useUserStore } from '@/store/user';
import { deleteImage } from '@/api/apis/image';
import { useCurrEditStore } from '@/store/currentEdit';
import { ElMessage, ElScrollbar } from 'element-plus';
import { updateNote, saveNoteVersion, starNote } from '@/api/apis/note';
import { useFolderStore } from '../store/folder';
import { useRoute } from 'vue-router';

const userStore = useUserStore();
const currentEditStore = useCurrEditStore();
const currentEdit = computed(() => currentEditStore.currentEdit);

const editorRef = shallowRef(); // 编辑器实例，必须用 shallowRef，重要!
const title = ref('');
const valueHtml = ref(''); // 内容 HTML
const imageList1 = reactive([]); // 图片列表 所有插入的图片 包括编辑器里删除的图片

const refresh = ref(false); // 刷新编辑器

const folderStore = useFolderStore();

const route = useRoute();

//============================生命周期钩子=======================
onMounted(() => {
    if (currentEdit.value != null && currentEdit.value.title != null) {
        title.value = JSON.parse(JSON.stringify(currentEdit.value.title));
        valueHtml.value = JSON.parse(JSON.stringify(currentEdit.value.content));
    }
})
// 退出页面 确认是否保存草稿
// 组件销毁前，要及时销毁编辑器，重要！
onUnmounted(() => {
    initEditContent();
    const editor = editorRef.value
    if (editor == null) return
    editor.destroy()
});

//===============================侦听器=============================\
// 切换笔记刷新编辑器
watch(currentEdit, async (newValue, oldV) => {
    // 1. 切换前：先等 旧笔记 保存完成！【关键修复】
    if (oldV && oldV.id) {
        if (oldV?.share?.sharePermission != 1 && oldV?.isRecycle != 1) {
            await saveNote('auto', oldV);  // 必须加 await
        }
    }

    // 2. 保存完了，再切换、再赋值
    if (!oldV || newValue?.id !== oldV.id) {
        refresh.value = true;

        if (newValue && newValue.id) {
            title.value = newValue.title || '';
            valueHtml.value = newValue.content || '';
        } else {
            title.value = '';
            valueHtml.value = '';
        }

        setTimeout(() => {
            refresh.value = false;
        }, 300);
    }
}, { immediate: true, deep: true });

// 监听Ctrl+S快捷键，保存笔记
document.addEventListener("keydown", (event) => {
    if (event.ctrlKey && event.code == 'KeyS' && handleIfEdit()) {
        saveNote('active');
        // 禁止触发浏览器保存页面事件
        event.returnValue = false;
    }
})

//=============================编辑器配置========================
const toolbarConfig = shallowRef({
    // 要清除掉的工具
    excludeKeys: [
        'group-video', // 视频工具组
        'group-undo', // 撤销工具组
        'undo', // 撤销
        'redo', // 重做
        'fullScreen', // 全屏
        'insertImage'
    ]
});
const editorConfig = shallowRef({
    placeholder: '请输入内容...',
    MENU_CONF: {
        uploadImage: { // 图片上传
            fieldName: 'file',
            server: `http://localhost:8082/image/upload?type=editor`,
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
});

// ================================编辑器钩子===================================
const handleCreated = (editor) => {
    editorRef.value = editor; // 记录 editor 实例，重要！！！！
};

const handleChange = (editor) => {
    editorRef.value = editor;
    // 动态更新只读状态
    if (!handleIfEdit()) {
        editorRef.value.disable();
    } else {
        editorRef.value.enable();
    }
};

// 编辑器销毁之前 进行保存
const handleDestroyed = (editor) => {
    saveNote('auto', currentEdit);
    deleteNotusedImage();
    editorRef.value = editor; // 记录 editor 实例，重要！！！！
};

//==============================编辑器管理===========================
// TODO 主动保存、自动保存、快捷键保存、切换笔记前保存
// 保存
const saveNote = async (type, note) => {
    // 必须用传入的 note，不能用当前编辑
    if (!note || !note.id) return;

    // 校验
    if (!ifNewInput(type, note)) return;

    try {
        // 保存版本（用旧note的id）
        await saveNoteVersion({
            noteId: note.id,
            title: title.value,       // 当前编辑器里的内容
            content: valueHtml.value // 当前编辑器里的内容
        });

        // 构建更新对象
        const newNote = {
            ...note,
            title: title.value,
            content: valueHtml.value,
            updateTime: new Date()
        };

        const res = await updateNote(newNote);
        if (res.code === 200) {
            if (type === 'active') {
                ElMessage.success('保存成功！');
                folderStore.isRefreshFolder = true;
            }

            // 只有手动保存时才更新store，自动保存不更新
            if (type === 'active') {
                currentEditStore.setCurrentEdit(newNote);
            }
        }
    } catch (err) {
        console.error('保存失败', err);
    }
};

// 判断是否有新内容
const ifNewInput = (type, note) => {
    // 1. 没修改 → 不保存
    if (note) {
        if (
            note.title === title.value &&
            note.content === valueHtml.value
        ) {
            if (type === 'active') {
                ElMessage.info("已保存过");
            }
            return false;
        }
    } else {
        if (
            currentEdit.value.title === title.value &&
            currentEdit.value.content === valueHtml.value
        ) {
            if (type === 'active') {
                ElMessage.info("已保存过");
            }
            return false;
        }
    }

    // 2. 标题不能为空
    if (!title.value?.trim()) {
        ElMessage.warning("标题不能为空！");
        return false;
    }

    // 3. 内容必须是【富文本】（必须包含 <...> 标签）
    const hasHtmlTag = /<[a-z]+>/i.test(valueHtml.value);
    if (!hasHtmlTag) {
        console.error("要保存富文本：" + valueHtml.value);
        return false;
    }

    // 4. 内容不能是空富文本（只有标签、没有文字）
    const text = valueHtml.value
        .replace(/<[^>]+>/g, "")  // 去掉所有标签
        .trim();

    if (!text) {
        ElMessage.warning("内容不能为空！");
        return false;
    }
    return true;
};

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

// 判断是否允许编辑
const handleIfEdit = () => {
    if (route.params.type == 'recycle' && currentEdit.value.isRecycle == 1) {
        return false;
    } else if (route.params.type == 'share' && currentEdit.value?.share?.sharePermission == 1) {
        return false;
    }
    return true;
}

</script>

<style lang="scss" scoped>
@use "css/editor.scss";
</style>