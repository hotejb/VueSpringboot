<template>
  <nav class="navbar">
    <div class="nav-container">
      <div class="nav-brand">
        <div class="brand-icon">🚀</div>
        <h1 class="brand-title">VueSpring Admin</h1>
      </div>
      
      <div class="nav-menu" :class="{ 'nav-menu-active': isMenuOpen }">
        <router-link to="/" class="nav-link" @click="closeMenu">
          <i class="icon">🏠</i>
          <span>首页</span>
        </router-link>
        
        <div class="nav-dropdown" @mouseenter="showDropdown" @mouseleave="hideDropdown">
          <div class="nav-link dropdown-trigger">
            <i class="icon">📊</i>
            <span>功能模块</span>
            <i class="arrow">▼</i>
          </div>
          <div class="dropdown-menu" :class="{ 'dropdown-active': isDropdownOpen }">
            <router-link to="/dashboard" class="dropdown-item" @click="closeMenu">
              <i class="icon">📈</i>
              <span>数据面板</span>
            </router-link>
            <router-link to="/users" class="dropdown-item" @click="closeMenu">
              <i class="icon">👥</i>
              <span>用户管理</span>
            </router-link>
            <router-link to="/settings" class="dropdown-item" @click="closeMenu">
              <i class="icon">⚙️</i>
              <span>系统设置</span>
            </router-link>
          </div>
        </div>
        
        <router-link to="/about" class="nav-link" @click="closeMenu">
          <i class="icon">ℹ️</i>
          <span>关于</span>
        </router-link>
        
        <div class="nav-auth">
          <router-link v-if="!isLoggedIn" to="/login" class="nav-link login-btn" @click="closeMenu">
            <i class="icon">🔐</i>
            <span>登录</span>
          </router-link>
          <div v-else class="user-menu" @click="toggleUserMenu">
            <div class="user-avatar">👤</div>
            <span class="user-name">{{ userName }}</span>
            <div class="user-dropdown" :class="{ 'user-dropdown-active': isUserMenuOpen }">
              <div class="user-dropdown-item" @click="logout">
                <i class="icon">🚪</i>
                <span>退出登录</span>
              </div>
            </div>
          </div>
        </div>
      </div>
      
      <div class="nav-toggle" @click="toggleMenu">
        <span></span>
        <span></span>
        <span></span>
      </div>
    </div>
  </nav>
</template>

<script>
import { ref, computed, onMounted, onUnmounted } from 'vue'
import { useRouter } from 'vue-router'
import { authAPI, stopSessionCheck } from '../services/api'

export default {
  name: 'Navbar',
  setup() {
    const router = useRouter()
    const isMenuOpen = ref(false)
    const isDropdownOpen = ref(false)
    const isUserMenuOpen = ref(false)
    const userName = ref('')
    const isLoggedIn = ref(false)
    
    // 检查登录状态
    const checkLoginStatus = () => {
      const loginStatus = localStorage.getItem('isLoggedIn')
      const storedUserName = localStorage.getItem('userName')
      isLoggedIn.value = loginStatus === 'true'
      userName.value = storedUserName || '用户'
    }
    
    const toggleMenu = () => {
      isMenuOpen.value = !isMenuOpen.value
    }
    
    const closeMenu = () => {
      isMenuOpen.value = false
      isDropdownOpen.value = false
      isUserMenuOpen.value = false
    }
    
    const showDropdown = () => {
      isDropdownOpen.value = true
    }
    
    const hideDropdown = () => {
      setTimeout(() => {
        isDropdownOpen.value = false
      }, 200)
    }
    
    const toggleUserMenu = () => {
      isUserMenuOpen.value = !isUserMenuOpen.value
    }
    
    const logout = async () => {
      try {
        // 调用后端退出登录API（会自动停止session检查）
        await authAPI.logout()
      } catch (error) {
        console.error('退出登录错误:', error)
        // 即使API调用失败，也要停止session检查
        stopSessionCheck()
      }
      
      // 清除本地存储
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userName')
      localStorage.removeItem('username')
      userName.value = ''
      isLoggedIn.value = false
      isUserMenuOpen.value = false
      router.push('/login')
    }
    
    // 监听存储变化
    const handleStorageChange = () => {
      checkLoginStatus()
    }
    
    // 监听认证状态变化
    const handleAuthStateChange = () => {
      checkLoginStatus()
    }
    
    onMounted(() => {
      checkLoginStatus()
      // 监听localStorage变化
      window.addEventListener('storage', handleStorageChange)
      // 监听自定义登录事件
      window.addEventListener('userLogin', checkLoginStatus)
      // 监听认证状态变化事件
      window.addEventListener('authStateChanged', handleAuthStateChange)
    })
    
    onUnmounted(() => {
      // 清理事件监听器
      window.removeEventListener('storage', handleStorageChange)
      window.removeEventListener('userLogin', checkLoginStatus)
      window.removeEventListener('authStateChanged', handleAuthStateChange)
    })
    
    return {
      isMenuOpen,
      isDropdownOpen,
      isUserMenuOpen,
      isLoggedIn,
      userName,
      toggleMenu,
      closeMenu,
      showDropdown,
      hideDropdown,
      toggleUserMenu,
      logout
    }
  }
}
</script>

