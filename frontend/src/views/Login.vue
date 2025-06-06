<template>
  <div class="login">
    <!-- 背景装饰元素 -->
    <div class="background-decoration">
      <div class="floating-shape shape-1"></div>
      <div class="floating-shape shape-2"></div>
      <div class="floating-shape shape-3"></div>
      <div class="floating-shape shape-4"></div>
    </div>
    
    <div class="login-container">
      <div class="login-card">
        <!-- 品牌标识 -->
        <div class="brand-section">
          <div class="brand-icon">🚀</div>
          <h1 class="brand-title">VueSpring Admin</h1>
          <p class="brand-subtitle">现代化管理系统</p>
        </div>
        
        <!-- 登录表单 -->
        <div class="form-section">
          <h2 class="login-title">欢迎回来</h2>
          <p class="login-subtitle">请登录您的账户</p>
          
          <form @submit.prevent="handleLogin" class="login-form">
            <div class="form-group">
              <div class="input-wrapper">
                <div class="input-icon">👤</div>
                <input
                  id="username"
                  v-model="loginForm.username"
                  type="text"
                  placeholder="用户名"
                  required
                  class="form-input"
                />
              </div>
            </div>
            
            <div class="form-group">
              <div class="input-wrapper">
                <div class="input-icon">🔒</div>
                <input
                  id="password"
                  v-model="loginForm.password"
                  type="password"
                  placeholder="密码"
                  required
                  class="form-input"
                />
              </div>
            </div>
            
            <button type="submit" :disabled="loading" class="login-button">
              <span v-if="loading" class="loading-spinner"></span>
              {{ loading ? '登录中...' : '立即登录' }}
            </button>
          </form>
          
          <div v-if="message" class="message" :class="messageType">
            {{ message }}
          </div>
        </div>
        
        <!-- 演示信息 -->
        <div class="demo-section">
          <div class="demo-header">
            <span class="demo-icon">💡</span>
            <span>演示账号</span>
          </div>
          <div class="demo-accounts">
            <div class="demo-account">
              <span class="account-type">管理员</span>
              <span class="account-info">admin / 123456</span>
            </div>
            <div class="demo-account">
              <span class="account-type">普通用户</span>
              <span class="account-info">user / 123456</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, reactive } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, startSessionCheck } from '../services/api'

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
          // 保存JWT令牌和用户信息
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('accessToken', response.data.accessToken)
          localStorage.setItem('refreshToken', response.data.refreshToken)
          
          if (response.data && response.data.user) {
            localStorage.setItem('userName', response.data.user.fullName)
            localStorage.setItem('username', response.data.user.username)
          }
          
          // 触发登录事件通知其他组件
          window.dispatchEvent(new Event('userLogin'))
          
          // 启动session状态检查
          startSessionCheck()
          
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
/* 主容器 - 完全居中 */
.login {
  position: relative;
  width: 100%;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  padding: 2rem;
  box-sizing: border-box;
  overflow: hidden;
}

/* 背景装饰 */
.background-decoration {
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  pointer-events: none;
  z-index: 1;
}

.floating-shape {
  position: absolute;
  border-radius: 50%;
  background: linear-gradient(135deg, rgba(102, 126, 234, 0.1), rgba(118, 75, 162, 0.1));
  animation: float 6s ease-in-out infinite;
}

.shape-1 {
  width: 200px;
  height: 200px;
  top: 10%;
  left: 10%;
  animation-delay: 0s;
}

.shape-2 {
  width: 150px;
  height: 150px;
  top: 20%;
  right: 15%;
  animation-delay: 2s;
}

.shape-3 {
  width: 100px;
  height: 100px;
  bottom: 20%;
  left: 20%;
  animation-delay: 4s;
}

.shape-4 {
  width: 120px;
  height: 120px;
  bottom: 15%;
  right: 10%;
  animation-delay: 1s;
}

@keyframes float {
  0%, 100% { transform: translateY(0px) rotate(0deg); }
  50% { transform: translateY(-20px) rotate(180deg); }
}

/* 登录容器 */
.login-container {
  position: relative;
  z-index: 2;
  width: 100%;
  max-width: 450px;
}

/* 登录卡片 */
.login-card {
  background: rgba(255, 255, 255, 0.95);
  backdrop-filter: blur(10px);
  border: 1px solid rgba(255, 255, 255, 0.2);
  border-radius: 20px;
  box-shadow: 
    0 20px 40px rgba(0, 0, 0, 0.1),
    0 0 0 1px rgba(255, 255, 255, 0.1);
  overflow: hidden;
  animation: slideUp 0.8s ease-out;
}

