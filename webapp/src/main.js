import { createApp } from 'vue'
import './style.css'
import App from './App.vue'

import router from './router/router'
import { createPinia } from 'pinia'
import piniaPersist from 'pinia-plugin-persistedstate'
import ElementPlus from 'element-plus'
import 'element-plus/dist/index.css'
import * as ElementPlusIconsVue from '@element-plus/icons-vue'

import '../src/styles/global.scss' // 消除初始边距

const app = createApp(App);
const pinia = createPinia();
pinia.use(piniaPersist);
app.use(pinia);
app.use(ElementPlus);
app.use(router);
for (const [key, component] of Object.entries(ElementPlusIconsVue)) {
  app.component(key, component)
}

app.mount('#app')
