<template>
  <div class="permissions">
    <div class="permissions-header">
      <h1 class="page-title">🔑 权限管理</h1>
      <div class="header-actions">
        <button class="add-permission-btn" @click="showAddModal = true">
          <i class="icon">➕</i>
          添加权限
        </button>
      </div>
    </div>

    <div class="permissions-filters">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          @input="searchPermissions"
          type="text" 
          placeholder="搜索权限名称、代码或模块..." 
          class="search-input"
        />
        <i class="search-icon">🔍</i>
      </div>
      <select v-model="statusFilter" @change="loadPermissions" class="status-filter">
        <option value="">所有状态</option>
        <option value="ACTIVE">启用</option>
        <option value="INACTIVE">禁用</option>
      </select>
      <select v-model="moduleFilter" @change="loadPermissions" class="module-filter">
        <option value="">所有模块</option>
        <option v-for="module in modules" :key="module" :value="module">{{ module }}</option>
      </select>
      <select v-model="typeFilter" @change="loadPermissions" class="type-filter">
        <option value="">所有类型</option>
        <option value="MENU">菜单</option>
        <option value="BUTTON">按钮</option>
        <option value="API">接口</option>
      </select>
    </div>

    <!-- 权限列表 -->
    <div class="permissions-list" v-if="!loading && permissions.length > 0">
      <div class="table-header">
        <div class="table-cell">权限信息</div>
        <div class="table-cell">模块</div>
        <div class="table-cell">类型</div>
        <div class="table-cell">状态</div>
        <div class="table-cell">创建时间</div>
        <div class="table-cell">操作</div>
      </div>
      
      <div class="table-row" v-for="permission in permissions" :key="permission.id">
        <div class="table-cell">
          <div class="permission-info">
            <div class="permission-icon" :class="permission.type.toLowerCase()">
              {{ getTypeIcon(permission.type) }}
            </div>
            <div class="permission-details">
              <div class="permission-name">{{ permission.name }}</div>
              <div class="permission-code">{{ permission.code }}</div>
              <div class="permission-description" v-if="permission.description">{{ permission.description }}</div>
            </div>
          </div>
        </div>
        <div class="table-cell">
          <span class="module-badge">{{ permission.module }}</span>
        </div>
        <div class="table-cell">
          <span class="type-badge" :class="permission.type.toLowerCase()">
            {{ getTypeText(permission.type) }}
          </span>
        </div>
        <div class="table-cell">
          <span class="status-badge" :class="permission.status.toLowerCase()">
            {{ getStatusText(permission.status) }}
          </span>
        </div>
        <div class="table-cell">
          <div class="permission-date">{{ formatDate(permission.createdAt) }}</div>
        </div>
        <div class="table-cell">
          <div class="action-buttons">
            <button class="action-btn edit" @click="editPermission(permission)" title="编辑">
              ✏️
            </button>
            <button class="action-btn status" @click="togglePermissionStatus(permission)" title="切换状态">
              {{ permission.status === 'ACTIVE' ? '🔒' : '🔓' }}
            </button>
            <button class="action-btn delete" @click="deletePermission(permission)" title="删除">
              🗑️
            </button>
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

    <!-- 空状态 -->
    <div v-if="!loading && permissions.length === 0" class="empty-state">
      <div class="empty-icon">🔑</div>
      <h3>暂无权限数据</h3>
      <p>点击上方"添加权限"按钮创建第一个权限</p>
    </div>

    <!-- 添加/编辑权限模态框 -->
    <div class="modal-overlay" v-if="showAddModal || showEditModal" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑权限' : '添加权限' }}</h3>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label>权限名称 *</label>
              <input 
                v-model="currentPermission.name" 
                type="text" 
                class="form-input"
                placeholder="请输入权限名称"
              />
            </div>
            <div class="form-group">
              <label>权限代码 *</label>
              <input 
                v-model="currentPermission.code" 
                type="text" 
                class="form-input"
                placeholder="请输入权限代码（如：user:view）"
                :disabled="showEditModal"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>所属模块 *</label>
              <input 
                v-model="currentPermission.module" 
                type="text" 
                class="form-input"
                placeholder="请输入模块名称"
                list="modules-list"
              />
              <datalist id="modules-list">
                <option v-for="module in modules" :key="module" :value="module"></option>
              </datalist>
            </div>
            <div class="form-group">
              <label>权限类型</label>
              <select v-model="currentPermission.type" class="form-input">
                <option value="MENU">菜单</option>
                <option value="BUTTON">按钮</option>
                <option value="API">接口</option>
              </select>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group full-width">
              <label>描述</label>
              <textarea 
                v-model="currentPermission.description" 
                class="form-input"
                placeholder="请输入权限描述"
                rows="3"
              ></textarea>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>状态</label>
              <select v-model="currentPermission.status" class="form-input">
                <option value="ACTIVE">启用</option>
                <option value="INACTIVE">禁用</option>
              </select>
            </div>
            <div class="form-group">
              <label>排序</label>
              <input 
                v-model.number="currentPermission.sortOrder" 
                type="number" 
                class="form-input"
                placeholder="排序值（数字越小越靠前）"
              />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button class="btn primary" @click="savePermission" :disabled="saving">
            {{ saving ? '保存中...' : (showEditModal ? '更新' : '添加') }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'Permissions',
  setup() {
    const permissions = ref([])
    const modules = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const moduleFilter = ref('')
    const typeFilter = ref('')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalPermissions = ref(0)
    const totalPages = ref(0)
    
    // 表单数据
    const currentPermission = ref({
      name: '',
      code: '',
      description: '',
      module: '',
      type: 'MENU',
      status: 'ACTIVE',
      sortOrder: 0
    })
    
    // 搜索防抖
    let searchTimeout = null

    const getTypeIcon = (type) => {
      const iconMap = {
        MENU: '📋',
        BUTTON: '🔘',
        API: '🔌'
      }
      return iconMap[type] || '📋'
    }

    const getTypeText = (type) => {
      const typeMap = {
        MENU: '菜单',
        BUTTON: '按钮',
        API: '接口'
      }
      return typeMap[type] || type
    }

    const getStatusText = (status) => {
      const statusMap = {
        ACTIVE: '启用',
        INACTIVE: '禁用'
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

    const loadPermissions = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          sortBy: 'module',
          sortDir: 'asc'
        }
        
        if (searchQuery.value.trim()) {
          params.search = searchQuery.value.trim()
        }
        
        if (statusFilter.value) {
          params.status = statusFilter.value
        }
        
        if (moduleFilter.value) {
          params.module = moduleFilter.value
        }
        
        if (typeFilter.value) {
          params.type = typeFilter.value
        }
        
        const response = await api.get('/permissions', { params })
        
        if (response.success) {
          const data = response.data
          permissions.value = data.permissions || []
          totalPermissions.value = data.totalItems || 0
          totalPages.value = data.totalPages || 0
          currentPage.value = data.currentPage || 0
        } else {
          console.error('API返回错误:', response.message)
          alert('获取权限列表失败: ' + response.message)
        }
      } catch (error) {
        console.error('加载权限列表失败:', error)
        alert('加载权限列表失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
    }

    const loadModules = async () => {
      try {
        const response = await api.get('/permissions/modules')
        if (response.success) {
          modules.value = response.data || []
        }
      } catch (error) {
        console.error('加载模块列表失败:', error)
      }
    }

    const searchPermissions = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 0
        loadPermissions()
      }, 500)
    }

    const changePage = (page) => {
      currentPage.value = page
      loadPermissions()
    }

    const resetForm = () => {
      currentPermission.value = {
        name: '',
        code: '',
        description: '',
        module: '',
        type: 'MENU',
        status: 'ACTIVE',
        sortOrder: 0
      }
    }

    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      resetForm()
    }

    const editPermission = (permission) => {
      currentPermission.value = { ...permission }
      showEditModal.value = true
    }

    const savePermission = async () => {
      if (!currentPermission.value.name || !currentPermission.value.code || !currentPermission.value.module) {
        alert('请填写权限名称、代码和模块')
        return
      }
      
      saving.value = true
      try {
        let response
        if (showEditModal.value) {
          response = await api.put(`/permissions/${currentPermission.value.id}`, currentPermission.value)
        } else {
          response = await api.post('/permissions', currentPermission.value)
        }
        
        if (response.success) {
          alert(showEditModal.value ? '权限更新成功' : '权限创建成功')
          closeModal()
          loadPermissions()
          loadModules() // 重新加载模块列表
        }
      } catch (error) {
        console.error('保存权限失败:', error)
        alert('保存权限失败: ' + (error.response?.data?.message || error.message))
      } finally {
        saving.value = false
      }
    }

    const deletePermission = async (permission) => {
      if (!confirm(`确定要删除权限 ${permission.name} 吗？此操作不可恢复！`)) {
        return
      }
      
      try {
        const response = await api.delete(`/permissions/${permission.id}`)
        if (response.success) {
          alert('权限删除成功')
          loadPermissions()
        }
      } catch (error) {
        console.error('删除权限失败:', error)
        alert('删除权限失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const togglePermissionStatus = async (permission) => {
      const newStatus = permission.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
      
      if (!confirm(`确定要${action}权限 ${permission.name} 吗？`)) {
        return
      }
      
      try {
        const response = await api.patch(`/permissions/${permission.id}/status`, { status: newStatus })
        if (response.success) {
          alert(`权限${action}成功`)
          loadPermissions()
        }
      } catch (error) {
        console.error('更新权限状态失败:', error)
        alert('更新权限状态失败: ' + (error.response?.data?.message || error.message))
      }
    }

    onMounted(() => {
      loadPermissions()
      loadModules()
    })

    return {
      permissions,
      modules,
      loading,
      saving,
      searchQuery,
      statusFilter,
      moduleFilter,
      typeFilter,
      showAddModal,
      showEditModal,
      currentPage,
      totalPages,
      currentPermission,
      getTypeIcon,
      getTypeText,
      getStatusText,
      formatDate,
      loadPermissions,
      searchPermissions,
      changePage,
      closeModal,
      editPermission,
      savePermission,
      deletePermission,
      togglePermissionStatus
    }
  }
}
</script>

