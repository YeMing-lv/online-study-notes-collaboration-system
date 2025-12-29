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
        <el-button style="margin-top: 5px;width: 130px;" type="primary">
            <el-icon :size="18">
                <Plus />
            </el-icon>
            <p style="margin-left: 5px;font-size: small;">新建</p>
        </el-button>
        <user-info v-model:display-info="isShowUserInfo"></user-info>
    </div>
</template>
<script setup>
import { UserFilled, Plus, Setting, CircleCloseFilled } from '@element-plus/icons-vue';
import { computed, ref } from 'vue';
import userInfo from './userInfo.vue';
import { useRouter } from 'vue-router';
import { useUserStore } from '../../store/user';

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

/**
 * methods
 * --------------------------------------------------
 */
const logOut = () => {
    router.push({ name: 'Login' });
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
</style>