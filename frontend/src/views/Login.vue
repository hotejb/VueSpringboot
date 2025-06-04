<template>
  <div class="login">
    <div class="login-container">
      <div class="login-card">
        <h2 class="login-title">用户登录</h2>
        <form @submit.prevent="handleLogin" class="login-form">
          <div class="form-group">
            <label for="username">用户名</label>
            <input
              id="username"
              v-model="loginForm.username"
              type="text"
              placeholder="请输入用户名"
              required
              class="form-input"
            />
          </div>
          <div class="form-group">
            <label for="password">密码</label>
            <input
              id="password"
              v-model="loginForm.password"
              type="password"
              placeholder="请输入密码"
              required
              class="form-input"
            />
          </div>
          <button type="submit" :disabled="loading" class="login-button">
            {{ loading ? '登录中...' : '登录' }}
          </button>
        </form>
        
        <div v-if="message" class="message" :class="messageType">
          {{ message }}
        </div>

        <div class="demo-info">
          <h3>演示账号</h3>
          <p><strong>用户名:</strong> admin</p>
          <p><strong>密码:</strong> 123456</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI } from '../services/api'

export default {
  name: 'Login',
  setup() {
    const router = useRouter()
    const loading = ref(false)
    const message = ref('')
    const messageType = ref('')
    
    const loginForm = reactive({
      username: '',
      password: ''
    })

    const handleLogin = async () => {
      loading.value = true
      message.value = ''
      
      try {
        const response = await authAPI.login(loginForm)
        
        if (response.success) {
          // 保存token和用户信息
          if (response.token) {
            localStorage.setItem('token', response.token)
          }
          if (response.data && response.data.fullName) {
            localStorage.setItem('userName', response.data.fullName)
          }
          
          // 触发登录事件通知其他组件
          window.dispatchEvent(new Event('userLogin'))
          
          message.value = '登录成功！'
          messageType.value = 'success'
          
          // 延迟跳转到首页
          setTimeout(() => {
            router.push('/')
          }, 1000)
        } else {
          message.value = response.message || '登录失败'
          messageType.value = 'error'
        }
      } catch (error) {
        console.error('登录错误:', error)
        message.value = error.response?.data?.message || '登录失败，请检查网络连接'
        messageType.value = 'error'
      } finally {
        loading.value = false
      }
    }

    return {
      loginForm,
      loading,
      message,
      messageType,
      handleLogin
    }
  }
}
</script>

<style scoped>
.login {
  min-height: 80vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  padding: 2rem;
}

.login-container {
  width: 100%;
  max-width: 400px;
}

.login-card {
  background: white;
  padding: 2.5rem;
  border-radius: 15px;
  box-shadow: 0 10px 30px rgba(0,0,0,0.1);
}

.login-title {
  text-align: center;
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 2rem;
}

.login-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.login-button {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem;
  font-size: 1.1rem;
  font-weight: 500;
  border-radius: 8px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.login-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.message {
  padding: 0.75rem;
  border-radius: 8px;
  text-align: center;
  font-weight: 500;
  margin-bottom: 1rem;
}

.message.success {
  background: #d4edda;
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: #f8d7da;
  color: #721c24;
  border: 1px solid #f5c6cb;
}

.demo-info {
  background: #f8f9fa;
  padding: 1.5rem;
  border-radius: 8px;
  border-left: 4px solid #667eea;
}

.demo-info h3 {
  margin: 0 0 1rem 0;
  color: #2c3e50;
  font-size: 1.1rem;
}

.demo-info p {
  margin: 0.5rem 0;
  color: #6c757d;
}

.demo-info strong {
  color: #2c3e50;
}
</style> 