<style scoped>
.navbar {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 0;
  box-shadow: 0 4px 20px rgba(0,0,0,0.15);
  position: sticky;
  top: 0;
  z-index: 1000;
}

.nav-container {
  max-width: 1200px;
  margin: 0 auto;
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 0 2rem;
  height: 70px;
}

.nav-brand {
  display: flex;
  align-items: center;
  gap: 0.5rem;
}

.brand-icon {
  font-size: 2rem;
  animation: pulse 2s infinite;
}

@keyframes pulse {
  0%, 100% { transform: scale(1); }
  50% { transform: scale(1.1); }
}

.brand-title {
  margin: 0;
  font-size: 1.5rem;
  font-weight: 700;
  background: linear-gradient(45deg, #fff, #f0f0f0);
  -webkit-background-clip: text;
  -webkit-text-fill-color: transparent;
  background-clip: text;
}

.nav-menu {
  display: flex;
  align-items: center;
  gap: 2rem;
}

.nav-link {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: white;
  text-decoration: none;
  padding: 0.75rem 1rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
  overflow: hidden;
}

.nav-link::before {
  content: '';
  position: absolute;
  top: 0;
  left: -100%;
  width: 100%;
  height: 100%;
  background: linear-gradient(90deg, transparent, rgba(255,255,255,0.2), transparent);
  transition: left 0.5s;
}

.nav-link:hover::before {
  left: 100%;
}

.nav-link:hover,
.nav-link.router-link-active {
  background-color: rgba(255, 255, 255, 0.2);
  transform: translateY(-2px);
}

.nav-dropdown {
  position: relative;
}

.dropdown-trigger {
  cursor: pointer;
}

.arrow {
  font-size: 0.8rem;
  transition: transform 0.3s ease;
}

.nav-dropdown:hover .arrow {
  transform: rotate(180deg);
}

.dropdown-menu {
  position: absolute;
  top: 100%;
  left: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  min-width: 200px;
  z-index: 1001;
}

.dropdown-active {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #333;
  text-decoration: none;
  padding: 1rem 1.5rem;
  transition: all 0.3s ease;
  border-bottom: 1px solid #f0f0f0;
}

.dropdown-item:last-child {
  border-bottom: none;
}

.dropdown-item:hover {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  transform: translateX(5px);
}

.nav-auth {
  position: relative;
}

.login-btn {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  font-weight: 600;
}

.user-menu {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  padding: 0.5rem 1rem;
  border-radius: 8px;
  transition: all 0.3s ease;
  position: relative;
}

.user-menu:hover {
  background-color: rgba(255, 255, 255, 0.1);
}

.user-avatar {
  width: 32px;
  height: 32px;
  border-radius: 50%;
  background: linear-gradient(135deg, #ff9a9e 0%, #fecfef 100%);
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.user-dropdown {
  position: absolute;
  top: 100%;
  right: 0;
  background: white;
  border-radius: 8px;
  box-shadow: 0 8px 25px rgba(0,0,0,0.15);
  opacity: 0;
  visibility: hidden;
  transform: translateY(-10px);
  transition: all 0.3s ease;
  min-width: 150px;
  z-index: 1001;
}

.user-dropdown-active {
  opacity: 1;
  visibility: visible;
  transform: translateY(0);
}

.user-dropdown-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  color: #333;
  padding: 1rem 1.5rem;
  cursor: pointer;
  transition: all 0.3s ease;
}

.user-dropdown-item:hover {
  background: linear-gradient(135deg, #ff6b6b 0%, #ee5a24 100%);
  color: white;
}

.nav-toggle {
  display: none;
  flex-direction: column;
  cursor: pointer;
  gap: 4px;
}

.nav-toggle span {
  width: 25px;
  height: 3px;
  background: white;
  border-radius: 2px;
  transition: all 0.3s ease;
}

.icon {
  font-size: 1.1rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .nav-container {
    padding: 0 1rem;
  }
  
  .nav-toggle {
    display: flex;
  }
  
  .nav-menu {
    position: fixed;
    top: 70px;
    left: 0;
    width: 100%;
    height: calc(100vh - 70px);
    background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
    flex-direction: column;
    justify-content: flex-start;
    align-items: stretch;
    padding: 2rem;
    gap: 1rem;
    transform: translateX(-100%);
    transition: transform 0.3s ease;
  }
  
  .nav-menu-active {
    transform: translateX(0);
  }
  
  .nav-link {
    justify-content: flex-start;
    padding: 1rem;
    border-radius: 8px;
    background: rgba(255, 255, 255, 0.1);
  }
  
  .dropdown-menu {
    position: static;
    opacity: 1;
    visibility: visible;
    transform: none;
    background: rgba(255, 255, 255, 0.1);
    box-shadow: none;
    margin-top: 0.5rem;
  }
  
  .dropdown-item {
    color: white;
    border-bottom: 1px solid rgba(255, 255, 255, 0.1);
  }
  
  .user-dropdown {
    position: static;
    opacity: 1;
    visibility: visible;
    transform: none;
    background: rgba(255, 255, 255, 0.1);
    box-shadow: none;
    margin-top: 0.5rem;
  }
  
  .user-dropdown-item {
    color: white;
  }
}
</style> 