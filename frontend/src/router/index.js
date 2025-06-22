import { createRouter, createWebHistory } from 'vue-router'
import Home from '../views/Home.vue'
import Login from '../views/Login.vue'
import Dashboard from '../views/Dashboard.vue'
import Users from '../views/Users.vue'
import Roles from '../views/Roles.vue'
import Permissions from '../views/Permissions.vue'
import Settings from '../views/Settings.vue'
import About from '../views/About.vue'

const routes = [
  {
    path: '/',
    name: 'Home',
    component: Home,
    meta: { requiresAuth: true }
  },
  {
    path: '/login',
    name: 'Login',
    component: Login,
    meta: { requiresAuth: false }
  },
  {
    path: '/dashboard',
    name: 'Dashboard',
    component: Dashboard,
    meta: { requiresAuth: true }
  },
  {
    path: '/users',
    name: 'Users',
    component: Users,
    meta: { 
      requiresAuth: true,
      requiredRoles: ['ADMIN', 'MANAGER']
    }
  },
  {
    path: '/roles',
    name: 'Roles',
    component: Roles,
    meta: { 
      requiresAuth: true,
      requiredRoles: ['ADMIN']
    }
  },
  {
    path: '/permissions',
    name: 'Permissions',
    component: Permissions,
    meta: { 
      requiresAuth: true,
      requiredRoles: ['ADMIN']
    }
  },
  {
    path: '/settings',
    name: 'Settings',
    component: Settings,
    meta: { requiresAuth: true }
  },
  {
    path: '/about',
    name: 'About',
    component: About,
    meta: { requiresAuth: true }
  }
]

const router = createRouter({
  history: createWebHistory(),
  routes
})

// JWT令牌验证函数
const isTokenValid = (token) => {
  if (!token) return false
  
  try {
    const payload = JSON.parse(atob(token.split('.')[1]))
    const currentTime = Date.now() / 1000
    return payload.exp > currentTime
  } catch (error) {
    console.error('JWT令牌解析失败:', error)
    return false
  }
}

// 检查用户角色权限
const hasRequiredRole = (userRole, requiredRoles) => {
  if (!requiredRoles || requiredRoles.length === 0) return true
  return requiredRoles.includes(userRole)
}

// 路由守卫 - 检查JWT认证状态和角色权限
router.beforeEach((to, from, next) => {
  const accessToken = localStorage.getItem('accessToken')
  const userRole = localStorage.getItem('userRole')
  const isAuthenticated = localStorage.getItem('isLoggedIn') === 'true' && isTokenValid(accessToken)
  
  // 如果路由需要认证但用户未登录或令牌无效，重定向到登录页
  if (to.meta.requiresAuth && !isAuthenticated) {
    // 清理无效的认证状态
    localStorage.removeItem('isLoggedIn')
    localStorage.removeItem('accessToken')
    localStorage.removeItem('refreshToken')
    localStorage.removeItem('userRole')
    localStorage.removeItem('userName')
    localStorage.removeItem('username')
    next('/login')
  }
  // 检查角色权限
  else if (to.meta.requiresAuth && to.meta.requiredRoles && !hasRequiredRole(userRole, to.meta.requiredRoles)) {
    // 用户没有访问权限，重定向到首页并显示提示
    console.warn(`用户角色 ${userRole} 没有访问 ${to.path} 的权限`)
    next('/')
  }
  // 如果用户已登录但访问登录页，重定向到首页
  else if (to.name === 'Login' && isAuthenticated) {
    next('/')
  }
  // 其他情况正常导航
  else {
    next()
  }
})

export default router 