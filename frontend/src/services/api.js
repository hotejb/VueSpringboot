import axios from 'axios';

// 创建axios实例
const api = axios.create({
  baseURL: '/api',
  timeout: 10000,
  withCredentials: true, // 支持跨域cookie
});

// 请求拦截器
api.interceptors.request.use(
  (config) => {
    return config;
  },
  (error) => {
    return Promise.reject(error);
  }
);

// 响应拦截器
api.interceptors.response.use(
  (response) => {
    return response.data;
  },
  (error) => {
    console.error('API请求错误:', error);
    return Promise.reject(error);
  }
);

// 认证相关API
export const authAPI = {
  // 登录
  login: (credentials) => api.post('/auth/login', credentials),
  
  // 退出登录
  logout: () => api.post('/auth/logout'),
  
  // 获取当前用户信息
  getCurrentUser: () => api.get('/auth/me'),
};

// 首页相关API
export const homeAPI = {
  // 获取首页数据
  getHomeData: () => api.get('/home'),
  
  // 获取统计数据
  getStats: () => api.get('/stats'),
};

export default api; 