import { createRouter } from "vue-router";
import { createWebHistory } from "vue-router";
import { checkToken } from '@/api/apis/token';
import { useUserStore } from "../store/user";

const routes = [
    {
        path: '/',
        redirect: '/file',
    },
    {
        path: '/login',
        name: 'Login',
        component: () => import('@/views/Login/login.vue'),
        meta: { title: '登录' },
    },
    {
        path: '/file',
        name: 'File',
        component: () => import('@/views/File/file.vue'),
        meta: { title: '首页' }
    },

    {
        path: '/:pathMath(.*)*',
        name: 'NotFound',
        component: () => import('@/views/NotFound/NotFound.vue'),
        meta: { title: 'NotFound' },
    },
];

const router = createRouter({
    routes,
    history: createWebHistory(),
})

// 路由守卫
router.beforeEach(async (to, from, next) => {
    const userStore = useUserStore();

    // 直接跳转登录页面
    if (to.path === '/login') {
        next();
    } else {
        // 跳转其它页面
        // 判断本地存储的 token 是否过期
        // const token = localStorage.getItem('token');
        const token = userStore.token;

        if (token === 'null' || !token) {
            // token为空，跳转登录页面，路由存储原先要跳转的页面路径
            console.error("token为空，请先登录");
            next({ name: 'Login', param: { redirect: to.fullPath } });
        } else {
            // 发送请求
            const result = await checkToken();
            if (result.code !== 200 || result === undefined) {
                console.error('token校验失败：', result.message);
                next({ name: 'Login', param: { redirect: to.fullPath } });
            } else {
                next();
            }
        }
    }
    document.title = to.meta.title;
})

export default router;