import { ref, computed } from 'vue'

// 用户权限状态
const userPermissions = ref([])
const userRole = ref('')

// 权限检查函数
export function usePermissions() {
  
  // 初始化用户权限
  const initPermissions = () => {
    // 从JWT令牌获取用户信息
    const accessToken = localStorage.getItem('accessToken')
    if (accessToken) {
      try {
        // 解析JWT令牌获取用户信息
        const payload = JSON.parse(atob(accessToken.split('.')[1]))
        // 假设JWT的sub字段是用户名，角色信息从localStorage获取
        userRole.value = localStorage.getItem('userRole') || ''
      } catch (error) {
        console.error('解析JWT令牌失败:', error)
      }
    }
    
    // 兼容原有的localStorage方式
    if (!userRole.value) {
      userRole.value = localStorage.getItem('userRole') || ''
    }
    const storedPermissions = JSON.parse(localStorage.getItem('userPermissions') || '[]')
    userPermissions.value = storedPermissions
  }
  
  // 检查是否有特定权限
  const hasPermission = (permissionCode) => {
    if (!permissionCode) return true
    
    // 超级管理员拥有所有权限
    if (userRole.value === 'ADMIN') return true
    
    // 检查用户是否有特定权限
    return userPermissions.value.some(permission => 
      permission.code === permissionCode && permission.status === 'ACTIVE'
    )
  }
  
  // 检查是否有任一权限
  const hasAnyPermission = (permissionCodes) => {
    if (!permissionCodes || permissionCodes.length === 0) return true
    return permissionCodes.some(code => hasPermission(code))
  }
  
  // 检查是否有所有权限
  const hasAllPermissions = (permissionCodes) => {
    if (!permissionCodes || permissionCodes.length === 0) return true
    return permissionCodes.every(code => hasPermission(code))
  }
  
  // 检查角色
  const hasRole = (role) => {
    if (!role) return true
    return userRole.value === role
  }
  
  // 检查是否有任一角色
  const hasAnyRole = (roles) => {
    if (!roles || roles.length === 0) return true
    return roles.includes(userRole.value)
  }
  
  // 计算属性：是否为管理员
  const isAdmin = computed(() => userRole.value === 'ADMIN')
  const isManager = computed(() => userRole.value === 'MANAGER' || userRole.value === 'ADMIN')
  const isUser = computed(() => userRole.value === 'USER')
  
  // 用户管理权限（基于角色的权限控制）
  const canViewUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    userRole.value === 'MANAGER' || 
    hasPermission('user:view')
  )
  const canCreateUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:create')
  )
  const canEditUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:edit')
  )
  const canDeleteUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:delete')
  )
  const canImportUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:import')
  )
  const canExportUsers = computed(() => 
    userRole.value === 'ADMIN' || 
    userRole.value === 'MANAGER' || 
    hasPermission('user:export')
  )
  const canChangeUserStatus = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:status')
  )
  const canResetPassword = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('user:password')
  )
  
  // 角色管理权限（仅ADMIN可以管理角色）
  const canViewRoles = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('role:view')
  )
  const canCreateRoles = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('role:create')
  )
  const canEditRoles = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('role:edit')
  )
  const canDeleteRoles = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('role:delete')
  )
  const canAssignRoles = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('role:assign')
  )
  
  // 权限管理权限（仅ADMIN可以管理权限）
  const canViewPermissions = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('permission:view')
  )
  const canCreatePermissions = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('permission:create')
  )
  const canEditPermissions = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('permission:edit')
  )
  const canDeletePermissions = computed(() => 
    userRole.value === 'ADMIN' || 
    hasPermission('permission:delete')
  )
  
  // 系统管理权限
  const canViewSystem = computed(() => hasPermission('system:settings'))
  const canMonitorSystem = computed(() => hasPermission('system:monitor'))
  const canBackupSystem = computed(() => hasPermission('system:backup'))
  
  return {
    // 状态
    userPermissions,
    userRole,
    
    // 方法
    initPermissions,
    hasPermission,
    hasAnyPermission,
    hasAllPermissions,
    hasRole,
    hasAnyRole,
    
    // 计算属性
    isAdmin,
    isManager,
    isUser,
    
    // 用户管理权限
    canViewUsers,
    canCreateUsers,
    canEditUsers,
    canDeleteUsers,
    canImportUsers,
    canExportUsers,
    canChangeUserStatus,
    canResetPassword,
    
    // 角色管理权限
    canViewRoles,
    canCreateRoles,
    canEditRoles,
    canDeleteRoles,
    canAssignRoles,
    
    // 权限管理权限
    canViewPermissions,
    canCreatePermissions,
    canEditPermissions,
    canDeletePermissions,
    
    // 系统管理权限
    canViewSystem,
    canMonitorSystem,
    canBackupSystem
  }
}

// 全局权限初始化
export const initGlobalPermissions = () => {
  const { initPermissions } = usePermissions()
  initPermissions()
} 