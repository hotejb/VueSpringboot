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
  // 触发登录状态变化事件
  window.dispatchEvent(new Event('authStateChanged'))
}

// 请求拦截器
api.interceptors.request.use(
  config => {
    // 使用session认证，不需要添加token
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
  error => {
    if (error.response?.status === 401) {
      // Session过期或未认证，清理本地状态
      clearAuthState()
      // 如果当前不在登录页面，跳转到登录页
      if (window.location.pathname !== '/login') {
        window.location.href = '/login'
      }
    }
    return Promise.reject(error)
  }
)

// 定期检查session状态的函数
const checkSessionStatus = async () => {
  try {
    await api.get('/auth/me')
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
    const isLoggedIn = localStorage.getItem('isLoggedIn') === 'true'
    if (isLoggedIn) {
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
  login: (credentials) => api.post('/auth/login', credentials),
  logout: () => {
    stopSessionCheck()
    return api.post('/auth/logout')
  },
  getCurrentUser: () => api.get('/auth/me')
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
  resetPassword: (id, password) => api.patch(`/users/${id}/password`, { password })
}

export const statsAPI = {
  getStats: () => api.get('/stats')
}

export { clearAuthState, startSessionCheck, stopSessionCheck, checkSessionStatus }

export default api 