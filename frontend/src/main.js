import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import pinia from './stores'
import './style.css'
import { startSessionCheck } from './services/api'
import { useAuthStore } from './stores/auth'

const app = createApp(App)

app.use(pinia)
app.use(router)

// 恢复认证状态
const authStore = useAuthStore()
authStore.restoreFromStorage()

// 如果有JWT令牌，启动session检查
if (authStore.accessToken) {
  startSessionCheck()
}

app.mount('#app') 