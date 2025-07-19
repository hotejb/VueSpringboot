import { defineStore } from 'pinia'
import { authAPI } from '../services/api'

export const useAuthStore = defineStore('auth', {
  state: () => ({
    isLoggedIn: false,
    user: null,
    accessToken: null,
    refreshToken: null,
    userRole: null,
    permissions: []
  }),

  getters: {
    isAuthenticated: (state) => state.isLoggedIn && !!state.accessToken,
    hasRole: (state) => (role) => state.userRole === role,
    hasPermission: (state) => (permission) => state.permissions.includes(permission),
    hasAnyRole: (state) => (roles) => roles.includes(state.userRole)
  },

  actions: {
    async login(credentials) {
      try {
        const response = await authAPI.login(credentials)
        
        if (response.success) {
          const { accessToken, refreshToken, user } = response.data
          
          this.isLoggedIn = true
          this.user = user
          this.accessToken = accessToken
          this.refreshToken = refreshToken
          this.userRole = user.role
          this.permissions = user.permissions || []
          
          // 同步到localStorage以保持兼容性
          localStorage.setItem('isLoggedIn', 'true')
          localStorage.setItem('accessToken', accessToken)
          localStorage.setItem('refreshToken', refreshToken)
          localStorage.setItem('userName', user.name)
          localStorage.setItem('userRole', user.role)
          
          return { success: true }
        }
        
        return { success: false, message: response.message }
      } catch (error) {
        console.error('登录失败:', error)
        return { 
          success: false, 
          message: error.response?.data?.message || '登录失败，请重试' 
        }
      }
    },

    async logout() {
      try {
        await authAPI.logout()
      } catch (error) {
        console.error('退出登录请求失败:', error)
      } finally {
        this.clearAuthState()
      }
    },

    async refreshAccessToken() {
      if (!this.refreshToken) {
        this.clearAuthState()
        return false
      }

      try {
        const response = await authAPI.refreshToken(this.refreshToken)
        
        if (response.success) {
          this.accessToken = response.data.accessToken
          localStorage.setItem('accessToken', response.data.accessToken)
          return true
        }
        
        this.clearAuthState()
        return false
      } catch (error) {
        console.error('刷新令牌失败:', error)
        this.clearAuthState()
        return false
      }
    },

    async getCurrentUser() {
      try {
        const response = await authAPI.getCurrentUser()
        
        if (response.success) {
          this.user = response.data
          this.userRole = response.data.role
          this.permissions = response.data.permissions || []
          
          localStorage.setItem('userName', response.data.name)
          localStorage.setItem('userRole', response.data.role)
        }
        
        return response
      } catch (error) {
        console.error('获取用户信息失败:', error)
        throw error
      }
    },

    clearAuthState() {
      this.isLoggedIn = false
      this.user = null
      this.accessToken = null
      this.refreshToken = null
      this.userRole = null
      this.permissions = []
      
      // 清理localStorage
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      localStorage.removeItem('userName')
      localStorage.removeItem('userRole')
      localStorage.removeItem('username')
      
      // 触发认证状态变化事件
      window.dispatchEvent(new Event('authStateChanged'))
    },

    // 从localStorage恢复状态
    restoreFromStorage() {
      const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
      const accessToken = localStorage.getItem('accessToken')
      const refreshToken = localStorage.getItem('refreshToken')
      const userName = localStorage.getItem('userName')
      const userRole = localStorage.getItem('userRole')
      
      if (isLoggedIn && accessToken) {
        this.isLoggedIn = true
        this.accessToken = accessToken
        this.refreshToken = refreshToken
        this.userRole = userRole
        this.user = { name: userName, role: userRole }
      }
    }
  },

  persist: {
    key: 'auth-store',
    storage: localStorage,
    paths: ['isLoggedIn', 'user', 'accessToken', 'refreshToken', 'userRole', 'permissions']
  }
})