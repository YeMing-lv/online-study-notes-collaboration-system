<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2026-05-03 16:57:04
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-05-29 09:39:53
 * @FilePath: \webapp\src\layout\components\shareNote.vue
 * @Description: 
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-dialog title="分享笔记" v-model="display" width="500" :append-to-body="true">
        <el-form v-loading="loading" :model="form" ref="formRef" :rules="rules" label-width="120" :inline="false">
            <el-form-item label="被分享人账号：" prop="shareToUserName">
                <el-input v-model="form.shareToUserName" placeholder="请输入被分享人账号" clearable></el-input>
            </el-form-item>
            <el-form-item label="有效时间：" prop="shareExpireTime">
                <el-date-picker v-model="form.shareExpireTime" type="date" placeholder="选择过期时间" clearable
                    :disabled-date="disabledDate" />
            </el-form-item>
            <el-form-item label="权限：" prop="sharePermission">
                <el-radio-group v-model="form.sharePermission" clearable>
                    <el-radio value="1">只读</el-radio>
                    <el-radio value="2">可编辑</el-radio>
                </el-radio-group>
            </el-form-item>
            <el-form-item>
                <el-button type="primary" @click="onSubmit">分享</el-button>
                <el-button @click="display = false">取消</el-button>
            </el-form-item>
        </el-form>
    </el-dialog>
</template>

<script setup>
import { computed, onMounted, onUnmounted, ref } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';
import { shareNote } from '../../api/apis/note';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['noteId', 'userId'])

const form = ref({
    targetType: 2,
    targetId: 0,
    shareFromUserId: props.userId,
    shareToUserName: '',
    sharePermission: 1,
    shareExpireTime: '',
});
const disabledDate = (time) => {
    const tomorrow = new Date()
    tomorrow.setDate(tomorrow.getDate() + 1) // 明天
    tomorrow.setHours(0, 0, 0, 0)
    return time.getTime() < tomorrow.getTime()
}

const formRef = ref({});

const rules = {};

const loading = ref(false);

// --------------------------------------------------//
onUnmounted(() => {
    form.value = {
        targetType: 2,
        targetId: props.noteId,
        shareFromUserId: props.userId,
        shareToUserName: '',
        sharePermission: 1,
        shareExpireTime: '',
    }
})

// --------------------methods---------------------//
const onSubmit = async () => {
    loading.value = true;
    form.value.targetId = props.noteId;
    console.log(form.value);
    const result = await shareNote(form.value);
    console.log(result);
    setTimeout(() => {
        loading.value = false;
        display.value = false;
    }, 1000);
}

</script>

<style lang="scss" scoped></style>