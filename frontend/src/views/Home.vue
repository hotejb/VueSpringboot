<template>
  <div class="home">
    <div class="hero-section">
      <h1 class="hero-title">欢迎来到 Vue + SpringBoot 项目</h1>
      <p class="hero-subtitle">现代化的全栈Web应用开发框架</p>
    </div>

    <div class="features-section">
      <h2 class="section-title">项目特性</h2>
      <div class="features-grid">
        <div class="feature-card">
          <div class="feature-icon">🚀</div>
          <h3>Vue 3</h3>
          <p>使用最新的Vue 3框架，享受Composition API带来的开发体验</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">⚡</div>
          <h3>Vite</h3>
          <p>极速的构建工具，提供快速的热重载和优化的生产构建</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🍃</div>
          <h3>SpringBoot</h3>
          <p>强大的Java后端框架，提供完整的企业级应用解决方案</p>
        </div>
        <div class="feature-card">
          <div class="feature-icon">🔒</div>
          <h3>安全认证</h3>
          <p>集成Spring Security，提供完整的用户认证和授权机制</p>
        </div>
      </div>
    </div>

    <div class="api-test-section">
      <h2 class="section-title">API 测试</h2>
      <div class="api-test-card">
        <button @click="testAPI" :disabled="loading" class="test-button">
          {{ loading ? '测试中...' : '测试后端连接' }}
        </button>
        <div v-if="apiMessage" class="api-result">
          <p>{{ apiMessage }}</p>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'
import { homeAPI } from '../services/api'

export default {
  name: 'Home',
  setup() {
    const loading = ref(false)
    const apiMessage = ref('')

    const testAPI = async () => {
      loading.value = true
      try {
        // 测试公开的API端点
        const response = await homeAPI.getPublicData()
        if (response.success) {
          apiMessage.value = `后端连接成功！${response.message || ''}`
        } else {
          apiMessage.value = '后端连接成功，但返回了错误信息'
        }
      } catch (error) {
        console.error('API测试失败:', error)
        // 如果公开端点失败，尝试统计端点
        try {
          const statsResponse = await homeAPI.getStats()
          if (statsResponse.success) {
            apiMessage.value = '后端连接成功！(通过统计接口验证)'
          } else {
            apiMessage.value = '后端连接失败，请检查后端服务是否启动'
          }
        } catch (statsError) {
          apiMessage.value = '后端连接失败，请检查后端服务是否启动'
        }
      } finally {
        loading.value = false
      }
    }

    return {
      loading,
      apiMessage,
      testAPI
    }
  }
}
</script>

<style scoped>
.home {
  min-height: 80vh;
}

.hero-section {
  text-align: center;
  padding: 4rem 0;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
  border-radius: 15px;
  margin-bottom: 3rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.2rem;
  color: #7f8c8d;
  max-width: 600px;
  margin: 0 auto;
}

.section-title {
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 2rem;
}

.features-section {
  margin-bottom: 3rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-top: 2rem;
}

.feature-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  text-align: center;
  transition: transform 0.3s ease;
}

.feature-card:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-card h3 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-card p {
  color: #7f8c8d;
  line-height: 1.6;
}

.api-test-section {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.api-test-card {
  text-align: center;
}

.test-button {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 1rem 2rem;
  font-size: 1.1rem;
  border-radius: 10px;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.test-button:hover:not(:disabled) {
  transform: translateY(-2px);
}

.test-button:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}

.api-result {
  margin-top: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  border-radius: 10px;
  border-left: 4px solid #667eea;
}

.api-result p {
  margin: 0;
  color: #2c3e50;
  font-weight: 500;
}
</style> 