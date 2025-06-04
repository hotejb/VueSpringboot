<template>
  <div class="settings">
    <div class="settings-header">
      <h1 class="page-title">⚙️ 系统设置</h1>
      <p class="page-subtitle">配置系统参数和个人偏好</p>
    </div>

    <div class="settings-content">
      <div class="settings-sidebar">
        <div class="sidebar-menu">
          <div 
            v-for="tab in tabs" 
            :key="tab.id"
            class="menu-item"
            :class="{ active: activeTab === tab.id }"
            @click="activeTab = tab.id"
          >
            <span class="menu-icon">{{ tab.icon }}</span>
            <span class="menu-text">{{ tab.name }}</span>
          </div>
        </div>
      </div>

      <div class="settings-main">
        <!-- 个人设置 -->
        <div v-if="activeTab === 'profile'" class="settings-section">
          <h2 class="section-title">👤 个人设置</h2>
          <div class="setting-group">
            <div class="setting-item">
              <label class="setting-label">用户名</label>
              <input v-model="profile.username" type="text" class="setting-input" />
            </div>
            <div class="setting-item">
              <label class="setting-label">邮箱地址</label>
              <input v-model="profile.email" type="email" class="setting-input" />
            </div>
            <div class="setting-item">
              <label class="setting-label">手机号码</label>
              <input v-model="profile.phone" type="tel" class="setting-input" />
            </div>
            <div class="setting-item">
              <label class="setting-label">个人简介</label>
              <textarea v-model="profile.bio" class="setting-textarea" rows="4"></textarea>
            </div>
          </div>
          <button class="save-btn" @click="saveProfile">保存个人设置</button>
        </div>

        <!-- 系统配置 -->
        <div v-if="activeTab === 'system'" class="settings-section">
          <h2 class="section-title">🖥️ 系统配置</h2>
          <div class="setting-group">
            <div class="setting-item">
              <label class="setting-label">系统语言</label>
              <select v-model="system.language" class="setting-select">
                <option value="zh-CN">简体中文</option>
                <option value="en-US">English</option>
                <option value="ja-JP">日本語</option>
              </select>
            </div>
            <div class="setting-item">
              <label class="setting-label">时区设置</label>
              <select v-model="system.timezone" class="setting-select">
                <option value="Asia/Shanghai">北京时间 (UTC+8)</option>
                <option value="America/New_York">纽约时间 (UTC-5)</option>
                <option value="Europe/London">伦敦时间 (UTC+0)</option>
              </select>
            </div>
            <div class="setting-item">
              <label class="setting-label">主题模式</label>
              <div class="radio-group">
                <label class="radio-item">
                  <input v-model="system.theme" type="radio" value="light" />
                  <span>浅色模式</span>
                </label>
                <label class="radio-item">
                  <input v-model="system.theme" type="radio" value="dark" />
                  <span>深色模式</span>
                </label>
                <label class="radio-item">
                  <input v-model="system.theme" type="radio" value="auto" />
                  <span>跟随系统</span>
                </label>
              </div>
            </div>
          </div>
          <button class="save-btn" @click="saveSystem">保存系统配置</button>
        </div>

        <!-- 通知设置 -->
        <div v-if="activeTab === 'notifications'" class="settings-section">
          <h2 class="section-title">🔔 通知设置</h2>
          <div class="setting-group">
            <div class="setting-item">
              <div class="toggle-item">
                <label class="toggle-label">邮件通知</label>
                <div class="toggle-switch" :class="{ active: notifications.email }" @click="notifications.email = !notifications.email">
                  <div class="toggle-slider"></div>
                </div>
              </div>
              <p class="setting-desc">接收重要系统邮件通知</p>
            </div>
            <div class="setting-item">
              <div class="toggle-item">
                <label class="toggle-label">短信通知</label>
                <div class="toggle-switch" :class="{ active: notifications.sms }" @click="notifications.sms = !notifications.sms">
                  <div class="toggle-slider"></div>
                </div>
              </div>
              <p class="setting-desc">接收重要系统短信通知</p>
            </div>
            <div class="setting-item">
              <div class="toggle-item">
                <label class="toggle-label">浏览器推送</label>
                <div class="toggle-switch" :class="{ active: notifications.push }" @click="notifications.push = !notifications.push">
                  <div class="toggle-slider"></div>
                </div>
              </div>
              <p class="setting-desc">在浏览器中显示推送通知</p>
            </div>
          </div>
          <button class="save-btn" @click="saveNotifications">保存通知设置</button>
        </div>

        <!-- 安全设置 -->
        <div v-if="activeTab === 'security'" class="settings-section">
          <h2 class="section-title">🔒 安全设置</h2>
          <div class="setting-group">
            <div class="setting-item">
              <label class="setting-label">当前密码</label>
              <input v-model="security.currentPassword" type="password" class="setting-input" />
            </div>
            <div class="setting-item">
              <label class="setting-label">新密码</label>
              <input v-model="security.newPassword" type="password" class="setting-input" />
            </div>
            <div class="setting-item">
              <label class="setting-label">确认新密码</label>
              <input v-model="security.confirmPassword" type="password" class="setting-input" />
            </div>
            <div class="setting-item">
              <div class="toggle-item">
                <label class="toggle-label">双因素认证</label>
                <div class="toggle-switch" :class="{ active: security.twoFactor }" @click="security.twoFactor = !security.twoFactor">
                  <div class="toggle-slider"></div>
                </div>
              </div>
              <p class="setting-desc">启用双因素认证以提高账户安全性</p>
            </div>
          </div>
          <button class="save-btn" @click="saveSecurity">更新安全设置</button>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref } from 'vue'

