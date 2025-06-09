<template>
  <div class="users">
    <div class="users-header">
      <h1 class="page-title">👥 用户管理</h1>
      <div class="header-actions">
        <button class="action-btn export" @click="exportUsers" title="导出Excel">
          📊 导出
        </button>
        <button class="action-btn template" @click="downloadTemplate" title="下载导入模板">
          📋 模板
        </button>
        <button class="action-btn import" @click="triggerImport" title="导入Excel">
          📥 导入
        </button>
        <input 
          ref="fileInput" 
          type="file" 
          accept=".xlsx,.xls" 
          @change="handleFileImport" 
          style="display: none"
        />
        <button class="add-user-btn" @click="showAddModal = true">
          <i class="icon">➕</i>
          添加用户
        </button>
      </div>
    </div>

    <div class="users-filters">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          @input="searchUsers"
          type="text" 
          placeholder="搜索用户名、姓名或邮箱..." 
          class="search-input"
        />
        <i class="search-icon">🔍</i>
      </div>
      <select v-model="statusFilter" @change="loadUsers" class="status-filter">
        <option value="">所有状态</option>
        <option value="ACTIVE">激活</option>
        <option value="INACTIVE">禁用</option>
        <option value="PENDING">待审核</option>
      </select>
      <select v-model="roleFilter" @change="loadUsers" class="role-filter">
        <option value="">所有角色</option>
        <option value="ADMIN">管理员</option>
        <option value="MANAGER">经理</option>
        <option value="USER">普通用户</option>
      </select>
    </div>

    <div class="users-stats">
      <div class="stat-card">
        <div class="stat-number">{{ totalUsers }}</div>
        <div class="stat-label">总用户数</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ activeUsers }}</div>
        <div class="stat-label">活跃用户</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ currentPage + 1 }}</div>
        <div class="stat-label">当前页</div>
      </div>
      <div class="stat-card">
        <div class="stat-number">{{ totalPages }}</div>
        <div class="stat-label">总页数</div>
      </div>
    </div>

    <div class="users-table" v-if="!loading">
      <div class="table-header">
        <div class="table-cell">头像</div>
        <div class="table-cell">用户信息</div>
        <div class="table-cell">联系方式</div>
        <div class="table-cell">部门职位</div>
        <div class="table-cell">角色状态</div>
        <div class="table-cell">创建时间</div>
        <div class="table-cell">操作</div>
      </div>
      
      <div class="table-body">
        <div 
          v-for="user in users" 
          :key="user.id" 
          class="table-row"
        >
          <div class="table-cell">
            <div class="user-avatar" :style="{ background: getAvatarColor(user.username) }">
              {{ user.fullName ? user.fullName.charAt(0) : user.username.charAt(0).toUpperCase() }}
            </div>
          </div>
          <div class="table-cell">
            <div class="user-info">
              <span class="user-name">{{ user.fullName || user.username }}</span>
              <span class="user-username">@{{ user.username }}</span>
            </div>
          </div>
          <div class="table-cell">
            <div class="contact-info">
              <span class="user-email">{{ user.email }}</span>
              <span class="user-phone" v-if="user.phone">{{ user.phone }}</span>
            </div>
          </div>
          <div class="table-cell">
            <div class="dept-info">
              <span class="user-department" v-if="user.department">{{ user.department }}</span>
              <span class="user-position" v-if="user.position">{{ user.position }}</span>
            </div>
          </div>
          <div class="table-cell">
            <div class="role-status">
              <span class="role-badge" :class="user.role.toLowerCase()">
                {{ getRoleText(user.role) }}
              </span>
              <span class="status-badge" :class="user.status.toLowerCase()">
                {{ getStatusText(user.status) }}
              </span>
            </div>
          </div>
          <div class="table-cell">
            <span class="user-date">{{ formatDate(user.createdAt) }}</span>
          </div>
          <div class="table-cell">
            <div class="action-buttons">
              <button class="action-btn edit" @click="editUser(user)" title="编辑">
                ✏️
              </button>
              <button class="action-btn status" @click="toggleUserStatus(user)" title="切换状态">
                {{ user.status === 'ACTIVE' ? '🔒' : '🔓' }}
              </button>
              <button class="action-btn password" @click="resetPassword(user)" title="重置密码">
                🔑
              </button>
              <button class="action-btn delete" @click="deleteUser(user)" title="删除">
                🗑️
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 分页控件 -->
    <div class="pagination" v-if="totalPages > 1">
      <button 
        class="page-btn" 
        :disabled="currentPage === 0"
        @click="changePage(currentPage - 1)"
      >
        上一页
      </button>
      <span class="page-info">
        第 {{ currentPage + 1 }} 页，共 {{ totalPages }} 页
      </span>
      <button 
        class="page-btn" 
        :disabled="currentPage >= totalPages - 1"
        @click="changePage(currentPage + 1)"
      >
        下一页
      </button>
    </div>

    <!-- 加载状态 -->
    <div v-if="loading" class="loading">
      <div class="loading-spinner"></div>
      <p>加载中...</p>
    </div>

    <!-- 添加/编辑用户模态框 -->
    <div v-if="showAddModal || showEditModal" class="modal-overlay" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑用户' : '添加新用户' }}</h3>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label>用户名 *</label>
              <input 
                v-model="currentUser.username" 
                type="text" 
                class="form-input"
                :disabled="showEditModal"
                placeholder="请输入用户名"
              />
            </div>
            <div class="form-group">
              <label>姓名 *</label>
              <input 
                v-model="currentUser.fullName" 
                type="text" 
                class="form-input"
                placeholder="请输入真实姓名"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>邮箱 *</label>
              <input 
                v-model="currentUser.email" 
                type="email" 
                class="form-input"
                placeholder="请输入邮箱地址"
              />
            </div>
            <div class="form-group">
              <label>手机号</label>
              <input 
                v-model="currentUser.phone" 
                type="tel" 
                class="form-input"
                placeholder="请输入手机号"
              />
            </div>
          </div>
          <div class="form-row" v-if="!showEditModal">
            <div class="form-group">
              <label>密码 *</label>
              <input 
                v-model="currentUser.password" 
                type="password" 
                class="form-input"
                placeholder="请输入密码（至少6位）"
              />
            </div>
            <div class="form-group">
              <label>确认密码 *</label>
              <input 
                v-model="confirmPassword" 
                type="password" 
                class="form-input"
                placeholder="请再次输入密码"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>部门</label>
              <input 
                v-model="currentUser.department" 
                type="text" 
                class="form-input"
                placeholder="请输入部门"
              />
            </div>
            <div class="form-group">
              <label>职位</label>
              <input 
                v-model="currentUser.position" 
                type="text" 
                class="form-input"
                placeholder="请输入职位"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>角色</label>
              <select v-model="currentUser.role" class="form-input">
                <option value="USER">普通用户</option>
                <option value="MANAGER">经理</option>
                <option value="ADMIN">管理员</option>
              </select>
            </div>
            <div class="form-group">
              <label>状态</label>
              <select v-model="currentUser.status" class="form-input">
                <option value="ACTIVE">激活</option>
                <option value="INACTIVE">禁用</option>
                <option value="PENDING">待审核</option>
              </select>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button class="btn primary" @click="saveUser" :disabled="saving">
            {{ saving ? '保存中...' : (showEditModal ? '更新' : '添加') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 重置密码模态框 -->
    <div v-if="showPasswordModal" class="modal-overlay" @click="showPasswordModal = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>重置密码</h3>
          <button class="close-btn" @click="showPasswordModal = false">✕</button>
        </div>
        <div class="modal-body">
          <p>为用户 <strong>{{ selectedUser?.fullName || selectedUser?.username }}</strong> 重置密码</p>
          <div class="form-group">
            <label>新密码</label>
            <input 
              v-model="newPassword" 
              type="password" 
              class="form-input"
              placeholder="请输入新密码（至少6位）"
            />
          </div>
          <div class="form-group">
            <label>确认密码</label>
            <input 
              v-model="confirmNewPassword" 
              type="password" 
              class="form-input"
              placeholder="请再次输入新密码"
            />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="showPasswordModal = false">取消</button>
          <button class="btn primary" @click="confirmResetPassword" :disabled="saving">
            {{ saving ? '重置中...' : '重置密码' }}
          </button>
        </div>
              </div>
      </div>

    <!-- 导入结果模态框 -->
    <div v-if="showImportResultModal" class="modal-overlay" @click="showImportResultModal = false">
      <div class="modal large" @click.stop>
        <div class="modal-header">
          <h3>导入结果</h3>
          <button class="close-btn" @click="showImportResultModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="import-summary">
            <div class="summary-item">
              <span class="label">总行数：</span>
              <span class="value">{{ importResult.totalRows }}</span>
            </div>
            <div class="summary-item">
              <span class="label">成功导入：</span>
              <span class="value success">{{ importResult.successCount }}</span>
            </div>
            <div class="summary-item">
              <span class="label">失败行数：</span>
              <span class="value error">{{ importResult.errorCount }}</span>
            </div>
          </div>
          
          <div v-if="importResult.errors && importResult.errors.length > 0" class="error-list">
            <h4>错误详情：</h4>
            <div class="error-items">
              <div v-for="(error, index) in importResult.errors" :key="index" class="error-item">
                {{ error }}
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn primary" @click="showImportResultModal = false">确定</button>
        </div>
      </div>
    </div>
    </div>
  </template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'Users',
  setup() {
    const users = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const roleFilter = ref('')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const showPasswordModal = ref(false)
    const showImportResultModal = ref(false)
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalUsers = ref(0)
    const totalPages = ref(0)
    
    // 表单数据
    const currentUser = ref({
      username: '',
      fullName: '',
      email: '',
      phone: '',
      department: '',
      position: '',
      role: 'USER',
      status: 'ACTIVE',
      password: ''
    })
    const confirmPassword = ref('')
    const selectedUser = ref(null)
    const newPassword = ref('')
    const confirmNewPassword = ref('')
    const fileInput = ref(null)
    const importResult = ref({
      totalRows: 0,
      successCount: 0,
      errorCount: 0,
      errors: []
    })
    
    // 搜索防抖
    let searchTimeout = null

    const activeUsers = computed(() => {
      return users.value.filter(user => user.status === 'ACTIVE').length
    })

    const getAvatarColor = (username) => {
      const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#43e97b', '#38f9d7']
      const index = username.charCodeAt(0) % colors.length
      return colors[index]
    }

    const getRoleText = (role) => {
      const roleMap = {
        ADMIN: '管理员',
        MANAGER: '经理',
        USER: '普通用户'
      }
      return roleMap[role] || role
    }

    const getStatusText = (status) => {
      const statusMap = {
        ACTIVE: '激活',
        INACTIVE: '禁用',
        PENDING: '待审核'
      }
      return statusMap[status] || status
    }

    const formatDate = (dateString) => {
      if (!dateString) return '-'
      const date = new Date(dateString)
      return date.toLocaleDateString('zh-CN', {
        year: 'numeric',
        month: '2-digit',
        day: '2-digit'
      })
    }

    const loadUsers = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          sortBy: 'id',
          sortDir: 'desc'
        }
        
        if (searchQuery.value.trim()) {
          params.search = searchQuery.value.trim()
        }
        
        if (statusFilter.value) {
          params.status = statusFilter.value
        }
        
        if (roleFilter.value) {
          params.role = roleFilter.value
        }
        
        const response = await api.get('/users', { params })
        
        if (response.success) {
          const data = response.data
          users.value = data.users || []
          totalUsers.value = data.totalItems || 0
          totalPages.value = data.totalPages || 0
          currentPage.value = data.currentPage || 0
        } else {
          console.error('API返回错误:', response.message)
          alert('获取用户列表失败: ' + response.message)
        }
      } catch (error) {
        console.error('加载用户列表失败:', error)
        if (error.response?.status === 401) {
          alert('登录已过期，请重新登录')
          localStorage.removeItem('isLoggedIn')
          localStorage.removeItem('userName')
          localStorage.removeItem('username')
          window.location.href = '/login'
        } else if (error.response?.status === 403) {
          console.log('没有权限访问，跳转到登录页')
          localStorage.removeItem('isLoggedIn')
          localStorage.removeItem('userName')
          localStorage.removeItem('username')
          window.location.href = '/login'
        } else {
          alert('加载用户列表失败: ' + (error.response?.data?.message || error.message))
        }
      } finally {
        loading.value = false
      }
    }

    const searchUsers = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 0
        loadUsers()
      }, 500)
    }

    const changePage = (page) => {
      console.log(`切换到第${page + 1}页`)
      currentPage.value = page
      loadUsers()
    }

    const resetForm = () => {
      currentUser.value = {
        username: '',
        fullName: '',
        email: '',
        phone: '',
        department: '',
        position: '',
        role: 'USER',
        status: 'ACTIVE',
        password: ''
      }
      confirmPassword.value = ''
    }

    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      resetForm()
    }

    const validateForm = () => {
      if (!currentUser.value.username.trim()) {
        alert('请输入用户名')
        return false
      }
      if (!currentUser.value.fullName.trim()) {
        alert('请输入姓名')
        return false
      }
      if (!currentUser.value.email.trim()) {
        alert('请输入邮箱')
        return false
      }
      if (!showEditModal.value) {
        if (!currentUser.value.password.trim()) {
          alert('请输入密码')
          return false
        }
        if (currentUser.value.password.length < 6) {
          alert('密码长度至少6位')
          return false
        }
        if (currentUser.value.password !== confirmPassword.value) {
          alert('两次输入的密码不一致')
          return false
        }
      }
      return true
    }

    const saveUser = async () => {
      if (!validateForm()) return
      
      saving.value = true
      try {
        if (showEditModal.value) {
          // 更新用户
          const response = await api.put(`/users/${currentUser.value.id}`, currentUser.value)
          if (response.success) {
            alert('用户更新成功')
            closeModal()
            loadUsers()
          }
        } else {
          // 创建用户
          const response = await api.post('/users', currentUser.value)
          if (response.success) {
            alert('用户创建成功')
            closeModal()
            loadUsers()
          }
        }
      } catch (error) {
        console.error('保存用户失败:', error)
        alert('保存用户失败: ' + (error.response?.data?.message || error.message))
      } finally {
        saving.value = false
      }
    }

    const editUser = (user) => {
      currentUser.value = { ...user }
      showEditModal.value = true
    }

    const deleteUser = async (user) => {
      if (!confirm(`确定要删除用户 ${user.fullName || user.username} 吗？此操作不可恢复！`)) {
        return
      }
      
      try {
        const response = await api.delete(`/users/${user.id}`)
        if (response.success) {
          alert('用户删除成功')
          loadUsers()
        }
      } catch (error) {
        console.error('删除用户失败:', error)
        alert('删除用户失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const toggleUserStatus = async (user) => {
      const newStatus = user.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const action = newStatus === 'ACTIVE' ? '激活' : '禁用'
      
      if (!confirm(`确定要${action}用户 ${user.fullName || user.username} 吗？`)) {
        return
      }
      
      try {
        const response = await api.patch(`/users/${user.id}/status`, { status: newStatus })
        if (response.success) {
          alert(`用户${action}成功`)
          loadUsers()
        }
      } catch (error) {
        console.error('更新用户状态失败:', error)
        alert('更新用户状态失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const resetPassword = (user) => {
      selectedUser.value = user
      newPassword.value = ''
      confirmNewPassword.value = ''
      showPasswordModal.value = true
    }

    const confirmResetPassword = async () => {
      if (!newPassword.value.trim()) {
        alert('请输入新密码')
        return
      }
      if (newPassword.value.length < 6) {
        alert('密码长度至少6位')
        return
      }
      if (newPassword.value !== confirmNewPassword.value) {
        alert('两次输入的密码不一致')
        return
      }
      
      saving.value = true
      try {
        const response = await api.patch(`/users/${selectedUser.value.id}/password`, {
          password: newPassword.value
        })
        if (response.success) {
          alert('密码重置成功')
          showPasswordModal.value = false
        }
      } catch (error) {
        console.error('重置密码失败:', error)
        alert('重置密码失败: ' + (error.response?.data?.message || error.message))
      } finally {
        saving.value = false
      }
    }



    // Excel导入导出功能
    const exportUsers = async () => {
      try {
        const response = await fetch('/api/users/export', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        })
        
        if (response.ok) {
          const blob = await response.blob()
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = `用户数据_${new Date().toISOString().slice(0, 10)}.xlsx`
          document.body.appendChild(a)
          a.click()
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
          alert('用户数据导出成功')
        } else {
          alert('导出失败')
        }
      } catch (error) {
        console.error('导出失败:', error)
        alert('导出失败: ' + error.message)
      }
    }

    const downloadTemplate = async () => {
      try {
        const response = await fetch('/api/users/import/template', {
          method: 'GET',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          }
        })
        
        if (response.ok) {
          const blob = await response.blob()
          const url = window.URL.createObjectURL(blob)
          const a = document.createElement('a')
          a.href = url
          a.download = '用户导入模板.xlsx'
          document.body.appendChild(a)
          a.click()
          window.URL.revokeObjectURL(url)
          document.body.removeChild(a)
          alert('模板下载成功')
        } else {
          alert('模板下载失败')
        }
      } catch (error) {
        console.error('模板下载失败:', error)
        alert('模板下载失败: ' + error.message)
      }
    }

    const triggerImport = () => {
      fileInput.value.click()
    }

    const handleFileImport = async (event) => {
      const file = event.target.files[0]
      if (!file) return

      const formData = new FormData()
      formData.append('file', file)

      try {
        const response = await fetch('/api/users/import', {
          method: 'POST',
          headers: {
            'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
          },
          body: formData
        })

        const result = await response.json()
        
        if (result.success || result.data) {
          importResult.value = result.data || {
            totalRows: 0,
            successCount: 0,
            errorCount: 0,
            errors: []
          }
          showImportResultModal.value = true
          
          // 如果有成功导入的数据，刷新用户列表
          if (importResult.value.successCount > 0) {
            loadUsers()
          }
        } else {
          alert('导入失败: ' + result.message)
        }
      } catch (error) {
        console.error('导入失败:', error)
        alert('导入失败: ' + error.message)
      } finally {
        // 清空文件输入
        event.target.value = ''
      }
    }

    onMounted(() => {
      loadUsers()
    })

    return {
      users,
      loading,
      saving,
      searchQuery,
      statusFilter,
      roleFilter,
      showAddModal,
      showEditModal,
      showPasswordModal,
      showImportResultModal,
      currentPage,
      totalUsers,
      totalPages,
      activeUsers,
      currentUser,
      confirmPassword,
      selectedUser,
      newPassword,
      confirmNewPassword,
      fileInput,
      importResult,
      getAvatarColor,
      getRoleText,
      getStatusText,
      formatDate,
      loadUsers,
      searchUsers,
      changePage,
      closeModal,
      saveUser,
      editUser,
      deleteUser,
      toggleUserStatus,
      resetPassword,
      confirmResetPassword,
      exportUsers,
      downloadTemplate,
      triggerImport,
      handleFileImport
    }
  }
}
</script>

