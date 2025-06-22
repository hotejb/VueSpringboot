<template>
  <div class="roles">
    <div class="roles-header">
      <h1 class="page-title">🛡️ 角色管理</h1>
      <div class="header-actions">
        <button class="add-role-btn" @click="showAddModal = true">
          <i class="icon">➕</i>
          添加角色
        </button>
      </div>
    </div>

    <div class="roles-filters">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          @input="searchRoles"
          type="text" 
          placeholder="搜索角色名称或代码..." 
          class="search-input"
        />
        <i class="search-icon">🔍</i>
      </div>
      <select v-model="statusFilter" @change="loadRoles" class="status-filter">
        <option value="">所有状态</option>
        <option value="ACTIVE">启用</option>
        <option value="INACTIVE">禁用</option>
      </select>
    </div>

    <!-- 角色列表 -->
    <div class="roles-list" v-if="!loading && roles.length > 0">
      <div class="table-header">
        <div class="table-cell">角色信息</div>
        <div class="table-cell">状态</div>
        <div class="table-cell">权限数量</div>
        <div class="table-cell">创建时间</div>
        <div class="table-cell">操作</div>
      </div>
      
      <div class="table-row" v-for="role in roles" :key="role.id">
        <div class="table-cell">
          <div class="role-info">
            <div class="role-avatar" :style="{ backgroundColor: getAvatarColor(role.code) }">
              {{ role.name.charAt(0) }}
            </div>
            <div class="role-details">
              <div class="role-name">{{ role.name }}</div>
              <div class="role-code">{{ role.code }}</div>
              <div class="role-description" v-if="role.description">{{ role.description }}</div>
            </div>
          </div>
        </div>
        <div class="table-cell">
          <span class="status-badge" :class="role.status.toLowerCase()">
            {{ getStatusText(role.status) }}
          </span>
        </div>
        <div class="table-cell">
          <span class="permission-count">{{ role.permissions ? role.permissions.length : 0 }}</span>
        </div>
        <div class="table-cell">
          <div class="role-date">{{ formatDate(role.createdAt) }}</div>
        </div>
        <div class="table-cell">
          <div class="action-buttons">
            <button class="action-btn edit" @click="editRole(role)" title="编辑">
              ✏️
            </button>
            <button class="action-btn permissions" @click="managePermissions(role)" title="权限管理">
              🔑
            </button>
            <button class="action-btn status" @click="toggleRoleStatus(role)" title="切换状态">
              {{ role.status === 'ACTIVE' ? '🔒' : '🔓' }}
            </button>
            <button class="action-btn delete" @click="deleteRole(role)" title="删除">
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
    <div v-if="!loading && roles.length === 0" class="empty-state">
      <div class="empty-icon">🛡️</div>
      <h3>暂无角色数据</h3>
      <p>点击上方"添加角色"按钮创建第一个角色</p>
    </div>

    <!-- 添加/编辑角色模态框 -->
    <div class="modal-overlay" v-if="showAddModal || showEditModal" @click="closeModal">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>{{ showEditModal ? '编辑角色' : '添加角色' }}</h3>
          <button class="close-btn" @click="closeModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-row">
            <div class="form-group">
              <label>角色名称 *</label>
              <input 
                v-model="currentRole.name" 
                type="text" 
                class="form-input"
                placeholder="请输入角色名称"
              />
            </div>
            <div class="form-group">
              <label>角色代码 *</label>
              <input 
                v-model="currentRole.code" 
                type="text" 
                class="form-input"
                placeholder="请输入角色代码（如：ADMIN）"
                :disabled="showEditModal"
              />
            </div>
          </div>
          <div class="form-row">
            <div class="form-group full-width">
              <label>描述</label>
              <textarea 
                v-model="currentRole.description" 
                class="form-input"
                placeholder="请输入角色描述"
                rows="3"
              ></textarea>
            </div>
          </div>
          <div class="form-row">
            <div class="form-group">
              <label>状态</label>
              <select v-model="currentRole.status" class="form-input">
                <option value="ACTIVE">启用</option>
                <option value="INACTIVE">禁用</option>
              </select>
            </div>
            <div class="form-group">
              <label>排序</label>
              <input 
                v-model.number="currentRole.sortOrder" 
                type="number" 
                class="form-input"
                placeholder="排序值（数字越小越靠前）"
              />
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="closeModal">取消</button>
          <button class="btn primary" @click="saveRole" :disabled="saving">
            {{ saving ? '保存中...' : (showEditModal ? '更新' : '添加') }}
          </button>
        </div>
      </div>
    </div>

    <!-- 权限管理模态框 -->
    <div class="modal-overlay" v-if="showPermissionModal" @click="closePermissionModal">
      <div class="modal large" @click.stop>
        <div class="modal-header">
          <h3>权限管理 - {{ selectedRole?.name }}</h3>
          <button class="close-btn" @click="closePermissionModal">✕</button>
        </div>
        <div class="modal-body">
          <div class="permission-groups">
            <div v-for="(permissions, module) in groupedPermissions" :key="module" class="permission-group">
              <div class="group-header">
                <h4>{{ module }}</h4>
                <label class="checkbox-label">
                  <input 
                    type="checkbox" 
                    :checked="isModuleAllSelected(module)"
                    :indeterminate="isModulePartialSelected(module)"
                    @change="toggleModule(module)"
                  />
                  全选
                </label>
              </div>
              <div class="permission-list">
                <label 
                  v-for="permission in permissions" 
                  :key="permission.id" 
                  class="permission-item"
                >
                  <input 
                    type="checkbox" 
                    :value="permission.id"
                    v-model="selectedPermissions"
                  />
                  <div class="permission-info">
                    <span class="permission-name">{{ permission.name }}</span>
                    <span class="permission-code">{{ permission.code }}</span>
                    <span class="permission-type" :class="permission.type.toLowerCase()">
                      {{ getPermissionTypeText(permission.type) }}
                    </span>
                  </div>
                </label>
              </div>
            </div>
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="closePermissionModal">取消</button>
          <button class="btn primary" @click="savePermissions" :disabled="savingPermissions">
            {{ savingPermissions ? '保存中...' : '保存权限' }}
          </button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'