export default {
  name: 'Settings',
  setup() {
    const activeTab = ref('profile')
    
    const tabs = [
      { id: 'profile', name: '个人设置', icon: '👤' },
      { id: 'system', name: '系统配置', icon: '🖥️' },
      { id: 'notifications', name: '通知设置', icon: '🔔' },
      { id: 'security', name: '安全设置', icon: '🔒' }
    ]

    const profile = ref({
      username: '管理员',
      email: 'admin@example.com',
      phone: '+86 138-0013-8000',
      bio: '系统管理员，负责平台的日常运维和管理工作。'
    })

    const system = ref({
      language: 'zh-CN',
      timezone: 'Asia/Shanghai',
      theme: 'light'
    })

    const notifications = ref({
      email: true,
      sms: false,
      push: true
    })

    const security = ref({
      currentPassword: '',
      newPassword: '',
      confirmPassword: '',
      twoFactor: false
    })

    const saveProfile = () => {
      // 这里可以调用API保存个人设置
      alert('个人设置已保存！')
    }

    const saveSystem = () => {
      // 这里可以调用API保存系统配置
      alert('系统配置已保存！')
    }

    const saveNotifications = () => {
      // 这里可以调用API保存通知设置
      alert('通知设置已保存！')
    }

    const saveSecurity = () => {
      if (security.value.newPassword !== security.value.confirmPassword) {
        alert('新密码和确认密码不匹配！')
        return
      }
      // 这里可以调用API保存安全设置
      alert('安全设置已更新！')
      security.value.currentPassword = ''
      security.value.newPassword = ''
      security.value.confirmPassword = ''
    }

    return {
      activeTab,
      tabs,
      profile,
      system,
      notifications,
      security,
      saveProfile,
      saveSystem,
      saveNotifications,
      saveSecurity
    }
  }
}
</script>

<style scoped>
.settings {
  padding: 2rem 0;
}

.settings-header {
  text-align: center;
  margin-bottom: 3rem;
}

.page-title {
  font-size: 2.5rem;
  font-weight: 700;
  color: #2c3e50;
  margin-bottom: 0.5rem;
}

.page-subtitle {
  font-size: 1.1rem;
  color: #7f8c8d;
}

.settings-content {
  display: grid;
  grid-template-columns: 250px 1fr;
  gap: 2rem;
  max-width: 1200px;
  margin: 0 auto;
}

.settings-sidebar {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  padding: 1rem;
  height: fit-content;
}

.sidebar-menu {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.menu-item {
  display: flex;
  align-items: center;
  gap: 0.75rem;
  padding: 1rem;
  border-radius: 10px;
  cursor: pointer;
  transition: all 0.3s ease;
  color: #7f8c8d;
}

.menu-item:hover {
  background: #f8f9fa;
  color: #2c3e50;
}

.menu-item.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
}

.menu-icon {
  font-size: 1.2rem;
}

.menu-text {
  font-weight: 500;
}

.settings-main {
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  padding: 2rem;
}

.settings-section {
  max-width: 600px;
}

.section-title {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 2rem;
  padding-bottom: 1rem;
  border-bottom: 2px solid #f0f0f0;
}

.setting-group {
  display: flex;
  flex-direction: column;
  gap: 1.5rem;
  margin-bottom: 2rem;
}

.setting-item {
  display: flex;
  flex-direction: column;
  gap: 0.5rem;
}

.setting-label {
  font-weight: 500;
  color: #2c3e50;
}

.setting-input,
.setting-select,
.setting-textarea {
  padding: 0.75rem;
  border: 2px solid #e1e8ed;
  border-radius: 8px;
  font-size: 1rem;
  transition: border-color 0.3s ease;
}

.setting-input:focus,
.setting-select:focus,
.setting-textarea:focus {
  outline: none;
  border-color: #667eea;
}

.setting-textarea {
  resize: vertical;
  min-height: 100px;
}

.radio-group {
  display: flex;
  gap: 1rem;
  flex-wrap: wrap;
}

.radio-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  cursor: pointer;
}

.radio-item input[type="radio"] {
  margin: 0;
}

.toggle-item {
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toggle-label {
  font-weight: 500;
  color: #2c3e50;
}

.toggle-switch {
  width: 50px;
  height: 26px;
  background: #e1e8ed;
  border-radius: 13px;
  position: relative;
  cursor: pointer;
  transition: background-color 0.3s ease;
}

.toggle-switch.active {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.toggle-slider {
  width: 22px;
  height: 22px;
  background: white;
  border-radius: 50%;
  position: absolute;
  top: 2px;
  left: 2px;
  transition: transform 0.3s ease;
  box-shadow: 0 2px 4px rgba(0,0,0,0.2);
}

.toggle-switch.active .toggle-slider {
  transform: translateX(24px);
}

.setting-desc {
  font-size: 0.9rem;
  color: #7f8c8d;
  margin: 0;
}

.save-btn {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  border: none;
  padding: 0.75rem 2rem;
  border-radius: 8px;
  font-weight: 600;
  cursor: pointer;
  transition: transform 0.3s ease;
}

.save-btn:hover {
  transform: translateY(-2px);
}

/* 响应式设计 */
@media (max-width: 768px) {
  .settings-content {
    grid-template-columns: 1fr;
    gap: 1rem;
  }
  
  .settings-sidebar {
    order: 2;
  }
  
  .sidebar-menu {
    flex-direction: row;
    overflow-x: auto;
    gap: 0.5rem;
  }
  
  .menu-item {
    flex-shrink: 0;
    padding: 0.75rem 1rem;
  }
  
  .menu-text {
    white-space: nowrap;
  }
  
  .settings-main {
    order: 1;
    padding: 1.5rem;
  }
  
  .page-title {
    font-size: 2rem;
  }
  
  .radio-group {
    flex-direction: column;
    gap: 0.75rem;
  }
}
</style> 