<style scoped>
.users {
  padding: 2rem 0;
}

.users-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 0.75rem;
  align-items: center;
}

.action-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.6rem 1rem;
  border: none;
  border-radius: 6px;
  font-weight: 500;
  cursor: pointer;
  transition: all 0.3s ease;
  font-size: 0.9rem;
}

.action-btn.export {
  background: #27ae60;
  color: white;
}

.action-btn.export:hover {
  background: #229954;
  transform: translateY(-1px);
}

.action-btn.template {
  background: #3498db;
  color: white;
}

.action-btn.template:hover {
  background: #2980b9;
  transform: translateY(-1px);
}

.action-btn.import {
  background: #e67e22;
  color: white;
}

.action-btn.import:hover {
  background: #d35400;
  transform: translateY(-1px);
}

.add-user-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 1.5rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.add-user-btn:hover {
  transform: translateY(-2px);
}

.users-filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 3rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.search-input:focus {
  outline: none;
  border-color: #667eea;
}

.search-icon {
  position: absolute;
  left: 1rem;
  top: 50%;
  transform: translateY(-50%);
  color: #7f8c8d;
}

.status-filter,
.role-filter {
  padding: 0.75rem 1rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
  min-width: 120px;
}

.users-stats {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(200px, 1fr));
  gap: 1rem;
  margin-bottom: 2rem;
}

