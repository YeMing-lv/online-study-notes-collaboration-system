<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-24 16:36:41
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-04 16:59:55
 * @FilePath: \online-study-notes-collaboration-system\src\layout\components\userAvatar.vue
 * @Description: 个人用户管理，可以编辑用户个人信息、设置、退出登录、新建文件
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="avatar-container">
        <el-popover popper-class="avatar-popover" placement="bottom" trigger="click">
            <template #reference>
                <el-avatar :class="user.avatar ? 'avatar-has' : ''" :src="user.avatar" :size="60" fit="fill"
                    @click="isShowPopover = !isShowPopover">
                    <el-icon :size="40">
                        <UserFilled />
                    </el-icon>
                </el-avatar>
            </template>
            <ul>
                <li @click="isShowUserInfo = !isShowUserInfo"><el-icon>
                        <UserFilled />
                    </el-icon>个人信息</li>
                <!-- TODO 用户设置 -->
                <!-- <el-popover popper-class="avatar-system-color" placement="right" trigger="hover">
                    <template #reference>
                        <li style="display: flex;justify-content: space-between;">
                            <div>
                                <el-icon>
                                    <Monitor />
                                </el-icon>
                                <span>主题模式</span>
                            </div>
                            <span>></span>
                        </li>
                    </template>
                    <ul onclick="(val) => console.log(val)">
                        <li>深色</li>
                        <li>浅色</li>
                    </ul>
                </el-popover> -->
                <li><el-icon>
                        <Setting />
                    </el-icon>设置</li>
                <li @click="logOut"><el-icon>
                        <CircleCloseFilled />
                    </el-icon>退出登录</li>
            </ul>
        </el-popover>
        <!-- <el-row :gutter="12">
            <el-col :span="12">
                <el-badge is-dot>消息</el-badge>
            </el-col>
            <el-col :span="12">
                <el-badge is-dot>公告</el-badge>
            </el-col>
        </el-row> -->
        <el-popover popper-class="pop-create-file" :visible="displayCreatePop" placement="bottom-end" width="320"
            :show-arrow="false">
            <template #reference>
                <el-button style="margin-top: 5px;width: 130px;" type="primary" @click="displayCreatePop = true">
                    <el-icon :size="18">
                        <Plus />
                    </el-icon>
                    <p style="margin-left: 5px;font-size: small;">新建</p>
                </el-button>
            </template>
            <div class="content" v-click-outside="() => displayCreatePop = false">
                <span style="font-size: large;font-weight: 700;">新建文件</span>
                <el-row :gutter="15">
                    <el-col :span="6" @click="handleCreate">
                        <el-image style="width: 40px;" :src="markDownImgUrl"></el-image>
                        <span>笔记</span>
                    </el-col>
                    <el-col :span="6" @click="handleCreate">
                        <el-image style="width: 40px" :src="folderImgUrl"></el-image>
                        <span>文件夹</span>
                    </el-col>
                    <el-col :span="6">3</el-col>
                    <el-col :span="6">4</el-col>
                </el-row>
            </div>
        </el-popover>
        <user-info v-model:display-info="isShowUserInfo"></user-info>
    </div>
</template>
<script setup>
import { UserFilled, Plus, Setting, CircleCloseFilled } from '@element-plus/icons-vue';
import { computed, ref } from 'vue';
import userInfo from './userInfo.vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../store/user';
import { ClickOutside as VClickOutside } from 'element-plus';
import markDownImgUrl from '@/assets/markdown.png';
import folderImgUrl from '@/assets/folder.png';

/**
 * data
 * ---------------------------------------------
 */
const props = defineProps({
    isCollapse: {
        type: Boolean,
        default: false,
    }
})

const isShowPopover = ref(false);
const isShowUserInfo = ref(false);

const router = useRouter();

const userStore = useUserStore();
const user = computed(() => userStore.user);

// 显示新建弹框
const displayCreatePop = ref(false);

/**
 * methods
 * --------------------------------------------------
 */
const logOut = () => {
    router.push({ name: 'Login' });
}

// 提交新建事件
const handleCreate = () => {

}
</script>

<style lang="scss" scoped>
.avatar-container {
    display: flex;
    flex-direction: column;
    align-items: center;
    padding: 20px;

    .el-row {
        margin-top: 10px;

        .el-col {
            font-size: small;
        }
    }
}
</style>

<style lang="scss">
.avatar-popover,
.avatar-system-color {
    padding: 10px 0px !important;

    ul {
        list-style-type: none;

        li {
            display: flex;
            align-items: center;
            padding: 5px 10px;
            transition: all .6s;
            cursor: pointer;
        }

        li:hover {
            background-color: #efefef;
        }
    }
}

.avatar-has {
    background-color: #fff;
}

.pop-create-file {

    .content {

        .el-row {

            .el-col {
                display: flex;
                flex-direction: column;
                align-items: center;
                justify-content: center;
                cursor: pointer;


            }

            .el-col:hover {
                background-color: #efefef;
            }
        }
    }
}
</style>