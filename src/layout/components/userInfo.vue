<template>
    <el-dialog v-model="displayInfo" title="个人信息" width="500" :append-to-body="true">
        <el-form ref="userForm" :model="user" :rules="rules" label-width="75" label-suffix=":">
            <el-form-item label="头像">
                <el-row :gutter="40">
                    <el-col :span="6">
                        <el-avatar :class="user.avatar ? 'avatar-has' : ''" :src="user.avatar" :size="70" fit="fill">
                            <el-icon>
                                <UserFilled />
                            </el-icon>
                        </el-avatar>
                    </el-col>
                    <el-col :span="18">
                        <el-upload action="http://localhost:8082/image/upload?type=userHead" :headers="uploadHeaders"
                            :show-file-list="false" :before-upload="beforeUpload" :on-success="uploadSuccess">
                            <el-button class="upload-avatar-button">上传头像</el-button><br>
                        </el-upload>
                        <span style="font-size: smaller;">支持JPG、PNG格式，小于2MB</span>
                    </el-col>
                </el-row>
            </el-form-item>
            <el-row>
                <el-col :span="12">
                    <el-form-item label="账号">
                        {{ user.username }}
                    </el-form-item>
                </el-col>
                <el-col :span="12">
                    <el-form-item label="注册日期">
                        {{ formatTime(user.createTime, 'YYYY-HH-DD') }}
                    </el-form-item>
                </el-col>
            </el-row>
            <el-form-item label="用户名" prop="name">
                <el-input v-model="user.name" clearable></el-input>
            </el-form-item>
            <el-form-item label="邮箱" prop="email">
                <el-input v-model="user.email" clearable></el-input>
            </el-form-item>
        </el-form>
        <template #footer>
            <el-button @click="submitUser(userForm)">提交</el-button>
            <el-button @click="displayInfo = false">取消</el-button>
        </template>
    </el-dialog>
</template>
<script setup>
import { onMounted, reactive, ref } from 'vue';
import { useUserStore } from '../../store/user';
import { formatTime } from '../../utils/timeHandle';
import { UserFilled } from '@element-plus/icons-vue';
import { updateUser } from '../../api/apis/user';
import { ElMessage } from 'element-plus';

/**
 * data
 * -------------------------------------------------
 */
const displayInfo = defineModel('displayInfo', {
    type: Boolean,
    default: false,
});
const userStore = useUserStore();
const user = reactive(JSON.parse(JSON.stringify(userStore.user)));

const uploadHeaders = {
    Authorization: userStore.token
}

const userForm = ref();

// 表单
const rules = reactive({
    name: [{ min: 3, max: 30, message: '用户名长度最小3，最大30', trigger: 'blur' }],
    email: [{
        pattern: /^[a-zA-Z0-9_-]+@[a-zA-Z0-9_-]+(\.[a-zA-Z0-9_-]+)+$/,
        message: '邮箱格式错误',
        trigger: 'blur'
    }]
})

/**
 * 钩子函数
 * ---------------------------------------------------
 */
onMounted(() => {
    // console.log(displayInfo);
    // console.log(JSON.stringify(user.value))
    // console.log(userStore.token);

})

/**
 * methods
 * -------------------------------------------------
 */
// 上传头像之前
const beforeUpload = (file) => {
    console.log(file);
    if (file.type !== 'image/png' && file.type !== 'image/jpg') {
        ElMessage.warning('上传类型错误！');
        return false;
    }
    if (file.size / 1024 / 1024 > 2) {
        ElMessage.warning('上传头像大小不能超过2MB！');
        return false;
    }
    return true;
}

// 上传头像成功
const uploadSuccess = async (event) => {
    console.log('上传进度:', event);
    if (event.errno === 0) { //上传成功
        try {
            user.avatar = event.data.url;
            // 更新用户头像
            const result = await updateUser(user);
            if (result.code = 200) {
                userStore.user.avatar = event.data.url;
            }
        } catch (error) {
            console.error("更新用户头像失败", error);
        }
    }
}

// 提交用户表单
const submitUser = (userForm) => {
    console.log(JSON.parse(JSON.stringify(userForm)));
    if (!userForm) return;
    userForm.validate(async (valid) => {
        if (valid) {
            try {
                const result = await updateUser(user);
                if (result.code === 200) {
                    ElMessage.success('更新用户信息成功');
                    displayInfo.value = false;
                } else {
                    ElMessage.warning('更新用户信息失败');
                }
            } catch (err) {
                ElMessage.warning('更新用户信息出错');
                console.error('更新用户信息出错' + err);
            }
        }
    })
}
</script>

<style lang="scss" scoped>
.upload-avatar-button {
    padding: 5px;
    height: auto;
    font-size: small;
}

.avatar-has {
    background-color: #fff;
}
</style>