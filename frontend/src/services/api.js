import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true, // 支持session cookie
  headers: {
    'Content-Type': 'application/json'
  }
})

// 清理登录状态的函数
const clearAuthState = () => {
  localStorage.removeItem('isLoggedIn')
  localStorage.removeItem('userName')
  localStorage.removeItem('username')
  localStorage.removeItem('accessToken')
  localStorage.removeItem('refreshToken')
  // 触发登录状态变化事件
  window.dispatchEvent(new Event('authStateChanged'))
}

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 添加JWT令牌到请求头
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      config.headers.Authorization = `Bearer ${accessToken}`
    }
    return config
  },
  error => {
    return Promise.reject(error)
  }
)

// 响应拦截器
api.interceptors.response.use(
  response => {
    return response.data
  },
  async error => {
    if (error.response?.status === 401) {
      // JWT令牌过期，尝试刷新令牌
      const refreshToken = localStorage.getItem('refreshToken')
      if (refreshToken && !error.config._retry) {
        error.config._retry = true
        
        try {
          const response = await axios.post('/api/v2/auth/refresh', {
            refreshToken: refreshToken
          })
          
          if (response.data.success) {
            const newAccessToken = response.data.data.accessToken
            localStorage.setItem('accessToken', newAccessToken)
            
            // 重新发送原请求
            error.config.headers.Authorization = `Bearer ${newAccessToken}`
            return api.request(error.config)
          }
        } catch (refreshError) {
          console.log('刷新令牌失败:', refreshError)
        }
      }
      
      // 刷新失败或没有刷新令牌，清理状态并跳转登录
      clearAuthState()
      localStorage.removeItem('accessToken')
      localStorage.removeItem('refreshToken')
      
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// 定期检查JWT令牌状态的函数
const checkSessionStatus = async () => {
  try {
    await api.get('/v2/auth/me')
    return true
  } catch (error) {
    if (error.response?.status === 401) {
      clearAuthState()
      return false
    }
    return true // 网络错误等其他情况不清理状态
  }
}

// 启动session状态检查定时器（每5分钟检查一次）
let sessionCheckInterval = null

const startSessionCheck = () => {
  if (sessionCheckInterval) {
    clearInterval(sessionCheckInterval)
  }
  
  sessionCheckInterval = setInterval(async () => {
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      await checkSessionStatus()
    }
  }, 5 * 60 * 1000) // 5分钟检查一次
}

const stopSessionCheck = () => {
  if (sessionCheckInterval) {
    clearInterval(sessionCheckInterval)
    sessionCheckInterval = null
  }
}

export const authAPI = {
  // 使用JWT认证端点
  login: (credentials) => api.post('/v2/auth/login', credentials),
  logout: () => {
    stopSessionCheck()
    return api.post('/v2/auth/logout')
  },
  getCurrentUser: () => api.get('/v2/auth/me'),
  refreshToken: (refreshToken) => api.post('/v2/auth/refresh', { refreshToken }),
  
  // 保留Session端点用于兼容
  sessionLogin: (credentials) => api.post('/auth/login', credentials),
  sessionLogout: () => api.post('/auth/logout'),
  sessionGetCurrentUser: () => api.get('/auth/me')
}

export const homeAPI = {
  getWelcomeMessage: () => api.get('/home/welcome'),
  getPublicData: () => api.get('/home'),
  getStats: () => api.get('/stats')
}

export const userAPI = {
  getUsers: (params) => api.get('/users', { params }),
  createUser: (userData) => api.post('/users', userData),
  updateUser: (id, userData) => api.put(`/users/${id}`, userData),
  deleteUser: (id) => api.delete(`/users/${id}`),
  updateUserStatus: (id, status) => api.patch(`/users/${id}/status`, { status }),
  resetPassword: (id, password) => api.patch(`/users/${id}/password`, { password }),
  exportUsers: () => api.get('/users/export', { responseType: 'blob' }),
  downloadTemplate: () => api.get('/users/import/template', { responseType: 'blob' }),
  importUsers: (formData) => api.post('/users/import', formData, {
    headers: {
      'Content-Type': 'multipart/form-data'
    }
  })
}

export const statsAPI = {
  getStats: () => api.get('/stats')
}

export { clearAuthState, startSessionCheck, stopSessionCheck, checkSessionStatus }

export default api 