@keyframes slideUp {
  from {
    opacity: 0;
    transform: translateY(30px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* 品牌区域 */
.brand-section {
  text-align: center;
  padding: 2.5rem 2.5rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.brand-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.brand-title {
  font-size: 1.8rem;
  font-weight: 700;
  margin: 0 0 0.5rem 0;
  text-shadow: 0 2px 4px rgba(0, 0, 0, 0.1);
}

.brand-subtitle {
  font-size: 1rem;
  opacity: 0.9;
  margin: 0;
  font-weight: 300;
}

/* 表单区域 */
.form-section {
  padding: 2rem 2.5rem;
}

.login-title {
  text-align: center;
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}

.login-subtitle {
  text-align: center;
  color: #6c757d;
  margin: 0 0 2rem 0;
  font-size: 0.95rem;
}

.login-form {
  margin-bottom: 1.5rem;
}

.form-group {
  margin-bottom: 1.5rem;
}

.input-wrapper {
  position: relative;
  display: flex;
  align-items: center;
}

.input-icon {
  position: absolute;
  left: 1rem;
  font-size: 1.2rem;
  color: #6c757d;
  z-index: 1;
}

.form-input {
  width: 100%;
  padding: 1rem 1rem 1rem 3rem;
  border: 2px solid #e9ecef;
  border-radius: 12px;
  font-size: 1rem;
  transition: all 0.3s ease;
  box-sizing: border-box;
  background: #f8f9fa;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
  background: white;
  box-shadow: 0 0 0 3px rgba(102, 126, 234, 0.1);
  transform: translateY(-1px);
}

.form-input::placeholder {
  color: #adb5bd;
}

/* 登录按钮 */
.login-button {
  width: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1rem;
  font-size: 1.1rem;
  font-weight: 600;
  border-radius: 12px;
  cursor: pointer;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.login-button::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255, 255, 255, 0.2), transparent);
  transition: left 0.5s;
}

.login-button:hover:not(:disabled)::before {
  left: 100%;
}

.login-button:hover:not(:disabled) {
  transform: translateY(-2px);
  box-shadow: 0 10px 25px rgba(102, 126, 234, 0.3);
}

.login-button:disabled {
  opacity: 0.7;
  cursor: not-allowed;
  transform: none;
}

.loading-spinner {
  display: inline-block;
  width: 16px;
  height: 16px;
  border: 2px solid rgba(255, 255, 255, 0.3);
  border-radius: 50%;
  border-top-color: white;
  animation: spin 1s ease-in-out infinite;
  margin-right: 0.5rem;
}

@keyframes spin {
  to { transform: rotate(360deg); }
}

/* 消息提示 */
.message {
  padding: 1rem;
  border-radius: 12px;
  text-align: center;
  font-weight: 500;
  margin-bottom: 1rem;
  animation: fadeIn 0.3s ease-out;
}

@keyframes fadeIn {
  from { opacity: 0; transform: translateY(-10px); }
  to { opacity: 1; transform: translateY(0); }
}

.message.success {
  background: linear-gradient(135deg, #d4edda, #c3e6cb);
  color: #155724;
  border: 1px solid #c3e6cb;
}

.message.error {
  background: linear-gradient(135deg, #f8d7da, #f5c6cb);
  color: #721c24;
  border: 1px solid #f5c6cb;
}

/* 演示区域 */
.demo-section {
  background: #f8f9fa;
  padding: 1.5rem 2.5rem 2rem;
  border-top: 1px solid #e9ecef;
}

.demo-header {
  display: flex;
  align-items: center;
  justify-content: center;
  margin-bottom: 1rem;
  font-weight: 600;
  color: #495057;
  font-size: 0.95rem;
}

.demo-icon {
  margin-right: 0.5rem;
  font-size: 1.1rem;
}

.demo-accounts {
  display: flex;
  flex-direction: column;
  gap: 0.75rem;
}

.demo-account {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0.75rem 1rem;
  background: white;
  border-radius: 8px;
  border: 1px solid #e9ecef;
  transition: all 0.2s ease;
}

.demo-account:hover {
  border-color: #667eea;
  transform: translateX(2px);
}

.account-type {
  font-weight: 500;
  color: #495057;
  font-size: 0.9rem;
}

.account-info {
  font-family: 'Courier New', monospace;
  color: #667eea;
  font-weight: 600;
  font-size: 0.85rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .login {
    padding: 1rem;
  }
  
  .login-container {
    max-width: 100%;
  }
  
  .brand-section {
    padding: 2rem 1.5rem 1rem;
  }
  
  .form-section {
    padding: 1.5rem;
  }
  
  .demo-section {
    padding: 1rem 1.5rem 1.5rem;
  }
  
  .brand-title {
    font-size: 1.5rem;
  }
  
  .floating-shape {
    opacity: 0.5;
  }
}
</style> 