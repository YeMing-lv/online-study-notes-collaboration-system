<!--
 * @Author: Yeming-lv 1602552896@qq.com
 * @Date: 2025-12-11 08:43:14
 * @LastEditors: Yeming-lv 1602552896@qq.com
 * @LastEditTime: 2026-03-10 17:09:35
 * @FilePath: \online-study-notes-collaboration-system\src\views\File\file.vue
 * @Description: 主要页面，包含了侧边文件夹导航栏、侧边文件夹内容导航栏、文件编辑器
 * 
 * Copyright (c) 2026 by ${git_name_email}, All Rights Reserved. 
-->
<template>
    <div class="file-container">
        <side-bar :my-folders="folders" @create-file="handleCreateFile" @rename-file="handleRenameFile"
            @delete-file="handleDeleteFile"></side-bar>
        <side-folder></side-folder>
        <editor></editor>
        <el-button type="primary" size="default" @click="sendMessage('前端发送消息')">前端发送消息</el-button>

    </div>
</template>
<script setup>
import sideBar from '@/layout/sideBar.vue';
import sideFolder from '@/layout/sideFolder.vue';
import editor from '../../layout/editor.vue';
import { listFolderByUserId } from '../../api/apis/folder';
import { onMounted, onUnmounted, reactive } from 'vue';
import { useUserStore } from '../../store/user';
import { ElMessage } from 'element-plus';
import { updateFolder, createFolder, deleteFolder } from '../../api/apis/folder';
import { Stomp, Client } from '@stomp/stompjs';
import SockJS from 'sockjs-client/dist/sockjs.min.js';

//======================================data========================================
const folders = reactive([]);
const userStore = useUserStore();

let stompClient = {};

//=====================================钩子函数+====================================
onMounted(() => {
    getFolders();

    // connectWebSocket(userStore.token, handleMessage, (error) => {
    //     alert('WebSocket连接失败：'+ error);
    // })


    // 连接到每个端点
    connectToEndpoint('/ws-native', '/ws/message1', '/topic/messages1');
})

// 封装WebSocket连接方法
function connectToEndpoint(endpoint, messagePath, subscriptionPath) {
    // 1. 初始化Client客户端
    stompClient = new Client({
        brokerURL: `ws://localhost:8082${endpoint}`,
        connectHeaders: {
            Authorization: userStore.token
        },
        onConnect: (message) => {
            console.log("Stomp Connect: " + message);
            // 订阅消息（接收后端推送/回复）
            stompClient.subscribe('/topic/messages1', (message) => {
                console.log('收到后端消息:', message.body);
                // 可在这里处理收到的消息（如展示到页面）
            });
        },
        onChangeState: (mes) => {
            console.log("Stomp ChangeState: " + mes);
        },

    });

    stompClient.activate();
}

const sendMessage = (message) => {
    // console.log(stompClient);
    stompClient.publish({
        destination: '/ws/message1',
        headers: {
            Authorization: userStore.token
        },
        body: message
    });
}


onUnmounted(() => {
    // disconnectWebSocket();
})

//====================================methods========================================
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

// 重命名文件
const handleRenameFile = async (folder) => {
    try {
        const result = await updateFolder(folder);
        if (result.code === 200) {
            getFolders();
        }
    } catch (error) {
        console.error(error);
    }
}

// 新建文件
const handleCreateFile = async ({ type, data }) => {
    try {
        switch (type) {
            case 'Folder':
                const result = await createFolder({
                    id: data.id,
                    userId: userStore.user.id,
                    name: data.name,
                    parentId: data.parentId
                });
                if (result.code === 200) {
                    getFolders();
                }
            //TODO 新建笔记
        }
    } catch (error) {
        console.error(error);
    }
}

// 删除文件
const handleDeleteFile = async (id) => {
    try {
        const result = await deleteFolder(id);
        if (result.code === 200) {
            getFolders();
        }
    } catch (error) {
        console.error(error);
    }
}

// 建立连接
const connectStomp = () => {
    stompClient.connect({
        token: userStore.token, // 有效 Token
        // headers: {
        //   'X-Extra-Header': 'extra-value' // 额外自定义头
        // }
    });
};

// // 连接WebSocket
// const connectWebSocket = () => {
//     const websocket = new SockJS('http://localhost:8082/websocket');
//     stompClient = Stomp.over(websocket);
//     const headers = {
//         Authorization: userStore.token,
//     };

//     // 4. 建立连接
//     stompClient.connect(
//         headers, // 握手时的请求头（核心：传递认证信息）
//         (frame) => { // 连接成功的回调
//             console.log('Stomp 连接成功：', frame);
//             console.log("Stomp对象：", stompClient);

//             // // 5. 订阅服务端的广播主题
//             // stompClient.subscribe('/topic/messages', (message) => {
//             //     // 解析服务端发送的消息体
//             //     const content = JSON.parse(message.body)
//             //     console.log('收到广播消息：', content)
//             // })

//             // // 6. 可选：订阅点对点消息（比如指定用户的消息）
//             // stompClient.subscribe('/queue/user/123', (message) => {
//             //     console.log('收到个人消息：', message.body)
//             // })
//         },
//         (error) => { // 连接失败的回调
//             console.error('Stomp 连接失败：', error)
//             // 重连逻辑（可选）
//             // setTimeout(() => connectWebSocket(), 3000)
//         }
//     )
// }

// // 断开WebSocket连接
// const disconnectWebSocket = () => {
//     if (stompClient && stompClient.connected) {
//         stompClient.disconnect(() => {
//             console.log("Stomp 连接已断开");
//         });
//         stompClient = null;
//     }
// }

// 接收消息的回调

const handleMessage = (msg) => {
    console.log(msg);
};

// 发送消息
// const send = () => {
//   if (!inputContent.value) return;
//   sendMessage(inputContent.value);
//   inputContent.value = '';
// };

</script>

<style lang="scss" scoped>
.file-container {
    display: flex;
    overflow: hidden;
    height: 100vh;
}
</style>