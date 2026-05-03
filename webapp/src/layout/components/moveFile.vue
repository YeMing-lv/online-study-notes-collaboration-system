<template>
    <el-dialog title="移动文件" v-model="display" width="300" :append-to-body="true">
        <div style="display: flex;align-items: center;">
            <img style="width: 20px;"
                :src="props.fileType === 'folder' ? getImgUrl('folder.png') : getImgUrl('markdown.png')">
            {{ props.fileName }}
        </div>
        <div style="margin-top: 10px;color: #7f7f7f;">移动到：</div>
        <div class="folders-wrapper">
            <el-tree ref="treeFoldersRef" :data="props.folders" node-key="id" :indent="8" :highlight-current="true">
                <template #default="{ node, data }">
                    <div class="tree-content">
                        <span> {{ data.name }} </span>
                    </div>
                </template>
            </el-tree>
        </div>

        <span slot="footer">
            <el-button @click="display = false">取消</el-button>
            <el-button type="primary" @click="">移动</el-button>
        </span>
    </el-dialog>
</template>

<script setup>
import { ref } from 'vue';
import { getImgUrl } from '@/utils/assetsImport.js';

const display = defineModel('display', {
    default: false
})
const props = defineProps(['folders', 'fileType', 'fileName'])

</script>

<style lang="scss" scoped>
.folders-wrapper {
    margin: 10px 0px;
    border: 1px solid #efefef;
    height: 250px;
    overflow-y: scroll;
}
</style>