<template>
    <el-dialog title="历史版本" v-model="display" width="80%" :append-to-body="true">
        <el-table style="margin: 10px 0px;" :data="versionList" height="300" border stripe>
            <el-table-column label="保存时间" width="160">
                <template #default="scope">
                    {{ formatTime(scope.row.createTime) }}
                </template>
            </el-table-column>
            <el-table-column prop="title" label="标题" width="200" show-overflow-tooltip></el-table-column>
            <el-table-column label="内容" show-overflow-tooltip>
                <template #default="scope">
                    {{ scope.row.content.replace(/<[^>]+>/g, '') }}
                </template>
            </el-table-column>
            <el-table-column prop="editRemark" label="备注" width="180" show-overflow-tooltip></el-table-column>
            <el-table-column label="操作">
                <template #default="scope">
                    <el-button type="primary" size="default" @click="">重新保存</el-button>
                    <el-button size="default" @click="">覆盖</el-button>
                    <el-button size="default" @click="toEdit(scope.row)">编辑</el-button>
                </template>
            </el-table-column>
        </el-table>

        <el-dialog title="编辑笔记历史版本" v-model="editDisplay" width="80%" :append-to-body="true">
            <el-form :model="form" ref="formRef" label-width="120px" :inline="false" size="normal">
                <el-row :gutter="20">
                    <el-col :span="12" :offset="0">
                        <el-form-item label="创建时间：">
                            <el-input :value="formatTime(form.createTime)" disabled></el-input>
                        </el-form-item>
                    </el-col>
                    <el-col :span="12" :offset="0">
                        <el-form-item label="备注：">
                            <el-input v-model="form.editRemark" clearable></el-input>
                        </el-form-item>
                    </el-col>
                </el-row>
                <el-form-item label="标题：">
                    <el-input v-model="form.title" disabled></el-input>
                </el-form-item>
                <el-form-item label="内容：">
                    <el-input :value="form.content.replace(/<[^>]+>/g, '')" disabled type="textarea"></el-input>
                </el-form-item>

                <el-form-item>
                    <el-button type="primary" @click="onSubmit">保存</el-button>
                    <el-button>取消</el-button>
                </el-form-item>
            </el-form>
        </el-dialog>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';
import { formatTime } from '../../utils/timeHandle';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['versionList'])

const editDisplay = ref(false)

const form = ref({});

const toEdit = (version) => {
    display.value = false;
    form.value = version;
    editDisplay.value = true;
}

</script>

<style lang="scss" scoped></style>