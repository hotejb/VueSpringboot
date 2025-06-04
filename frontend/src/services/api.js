import axios from 'axios'

const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true // 支持session cookie
})

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
      localStorage.removeItem('isLoggedIn')
      localStorage.removeItem('userName')
      localStorage.removeItem('username')
      window.location.href = '/login'
    }
    return Promise.reject(error)
  }
)

export const authAPI = {
  login: (credentials) => api.post('/auth/login', credentials),
  logout: () => api.post('/auth/logout')
}

export const homeAPI = {
  getWelcomeMessage: () => api.get('/home/welcome'),
  getPublicData: () => api.get('/home'),
  getStats: () => api.get('/stats')
}

export default api 