.stat-card {
  background: white;
  padding: 1.5rem;
  border-radius: 12px;
  box-shadow: 0 2px 10px rgba(0,0,0,0.1);
  text-align: center;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #667eea;
  margin-bottom: 0.5rem;
}

.stat-label {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.users-table {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  overflow: hidden;
  margin-bottom: 2rem;
}

.table-header {
  display: grid;
  grid-template-columns: 80px 1fr 1fr 1fr 1fr 120px 150px;
  background: linear-gradient(135deg, #f8f9fa 0%, #e9ecef 100%);
  padding: 1rem;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 2px solid #e1e8ed;
}

.table-body {
  max-height: 600px;
  overflow-y: auto;
}

.table-row {
  display: grid;
  grid-template-columns: 80px 1fr 1fr 1fr 1fr 120px 150px;
  padding: 1rem;
  border-bottom: 1px solid #f0f0f0;
  transition: background-color 0.3s ease;
}

.table-row:hover {
  background: #f8f9fa;
}

.table-cell {
  display: flex;
  align-items: center;
  padding: 0 0.5rem;
}

.user-avatar {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 1.1rem;
}

.user-info {
  display: flex;
  flex-direction: column;
}

.user-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 2px;
}

.user-username {
  color: #7f8c8d;
  font-size: 0.85rem;
}

.contact-info {
  display: flex;
  flex-direction: column;
}

.user-email {
  color: #2c3e50;
  margin-bottom: 2px;
}

.user-phone {
  color: #7f8c8d;
  font-size: 0.85rem;
}

.dept-info {
  display: flex;
  flex-direction: column;
}

.user-department {
  color: #2c3e50;
  margin-bottom: 2px;
}

.user-position {
  color: #7f8c8d;
  font-size: 0.85rem;
}

.role-status {
  display: flex;
  flex-direction: column;
  gap: 4px;
}

.role-badge,
.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.75rem;
  font-weight: 600;
  text-align: center;
}