<style scoped>
.permissions {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.permissions-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  margin-bottom: 2rem;
}

.page-title {
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  margin: 0;
}

.header-actions {
  display: flex;
  gap: 1rem;
}

.add-permission-btn {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  padding: 0.75rem 1.5rem;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  border-radius: 8px;
  cursor: pointer;
  font-weight: 500;
  transition: transform 0.3s ease;
}

.add-permission-btn:hover {
  transform: translateY(-2px);
}

.permissions-filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
  flex-wrap: wrap;
}

.search-box {
  position: relative;
  flex: 1;
  max-width: 400px;
}

.search-input {
  width: 100%;
  padding: 0.75rem 1rem 0.75rem 2.5rem;
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
  left: 0.75rem;
  top: 50%;
  transform: translateY(-50%);
  color: #7f8c8d;
}

.status-filter,
.module-filter,
.type-filter {
  padding: 0.75rem 1rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
  min-width: 120px;
}

.permissions-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1.5fr;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #e1e8ed;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1fr 1.5fr;
  gap: 1rem;
  padding: 1rem;
  border-bottom: 1px solid #f1f3f4;
  transition: background-color 0.3s ease;
}

.table-row:hover {
  background-color: #f8f9fa;
}

.table-cell {
  display: flex;
  align-items: center;
}

