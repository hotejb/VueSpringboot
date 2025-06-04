<template>
  <div class="users">
    <div class="users-header">
      <h1 class="page-title">👥 用户管理</h1>
      <button class="add-user-btn" @click="showAddModal = true">
        <i class="icon">➕</i>
        添加用户
      </button>
    </div>

    <div class="users-filters">
      <div class="search-box">
        <input 
          v-model="searchQuery" 
          type="text" 
          placeholder="搜索用户..." 
          class="search-input"
        />
        <i class="search-icon">🔍</i>
      </div>
      <select v-model="statusFilter" class="status-filter">
        <option value="">所有状态</option>
        <option value="active">活跃</option>
        <option value="inactive">非活跃</option>
        <option value="banned">已禁用</option>
      </select>
    </div>

    <div class="users-table">
      <div class="table-header">
        <div class="table-cell">头像</div>
        <div class="table-cell">用户名</div>
        <div class="table-cell">邮箱</div>
        <div class="table-cell">状态</div>
        <div class="table-cell">注册时间</div>
        <div class="table-cell">操作</div>
      </div>
      
      <div class="table-body">
        <div 
          v-for="user in filteredUsers" 
          :key="user.id" 
          class="table-row"
        >
          <div class="table-cell">
            <div class="user-avatar" :style="{ background: user.avatarColor }">
              {{ user.username.charAt(0).toUpperCase() }}
            </div>
          </div>
          <div class="table-cell">
            <span class="user-name">{{ user.username }}</span>
          </div>
          <div class="table-cell">
            <span class="user-email">{{ user.email }}</span>
          </div>
          <div class="table-cell">
            <span class="status-badge" :class="user.status">
              {{ getStatusText(user.status) }}
            </span>
          </div>
          <div class="table-cell">
            <span class="user-date">{{ user.createdAt }}</span>
          </div>
          <div class="table-cell">
            <div class="action-buttons">
              <button class="action-btn edit" @click="editUser(user)">
                ✏️
              </button>
              <button class="action-btn delete" @click="deleteUser(user)">
                🗑️
              </button>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- 添加用户模态框 -->
    <div v-if="showAddModal" class="modal-overlay" @click="showAddModal = false">
      <div class="modal" @click.stop>
        <div class="modal-header">
          <h3>添加新用户</h3>
          <button class="close-btn" @click="showAddModal = false">✕</button>
        </div>
        <div class="modal-body">
          <div class="form-group">
            <label>用户名</label>
            <input v-model="newUser.username" type="text" class="form-input" />
          </div>
          <div class="form-group">
            <label>邮箱</label>
            <input v-model="newUser.email" type="email" class="form-input" />
          </div>
          <div class="form-group">
            <label>密码</label>
            <input v-model="newUser.password" type="password" class="form-input" />
          </div>
        </div>
        <div class="modal-footer">
          <button class="btn secondary" @click="showAddModal = false">取消</button>
          <button class="btn primary" @click="addUser">添加</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, computed, onMounted } from 'vue'