.role-badge.admin {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.role-badge.manager {
  background: rgba(241, 196, 15, 0.1);
  color: #f39c12;
}

.role-badge.user {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.status-badge.active {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.status-badge.inactive {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.status-badge.pending {
  background: rgba(241, 196, 15, 0.1);
  color: #f1c40f;
}

.user-date {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 0.25rem;
}

.action-btn {
  width: 28px;
  height: 28px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
  font-size: 0.8rem;
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-btn.edit {
  background: rgba(52, 152, 219, 0.1);
}

.action-btn.status {
  background: rgba(241, 196, 15, 0.1);
}

.action-btn.password {
  background: rgba(155, 89, 182, 0.1);
}

.action-btn.delete {
  background: rgba(231, 76, 60, 0.1);
}

.pagination {
  display: flex;
  justify-content: center;
  align-items: center;
  gap: 1rem;
  margin: 2rem 0;
}

.page-btn {
  padding: 0.5rem 1rem;
  border: 2px solid #667eea;
  background: white;
  color: #667eea;
  border-radius: 6px;
  cursor: pointer;
  transition: all 0.3s ease;
}

.page-btn:hover:not(:disabled) {
  background: #667eea;
  color: white;
}

.page-btn:disabled {
  opacity: 0.5;
  cursor: not-allowed;
}

.page-info {
  color: #7f8c8d;
  font-weight: 500;
}

.loading {
  display: flex;
  flex-direction: column;
  align-items: center;
  justify-content: center;
  padding: 4rem;
  color: #7f8c8d;
}

.loading-spinner {
  width: 40px;
  height: 40px;
  border: 4px solid #f3f3f3;
  border-top: 4px solid #667eea;
  border-radius: 50%;
  animation: spin 1s linear infinite;
  margin-bottom: 1rem;
}

@keyframes spin {
  0% { transform: rotate(0deg); }
  100% { transform: rotate(360deg); }
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  width: 100%;
  height: 100%;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 15px;
  width: 90%;
  max-width: 700px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 10px 30px rgba(0,0,0,0.3);
}

.modal-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1.5rem;
  border-bottom: 1px solid #e1e8ed;
}

.modal-header h3 {
  margin: 0;
  color: #2c3e50;
}

.close-btn {
  background: none;
  border: none;
  font-size: 1.5rem;
  cursor: pointer;
  color: #7f8c8d;
}

.modal-body {
  padding: 1.5rem;
}

.form-row {
  display: grid;
  grid-template-columns: 1fr 1fr;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  margin-bottom: 1rem;
}

.form-group label {
  display: block;
  margin-bottom: 0.5rem;
  font-weight: 500;
  color: #2c3e50;
}

.form-input {
  width: 100%;
  padding: 0.75rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
  box-sizing: border-box;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-input:disabled {
  background: #f8f9fa;
  color: #6c757d;
}

.modal-footer {
  display: flex;
  justify-content: flex-end;
  gap: 1rem;
  padding: 1.5rem;
  border-top: 1px solid #e1e8ed;
}

.btn {
  padding: 0.75rem 1.5rem;
  border: none;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.btn:hover:not(:disabled) {
  transform: translateY(-2px);
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
  transform: none;
}

.btn.primary {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.btn.secondary {
  background: #e1e8ed;
  color: #2c3e50;
}

/* 响应式设计 */
@media (max-width: 1200px) {
  .table-header,
  .table-row {
    grid-template-columns: 60px 1fr 1fr 100px 120px;
    font-size: 0.9rem;
  }
  
  .table-header .table-cell:nth-child(4),
  .table-row .table-cell:nth-child(4),
  .table-header .table-cell:nth-child(6),
  .table-row .table-cell:nth-child(6) {
    display: none;
  }
}

@media (max-width: 768px) {
  .users-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .users-filters {
    flex-direction: column;
  }
  
  .users-stats {
    grid-template-columns: repeat(2, 1fr);
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 60px 1fr 80px;
    font-size: 0.85rem;
  }
  
  .table-header .table-cell:nth-child(3),
  .table-row .table-cell:nth-child(3),
  .table-header .table-cell:nth-child(4),
  .table-row .table-cell:nth-child(4),
  .table-header .table-cell:nth-child(5),
  .table-row .table-cell:nth-child(5),
  .table-header .table-cell:nth-child(6),
  .table-row .table-cell:nth-child(6) {
    display: none;
  }
  
  .form-row {
    grid-template-columns: 1fr;
  }
  
  .page-title {
    font-size: 2rem;
  }
}

/* 导入结果模态框样式 */
.modal.large {
  max-width: 800px;
}

.import-summary {
  display: grid;
  grid-template-columns: repeat(3, 1fr);
  gap: 1rem;
  margin-bottom: 1.5rem;
}

.summary-item {
  background: #f8f9fa;
  padding: 1rem;
  border-radius: 8px;
  text-align: center;
}

.summary-item .label {
  display: block;
  font-size: 0.9rem;
  color: #6c757d;
  margin-bottom: 0.5rem;
}

.summary-item .value {
  display: block;
  font-size: 1.5rem;
  font-weight: 700;
}

.summary-item .value.success {
  color: #27ae60;
}

.summary-item .value.error {
  color: #e74c3c;
}

.error-list {
  margin-top: 1.5rem;
}

.error-list h4 {
  color: #e74c3c;
  margin-bottom: 1rem;
}

.error-items {
  max-height: 300px;
  overflow-y: auto;
  background: #fff5f5;
  border: 1px solid #fed7d7;
  border-radius: 8px;
  padding: 1rem;
}

.error-item {
  padding: 0.5rem 0;
  border-bottom: 1px solid #fed7d7;
  color: #c53030;
  font-size: 0.9rem;
}

.error-item:last-child {
  border-bottom: none;
}
</style> 