.permission-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.permission-icon {
  width: 40px;
  height: 40px;
  border-radius: 8px;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.permission-icon.menu {
  background: rgba(52, 152, 219, 0.1);
}

.permission-icon.button {
  background: rgba(39, 174, 96, 0.1);
}

.permission-icon.api {
  background: rgba(155, 89, 182, 0.1);
}

.permission-details {
  flex: 1;
}

.permission-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.25rem;
}

.permission-code {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-family: monospace;
  margin-bottom: 0.25rem;
}

.permission-description {
  font-size: 0.85rem;
  color: #95a5a6;
}

.module-badge {
  background: rgba(241, 196, 15, 0.1);
  color: #f39c12;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.type-badge.menu {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.type-badge.button {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.type-badge.api {
  background: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.85rem;
  font-weight: 500;
}

.status-badge.active {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.status-badge.inactive {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.permission-date {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 0.25rem;
}

.action-btn {
  width: 32px;
  height: 32px;
  border: none;
  border-radius: 6px;
  cursor: pointer;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: transform 0.3s ease;
  font-size: 0.9rem;
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

.empty-state {
  text-align: center;
  padding: 4rem;
  color: #7f8c8d;
}

.empty-icon {
  font-size: 4rem;
  margin-bottom: 1rem;
}

/* 模态框样式 */
.modal-overlay {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background: rgba(0, 0, 0, 0.5);
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1000;
}

.modal {
  background: white;
  border-radius: 12px;
  width: 90%;
  max-width: 600px;
  max-height: 90vh;
  overflow-y: auto;
  box-shadow: 0 20px 60px rgba(0, 0, 0, 0.3);
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
  padding: 0.25rem;
}

.modal-body {
  padding: 1.5rem;
}

.form-row {
  display: flex;
  gap: 1rem;
  margin-bottom: 1rem;
}

.form-group {
  flex: 1;
}

.form-group.full-width {
  flex: none;
  width: 100%;
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
  border-radius: 6px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.form-input:focus {
  outline: none;
  border-color: #667eea;
}

.form-input:disabled {
  background-color: #f8f9fa;
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
  border-radius: 6px;
  cursor: pointer;
  font-weight: 500;
  transition: all 0.3s ease;
}

.btn.primary {
  background: #667eea;
  color: white;
}

.btn.primary:hover:not(:disabled) {
  background: #5a6fd8;
}

.btn.secondary {
  background: #e1e8ed;
  color: #2c3e50;
}

.btn.secondary:hover {
  background: #d1d9e0;
}

.btn:disabled {
  opacity: 0.6;
  cursor: not-allowed;
}
</style> 