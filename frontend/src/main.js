import { createApp } from 'vue'
import App from './App.vue'
import router from './router'
import './style.css'
import { startSessionCheck } from './services/api'

const app = createApp(App)
app.use(router)
app.mount('#app')

// 如果有JWT令牌，启动session检查
if (localStorage.getItem('accessToken')) {
  startSessionCheck()
} 