import api from '../services/api'

export default {
  name: 'Roles',
  setup() {
    const roles = ref([])
    const permissions = ref([])
    const loading = ref(false)
    const saving = ref(false)
    const savingPermissions = ref(false)
    const searchQuery = ref('')
    const statusFilter = ref('')
    const showAddModal = ref(false)
    const showEditModal = ref(false)
    const showPermissionModal = ref(false)
    
    // 分页相关
    const currentPage = ref(0)
    const pageSize = ref(10)
    const totalRoles = ref(0)
    const totalPages = ref(0)
    
    // 表单数据
    const currentRole = ref({
      name: '',
      code: '',
      description: '',
      status: 'ACTIVE',
      sortOrder: 0
    })
    
    // 权限管理相关
    const selectedRole = ref(null)
    const selectedPermissions = ref([])
    
    // 搜索防抖
    let searchTimeout = null

    const groupedPermissions = computed(() => {
      const groups = {}
      permissions.value.forEach(permission => {
        if (!groups[permission.module]) {
          groups[permission.module] = []
        }
        groups[permission.module].push(permission)
      })
      return groups
    })

    const getAvatarColor = (code) => {
      const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe', '#43e97b', '#38f9d7']
      const index = code.charCodeAt(0) % colors.length
      return colors[index]
    }

    const getStatusText = (status) => {
      const statusMap = {
        ACTIVE: '启用',
        INACTIVE: '禁用'
      }
      return statusMap[status] || status
    }

    const getPermissionTypeText = (type) => {
      const typeMap = {
        MENU: '菜单',
        BUTTON: '按钮',
        API: '接口'
      }
      return typeMap[type] || type
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

    const loadRoles = async () => {
      loading.value = true
      try {
        const params = {
          page: currentPage.value,
          size: pageSize.value,
          sortBy: 'sortOrder',
          sortDir: 'asc'
        }
        
        if (searchQuery.value.trim()) {
          params.search = searchQuery.value.trim()
        }
        
        if (statusFilter.value) {
          params.status = statusFilter.value
        }
        
        const response = await api.get('/roles', { params })
        
        if (response.success) {
          const data = response.data
          roles.value = data.roles || []
          totalRoles.value = data.totalItems || 0
          totalPages.value = data.totalPages || 0
          currentPage.value = data.currentPage || 0
        } else {
          console.error('API返回错误:', response.message)
          alert('获取角色列表失败: ' + response.message)
        }
      } catch (error) {
        console.error('加载角色列表失败:', error)
        alert('加载角色列表失败: ' + (error.response?.data?.message || error.message))
      } finally {
        loading.value = false
      }
    }

    const loadPermissions = async () => {
      try {
        const response = await api.get('/permissions/active')
        if (response.success) {
          permissions.value = response.data || []
        }
      } catch (error) {
        console.error('加载权限列表失败:', error)
      }
    }

    const searchRoles = () => {
      if (searchTimeout) {
        clearTimeout(searchTimeout)
      }
      searchTimeout = setTimeout(() => {
        currentPage.value = 0
        loadRoles()
      }, 500)
    }

    const changePage = (page) => {
      currentPage.value = page
      loadRoles()
    }

    const resetForm = () => {
      currentRole.value = {
        name: '',
        code: '',
        description: '',
        status: 'ACTIVE',
        sortOrder: 0
      }
    }

    const closeModal = () => {
      showAddModal.value = false
      showEditModal.value = false
      resetForm()
    }

    const editRole = (role) => {
      currentRole.value = { ...role }
      showEditModal.value = true
    }

    const saveRole = async () => {
      if (!currentRole.value.name || !currentRole.value.code) {
        alert('请填写角色名称和代码')
        return
      }
      
      saving.value = true
      try {
        let response
        if (showEditModal.value) {
          response = await api.put(`/roles/${currentRole.value.id}`, currentRole.value)
        } else {
          response = await api.post('/roles', currentRole.value)
        }
        
        if (response.success) {
          alert(showEditModal.value ? '角色更新成功' : '角色创建成功')
          closeModal()
          loadRoles()
        }
      } catch (error) {
        console.error('保存角色失败:', error)
        alert('保存角色失败: ' + (error.response?.data?.message || error.message))
      } finally {
        saving.value = false
      }
    }

    const deleteRole = async (role) => {
      if (!confirm(`确定要删除角色 ${role.name} 吗？此操作不可恢复！`)) {
        return
      }
      
      try {
        const response = await api.delete(`/roles/${role.id}`)
        if (response.success) {
          alert('角色删除成功')
          loadRoles()
        }
      } catch (error) {
        console.error('删除角色失败:', error)
        alert('删除角色失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const toggleRoleStatus = async (role) => {
      const newStatus = role.status === 'ACTIVE' ? 'INACTIVE' : 'ACTIVE'
      const action = newStatus === 'ACTIVE' ? '启用' : '禁用'
      
      if (!confirm(`确定要${action}角色 ${role.name} 吗？`)) {
        return
      }
      
      try {
        const response = await api.patch(`/roles/${role.id}/status`, { status: newStatus })
        if (response.success) {
          alert(`角色${action}成功`)
          loadRoles()
        }
      } catch (error) {
        console.error('更新角色状态失败:', error)
        alert('更新角色状态失败: ' + (error.response?.data?.message || error.message))
      }
    }

    const managePermissions = async (role) => {
      selectedRole.value = role
      
      try {
        // 获取角色的权限
        const response = await api.get(`/roles/${role.id}/permissions`)
        if (response.success) {
          const roleWithPermissions = response.data
          selectedPermissions.value = roleWithPermissions.permissions.map(p => p.id)
        }
      } catch (error) {
        console.error('获取角色权限失败:', error)
        selectedPermissions.value = []
      }
      
      showPermissionModal.value = true
    }

    const closePermissionModal = () => {
      showPermissionModal.value = false
      selectedRole.value = null
      selectedPermissions.value = []
    }

    const isModuleAllSelected = (module) => {
      const modulePermissions = groupedPermissions.value[module]
      return modulePermissions.every(p => selectedPermissions.value.includes(p.id))
    }

    const isModulePartialSelected = (module) => {
      const modulePermissions = groupedPermissions.value[module]
      const selectedCount = modulePermissions.filter(p => selectedPermissions.value.includes(p.id)).length
      return selectedCount > 0 && selectedCount < modulePermissions.length
    }

    const toggleModule = (module) => {
      const modulePermissions = groupedPermissions.value[module]
      const allSelected = isModuleAllSelected(module)
      
      if (allSelected) {
        // 取消选择该模块的所有权限
        modulePermissions.forEach(p => {
          const index = selectedPermissions.value.indexOf(p.id)
          if (index > -1) {
            selectedPermissions.value.splice(index, 1)
          }
        })
      } else {
        // 选择该模块的所有权限
        modulePermissions.forEach(p => {
          if (!selectedPermissions.value.includes(p.id)) {
            selectedPermissions.value.push(p.id)
          }
        })
      }
    }

    const savePermissions = async () => {
      savingPermissions.value = true
      try {
        const response = await api.put(`/roles/${selectedRole.value.id}/permissions`, {
          permissionIds: selectedPermissions.value
        })
        
        if (response.success) {
          alert('权限分配成功')
          closePermissionModal()
          loadRoles()
        }
      } catch (error) {
        console.error('保存权限失败:', error)
        alert('保存权限失败: ' + (error.response?.data?.message || error.message))
      } finally {
        savingPermissions.value = false
      }
    }

    onMounted(() => {
      loadRoles()
      loadPermissions()
    })

    return {
      roles,
      permissions,
      loading,
      saving,
      savingPermissions,
      searchQuery,
      statusFilter,
      showAddModal,
      showEditModal,
      showPermissionModal,
      currentPage,
      totalPages,
      currentRole,
      selectedRole,
      selectedPermissions,
      groupedPermissions,
      getAvatarColor,
      getStatusText,
      getPermissionTypeText,
      formatDate,
      loadRoles,
      searchRoles,
      changePage,
      closeModal,
      editRole,
      saveRole,
      deleteRole,
      toggleRoleStatus,
      managePermissions,
      closePermissionModal,
      isModuleAllSelected,
      isModulePartialSelected,
      toggleModule,
      savePermissions
    }
  }
}
</script>

<style scoped>
.roles {
  padding: 2rem;
  max-width: 1400px;
  margin: 0 auto;
}

.roles-header {
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

.add-role-btn {
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

.add-role-btn:hover {
  transform: translateY(-2px);
}

.roles-filters {
  display: flex;
  gap: 1rem;
  margin-bottom: 2rem;
  align-items: center;
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

.status-filter {
  padding: 0.75rem 1rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
}

.roles-list {
  background: white;
  border-radius: 12px;
  box-shadow: 0 4px 6px rgba(0, 0, 0, 0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1.5fr;
  gap: 1rem;
  padding: 1rem;
  background: #f8f9fa;
  font-weight: 600;
  color: #2c3e50;
  border-bottom: 1px solid #e1e8ed;
}

.table-row {
  display: grid;
  grid-template-columns: 2fr 1fr 1fr 1fr 1.5fr;
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

.role-info {
  display: flex;
  align-items: center;
  gap: 1rem;
}

.role-avatar {
  width: 48px;
  height: 48px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  color: white;
  font-weight: 600;
  font-size: 1.2rem;
}

.role-details {
  flex: 1;
}

.role-name {
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 0.25rem;
}

.role-code {
  font-size: 0.9rem;
  color: #7f8c8d;
  font-family: monospace;
  margin-bottom: 0.25rem;
}

.role-description {
  font-size: 0.85rem;
  color: #95a5a6;
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

.permission-count {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-weight: 500;
}

.role-date {
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

.action-btn.permissions {
  background: rgba(155, 89, 182, 0.1);
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

.modal.large {
  max-width: 800px;
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

/* 权限管理样式 */
.permission-groups {
  max-height: 400px;
  overflow-y: auto;
}

.permission-group {
  margin-bottom: 1.5rem;
  border: 1px solid #e1e8ed;
  border-radius: 8px;
  overflow: hidden;
}

.group-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background: #f8f9fa;
  border-bottom: 1px solid #e1e8ed;
}

.group-header h4 {
  margin: 0;
  color: #2c3e50;
}

.checkbox-label {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
  font-size: 0.9rem;
  color: #7f8c8d;
}

.permission-list {
  padding: 1rem;
}

.permission-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 0.5rem 0;
  cursor: pointer;
  border-bottom: 1px solid #f1f3f4;
}

.permission-item:last-child {
  border-bottom: none;
}

.permission-info {
  display: flex;
  flex-direction: column;
  gap: 0.25rem;
}

.permission-name {
  font-weight: 500;
  color: #2c3e50;
}

.permission-code {
  font-size: 0.85rem;
  color: #7f8c8d;
  font-family: monospace;
}

.permission-type {
  font-size: 0.75rem;
  padding: 0.125rem 0.5rem;
  border-radius: 12px;
  font-weight: 500;
  width: fit-content;
}

.permission-type.menu {
  background: rgba(52, 152, 219, 0.1);
  color: #3498db;
}

.permission-type.button {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.permission-type.api {
  background: rgba(155, 89, 182, 0.1);
  color: #9b59b6;
}
</style> 