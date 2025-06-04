<template>
  <div id="app">
    <Navbar v-if="!isLoginPage" />
    <main class="main-content" :class="{ 'login-layout': isLoginPage }">
      <router-view />
    </main>
  </div>
</template>

<script>
import { computed } from 'vue'
import { useRoute } from 'vue-router'
import Navbar from './components/Navbar.vue'

export default {
  name: 'App',
  components: {
    Navbar
  },
  setup() {
    const route = useRoute()
    
    // 检查是否为登录页面
    const isLoginPage = computed(() => {
      return route.name === 'Login'
    })
    
    return {
      isLoginPage
    }
  }
}
</script>

<style scoped>
.main-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 2rem;
  min-height: calc(100vh - 70px);
}

/* 登录页面布局 */
.main-content.login-layout {
  max-width: 100%;
  padding: 0;
  min-height: 100vh;
  display: flex;
  align-items: center;
  justify-content: center;
  background: linear-gradient(135deg, #f5f7fa 0%, #c3cfe2 100%);
}
</style> 