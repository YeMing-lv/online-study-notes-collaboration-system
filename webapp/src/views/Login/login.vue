<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 08:40:30
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-21 08:56:29
 * @FilePath: \online-study-notes-collaboration-system\src\views\Login\login.vue
 * @Description: 登录页面，可进行登录/注册，需要进行滑动验证，表单校验。
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="login-container">
        <h3>在线学习笔记协作系统</h3>
        <el-card style="padding: 20px;">
            <template #header>
                <el-row justify="center" :gutter="10">
                    <el-col :span="6" :class="{ active: isLogOrReg }" style="text-align: center;"
                        @click="isLogOrReg = !isLogOrReg"><span>登录</span></el-col>
                    <el-col id="parator" :span="2" style="text-align: center;">/</el-col>
                    <el-col :span="6" :class="{ active: !isLogOrReg }" style="text-align: center;"
                        @click="isLogOrReg = !isLogOrReg"><span>注册</span></el-col>
                </el-row>
            </template>
            <user-log-form :form-type="isLogOrReg ? 'login' : 'register'"></user-log-form>
        </el-card>
    </div>
</template>
<script setup>
import { onMounted, ref } from 'vue';
import userLogForm from './components/userLogForm.vue';
import { useUserStore } from '../../store/user';

/**
 * data
 */
// 登录、注册状态切换
const isLogOrReg = ref(true);
const userStore = useUserStore();

/**
 * 生命周期钩子
 */
onMounted(() => {
    // console.log(isLogOrReg.value);
    // 清理用户身份信息
    userStore.initState();
    // console.log(userStore.user);
})

/**
 * methods
 */

</script>

<style lang="scss" scoped>
.login-container {
    height: 100vh;
    display: flex;
    flex-direction: column;
    align-items: center;
    justify-content: center;

    h3 {
        margin: 20px auto;
    }

    .el-card {
        width: 300px;

        .card-footer {
            display: flex;
            justify-content: center;
        }

        .el-col:hover {
            color: #496eff;
            border-bottom: 3px solid;
            cursor: pointer;
            transition: all .05s;
        }

        .active {
            color: #496eff;
            border-bottom: 3px solid;
            cursor: pointer;
            transition: all .05s;
        }

        #parator {
            color: #000;
            border-bottom: none;
            transition: none;
            cursor: default;
        }
    }
}
</style>