export default {
  name: 'Users',
  setup() {
    const users = ref([])
    const searchQuery = ref('')
    const statusFilter = ref('')
    const showAddModal = ref(false)
    const newUser = ref({
      username: '',
      email: '',
      password: ''
    })

    const filteredUsers = computed(() => {
      let filtered = users.value

      if (searchQuery.value) {
        filtered = filtered.filter(user => 
          user.username.toLowerCase().includes(searchQuery.value.toLowerCase()) ||
          user.email.toLowerCase().includes(searchQuery.value.toLowerCase())
        )
      }

      if (statusFilter.value) {
        filtered = filtered.filter(user => user.status === statusFilter.value)
      }

      return filtered
    })

    const getStatusText = (status) => {
      const statusMap = {
        active: '活跃',
        inactive: '非活跃',
        banned: '已禁用'
      }
      return statusMap[status] || status
    }

    const generateMockUsers = () => {
      const names = ['张三', '李四', '王五', '赵六', '钱七', '孙八', '周九', '吴十']
      const statuses = ['active', 'inactive', 'banned']
      const colors = ['#667eea', '#764ba2', '#f093fb', '#f5576c', '#4facfe', '#00f2fe']
      
      return names.map((name, index) => ({
        id: index + 1,
        username: name,
        email: `${name.toLowerCase()}@example.com`,
        status: statuses[Math.floor(Math.random() * statuses.length)],
        createdAt: new Date(Date.now() - Math.random() * 365 * 24 * 60 * 60 * 1000).toLocaleDateString(),
        avatarColor: colors[Math.floor(Math.random() * colors.length)]
      }))
    }

    const addUser = () => {
      if (newUser.value.username && newUser.value.email && newUser.value.password) {
        const user = {
          id: users.value.length + 1,
          username: newUser.value.username,
          email: newUser.value.email,
          status: 'active',
          createdAt: new Date().toLocaleDateString(),
          avatarColor: '#667eea'
        }
        users.value.push(user)
        newUser.value = { username: '', email: '', password: '' }
        showAddModal.value = false
      }
    }

    const editUser = (user) => {
      // 这里可以实现编辑功能
      // 可以打开编辑模态框或跳转到编辑页面
    }

    const deleteUser = (user) => {
      if (confirm(`确定要删除用户 ${user.username} 吗？`)) {
        users.value = users.value.filter(u => u.id !== user.id)
      }
    }

    onMounted(() => {
      users.value = generateMockUsers()
    })

    return {
      users,
      searchQuery,
      statusFilter,
      showAddModal,
      newUser,
      filteredUsers,
      getStatusText,
      addUser,
      editUser,
      deleteUser
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

.status-filter {
  padding: 0.75rem 1rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  background: white;
  cursor: pointer;
}

.users-table {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  overflow: hidden;
}

.table-header {
  display: grid;
  grid-template-columns: 80px 1fr 1fr 120px 120px 120px;
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
  grid-template-columns: 80px 1fr 1fr 120px 120px 120px;
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

.user-name {
  font-weight: 600;
  color: #2c3e50;
}

.user-email {
  color: #7f8c8d;
}

.status-badge {
  padding: 0.25rem 0.75rem;
  border-radius: 20px;
  font-size: 0.8rem;
  font-weight: 600;
  text-align: center;
}

.status-badge.active {
  background: rgba(39, 174, 96, 0.1);
  color: #27ae60;
}

.status-badge.inactive {
  background: rgba(241, 196, 15, 0.1);
  color: #f1c40f;
}

.status-badge.banned {
  background: rgba(231, 76, 60, 0.1);
  color: #e74c3c;
}

.user-date {
  color: #7f8c8d;
  font-size: 0.9rem;
}

.action-buttons {
  display: flex;
  gap: 0.5rem;
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
}

.action-btn:hover {
  transform: scale(1.1);
}

.action-btn.edit {
  background: rgba(52, 152, 219, 0.1);
}

.action-btn.delete {
  background: rgba(231, 76, 60, 0.1);
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
  max-width: 500px;
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

.form-group {
  margin-bottom: 1.5rem;
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

.btn:hover {
  transform: translateY(-2px);
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
@media (max-width: 768px) {
  .users-header {
    flex-direction: column;
    gap: 1rem;
    align-items: stretch;
  }
  
  .users-filters {
    flex-direction: column;
  }
  
  .table-header,
  .table-row {
    grid-template-columns: 60px 1fr 80px 80px;
    font-size: 0.9rem;
  }
  
  .table-header .table-cell:nth-child(3),
  .table-row .table-cell:nth-child(3),
  .table-header .table-cell:nth-child(5),
  .table-row .table-cell:nth-child(5) {
    display: none;
  }
  
  .page-title {
    font-size: 2rem;
  }
}
</style> 