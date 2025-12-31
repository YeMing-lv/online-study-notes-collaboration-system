<template>
    <div class="folder-container">
        <div class="search">
            <el-col :span="18">
                <el-input prefix-icon="Search" v-model="searchInput" placeholder="搜索笔记" clearable></el-input>
            </el-col>
            <el-popover :visible="displaySearchCondPop" placement="bottom">
                <template #reference>
                    <el-icon :size="22" @click="displaySearchCondPop = true">
                        <Operation />
                    </el-icon>
                </template>
                <div v-click-outside="() => displaySearchCondPop = false">
                    12314
                </div>
            </el-popover>
        </div>
        <div class="menu">
            menu
        </div>
        <div class="files">
            files
        </div>
    </div>
</template>
<script setup>
import { ref, reactive } from 'vue';
import { ClickOutside as VClickOutside } from 'element-plus';
/**
 * data
 * -----------------------------------------------------------------
 */
const searchInput = ref();
const searchCondRef = ref();
const searchCondPopRef = ref();
const displaySearchCondPop = ref(false);



</script>

<style lang="scss" scoped>
.folder-container {
    display: flex;
    flex-direction: column;
    width: 100%;
    max-width: 250px;
    border: 1px solid var(--el-border-color);

    .search {
        display: flex;
        align-items: center;
        justify-content: space-between;
        padding: 10px;

        .el-input {
            flex-grow: 2;
        }

        .el-icon:hover {
            cursor: pointer;
            background-color: #efefef;
        }
    }
}
</style>