<template>
    <div class="file-container">
        <side-bar :my-folders="folders" @rename-folder="handleRenameFolder"></side-bar>
        <side-folder></side-folder>
    </div>
</template>
<script setup>
import sideBar from '@/layout/sideBar.vue';
import sideFolder from '@/layout/sideFolder.vue';
import { listFolderByUserId } from '../../api/apis/folder';
import { onMounted, reactive } from 'vue';
import { useUserStore } from '../../store/user';
import { ElMessage } from 'element-plus';
import { updateFolder } from '../../api/apis/folder';

/**
 * data
 * -------------------------------------------------------
 */
const folders = reactive([]);
const userStore = useUserStore();

/**
 * 钩子函数
 * ------------------------------------------------------
 */
onMounted(() => {
    getFolders();
})

/**
 * methods
 * ------------------------------------------------------
 */
// 获取用户个人文件夹
const getFolders = async () => {
    try {
        const result = await listFolderByUserId(userStore.user);
        if (result.code === 200) {
            folders.length = 0;
            folders.push(...result.data);
        } else {
            ElMessage.error('获取用户文件夹失败！');
        }
    } catch (err) {
        console.error('获取用户文件夹出错:' + err);
        ElMessage.error('获取用户文件夹失败！');
    }
}

// 重命名文件夹
const handleRenameFolder = async (folder) => {
    console.log(folder);
    try {
        const result = await updateFolder(folder);
        if (result.code === 200) {
            getFolders();
        }
    } catch (error) {
        console.error(error);
    }
}


</script>

<style lang="scss" scoped>
.file-container {
    display: flex;

}
</style>