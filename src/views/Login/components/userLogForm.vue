<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-12 17:26:01
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-01-21 09:03:34
 * @FilePath: \online-study-notes-collaboration-system\src\views\Login\components\userLogForm.vue
 * @Description: 登录/注册表单，对表单进行滑动验证管理和表单校验。
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <el-form ref="userLogForm" :model="user" :rules="rules" label-width="auto">
        <el-form-item label="用户名" prop="name" v-show="!(props.formType === 'login')">
            <el-input v-model="user.name" clearable></el-input>
        </el-form-item>
        <el-form-item label="账号" prop="username">
            <el-input v-model="user.username" clearable></el-input>
        </el-form-item>
        <el-form-item label="密码" prop="password">
            <el-input v-model="user.password" type="password" clearable show-password></el-input>
        </el-form-item>

        <div class="buttons">
            <el-button type="primary" @click="toLogOrReg(userLogForm)">
                {{ props.formType === 'login' ? '登录' : '注册' }}
            </el-button>
        </div>
        zhangSan3
        <Vcode :show="displayVcode" @success="successVcode" @fail="failVcode" @close="displayVcode = false">
        </Vcode>
    </el-form>
</template>
<script setup>
import { ElMessage } from 'element-plus';
import { computed, onMounted, reactive, ref, watch } from 'vue';
import Vcode from "vue3-puzzle-vcode";
import { login, register } from '@/api/apis/user';
import { useUserStore } from '@/store/user';
import { useRouter, useRoute } from 'vue-router';

/**
 * data
 * ——————————————————————————————
 */
const userStore = useUserStore();
const route = useRoute();
const router = useRouter();

// 接收：表单类型
const props = defineProps({
    formType: {
        type: String,
        default: 'login'
    }
});
// 用户输入
const user = reactive({
    name: '',
    username: '',
    password: ''
});

// 表单实例
const userLogForm = ref();
//表单校验规则
const rules = reactive({
    username: [
        { required: true, message: '请输入账号', trigger: 'blur' },
        { min: 3, max: 30, message: '账号长度至少为3位', trigger: 'blur' },
        {
            pattern: /^[a-zA-Z0-9_-]{3,30}$/,
            message: '账号只能包含大小写字母、数字、下划线和连接符',
            trigger: 'blur'
        }
    ],
    password: [
        { required: true, message: '请输入密码', trigger: 'blur' },
        { min: 3, message: '密码长度至少为3位', trigger: 'blur' },
        {
            pattern: /^(?=.*\d)(?=.*[a-zA-Z]).{3,}$/,
            message: '密码必须包含至少一个字母和一个数字',
            trigger: 'blur'
        }
    ],
});

// 滑动验证框
const displayVcode = ref(false);
// 验证限制
const failVcodeNum = computed(() => userStore.verify.failVcodeNum);
const lastVcodeTime = computed(() => userStore.verify.lastVcodeTime);
const enableVcodeTime = computed(() => userStore.verify.enableVcodeTime);

/**
 * 生命周期钩子
 */
onMounted(() => {
    // console.log(router);
})

/**
 * watch
 * ——————————————————————————————————-
 */
// 监听表单类型切换
watch(() => props.formType, (newV, oldV) => {
    initUserForm();
})

/**
 * methods
 * ————————————————————————————————————
 */
/**
 * 登录或注册
 */
const toLogOrReg = (userLogForm) => {
    if (!userLogForm) return;
    userLogForm.validate((valid) => {
        if (valid) {
            // 能否进行滑动验证
            const vcodeResult = ifVcode();
            // console.log(vcodeResult);
            if (vcodeResult) {
                // 进行滑动验证
                displayVcode.value = true;
            } else {
                ElMessage.error(`还未等待满 3 分钟`);
            }
        }
    })
}

/**
 * 是否可以进行滑动验证
 */
const ifVcode = () => {
    updateVcodeData();
    const endTime = 3 * 60 * 1000;

    if (failVcodeNum <= 3) {
        return true;
    } else {
        const startTime = new Date(enableVcodeTime);

        if ((startTime.getTime() + endTime) < new Date().getTime()) {
            return false;
        } else {
            userStore.initVerify();
            return true;
        }
    }
}

/**
 * 是否更新验证数据
 */
const updateVcodeData = () => {
    if (!lastVcodeTime) return;

    const lastTime = new Date(lastVcodeTime);
    const updateTime = 30 * 60 * 1000;
    if (isNaN(lastTime.getTime())) return;

    if ((lastTime.getTime() + updateTime) < new Date().getTime()) {
        userStore.initVerify();
    }
}

/**
 * 存储验证数据
 */
const saveVcodeData = () => {
    userStore.saveVerify(failVcodeNum.value, lastVcodeTime.value, enableVcodeTime.value);
}

/**
 * 验证成功
 */
const successVcode = () => {
    userStore.initVerify();
    saveVcodeData();
    displayVcode.value = false;
    postRequest();
}
/**
 * 验证失败
 */
const failVcode = () => {
    failVcodeNum++;
    if (failVcodeNum > 3) {
        displayVcode.value = false;
        enableVcodeTime = new Date();
        ElMessage.error(`验证失败超过 3 次，限制3分钟后才能再次操作`);
    } else {
        ElMessage(`已验证失败 ${failVcodeNum} 次，超过 3 次后限制等待 3 分钟才能再操作`);
    }
    lastVcodeTime = new Date();
    saveVcodeData();
}

/**
 * 发送请求
 */
const postRequest = async () => {
    try {
        let result;
        if (props.formType === 'login') {
            result = await login(user);
        } else if (props.formType === 'register') {
            result = await register(user);
        }
        if (result.code === 200) {
            userStore.token = result.data.token;
            userStore.user = result.data.user;
            if (route.params.redirect) {
                router.push({ path: route.params.redirect });
            } else {
                router.push({ name: 'File' });
            }
        } else {
            console.error("登录注册表单请求错误：" + result.message);
        }
    } catch (err) {
        console.error("登录注册表单请求出错：" + err);
    }
}

/**
 * 清空表单输入
 */
const initUserForm = () => {
    userLogForm.value.resetFields();
}

</script>
<style lang="scss" scoped>
.buttons {
    display: flex;
    justify-content: center;
}
</style>