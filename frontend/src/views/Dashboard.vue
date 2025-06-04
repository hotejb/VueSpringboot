<template>
  <div class="dashboard">
    <div class="dashboard-header">
      <h1 class="page-title">📈 数据面板</h1>
      <p class="page-subtitle">实时监控系统运行状态</p>
    </div>

    <div class="stats-grid">
      <div class="stat-card">
        <div class="stat-icon">👥</div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.users }}</h3>
          <p class="stat-label">在线用户</p>
        </div>
        <div class="stat-trend up">+12%</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">📊</div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.requests }}</h3>
          <p class="stat-label">今日请求</p>
        </div>
        <div class="stat-trend up">+8%</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">💾</div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.storage }}</h3>
          <p class="stat-label">存储使用</p>
        </div>
        <div class="stat-trend down">-3%</div>
      </div>

      <div class="stat-card">
        <div class="stat-icon">⚡</div>
        <div class="stat-content">
          <h3 class="stat-number">{{ stats.performance }}</h3>
          <p class="stat-label">系统性能</p>
        </div>
        <div class="stat-trend up">+5%</div>
      </div>
    </div>

    <div class="charts-section">
      <div class="chart-container">
        <h3 class="chart-title">📈 访问趋势</h3>
        <div class="chart-placeholder">
          <div class="chart-bars">
            <div class="bar" style="height: 60%"></div>
            <div class="bar" style="height: 80%"></div>
            <div class="bar" style="height: 45%"></div>
            <div class="bar" style="height: 90%"></div>
            <div class="bar" style="height: 70%"></div>
            <div class="bar" style="height: 85%"></div>
            <div class="bar" style="height: 95%"></div>
          </div>
          <div class="chart-labels">
            <span>周一</span>
            <span>周二</span>
            <span>周三</span>
            <span>周四</span>
            <span>周五</span>
            <span>周六</span>
            <span>周日</span>
          </div>
        </div>
      </div>

      <div class="chart-container">
        <h3 class="chart-title">🍰 用户分布</h3>
        <div class="pie-chart">
          <div class="pie-segment" style="--percentage: 40; --color: #667eea;">
            <span class="pie-label">桌面端 40%</span>
          </div>
          <div class="pie-segment" style="--percentage: 35; --color: #764ba2;">
            <span class="pie-label">移动端 35%</span>
          </div>
          <div class="pie-segment" style="--percentage: 25; --color: #f093fb;">
            <span class="pie-label">平板端 25%</span>
          </div>
        </div>
      </div>
    </div>

    <div class="activity-section">
      <h3 class="section-title">📋 最近活动</h3>
      <div class="activity-list">
        <div class="activity-item" v-for="activity in activities" :key="activity.id">
          <div class="activity-icon" :class="activity.type">{{ activity.icon }}</div>
          <div class="activity-content">
            <p class="activity-text">{{ activity.text }}</p>
            <span class="activity-time">{{ activity.time }}</span>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
import { ref, onMounted } from 'vue'

export default {
  name: 'Dashboard',
  setup() {
    const stats = ref({
      users: 0,
      requests: 0,
      storage: '0GB',
      performance: '0%'
    })

    const activities = ref([
      {
        id: 1,
        icon: '👤',
        type: 'user',
        text: '新用户 张三 注册成功',
        time: '2分钟前'
      },
      {
        id: 2,
        icon: '📊',
        type: 'data',
        text: '数据备份完成',
        time: '15分钟前'
      },
      {
        id: 3,
        icon: '🔧',
        type: 'system',
        text: '系统更新已安装',
        time: '1小时前'
      },
      {
        id: 4,
        icon: '🚨',
        type: 'warning',
        text: 'CPU使用率达到85%',
        time: '2小时前'
      },
      {
        id: 5,
        icon: '✅',
        type: 'success',
        text: '定时任务执行成功',
        time: '3小时前'
      }
    ])

    const animateNumbers = () => {
      // 模拟数据加载动画
      const targets = {
        users: 1247,
        requests: 8934,
        storage: '2.4GB',
        performance: '92%'
      }

      // 数字动画
      const animateNumber = (key, target) => {
        if (typeof target === 'string') {
          setTimeout(() => {
            stats.value[key] = target
          }, Math.random() * 1000 + 500)
          return
        }

        let current = 0
        const increment = target / 50
        const timer = setInterval(() => {
          current += increment
          if (current >= target) {
            current = target
            clearInterval(timer)
          }
          stats.value[key] = Math.floor(current)
        }, 30)
      }

      Object.entries(targets).forEach(([key, value]) => {
        animateNumber(key, value)
      })
    }

    onMounted(() => {
      animateNumbers()
    })

    return {
      stats,
      activities
    }
  }
}
</script>

<style scoped>
.dashboard {
  padding: 2rem 0;
}

.dashboard-header {
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

.stats-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
  margin-bottom: 3rem;
}

.stat-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  display: flex;
  align-items: center;
  gap: 1.5rem;
  transition: transform 0.3s ease;
  position: relative;
  overflow: hidden;
}

.stat-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 4px;
  height: 100%;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.stat-card:hover {
  transform: translateY(-5px);
}

.stat-icon {
  font-size: 3rem;
  padding: 1rem;
  border-radius: 12px;
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.stat-content {
  flex: 1;
}

.stat-number {
  font-size: 2rem;
  font-weight: 700;
  color: #2c3e50;
  margin: 0 0 0.5rem 0;
}

.stat-label {
  color: #7f8c8d;
  margin: 0;
  font-size: 0.9rem;
}

.stat-trend {
  font-size: 0.9rem;
  font-weight: 600;
  padding: 0.25rem 0.5rem;
  border-radius: 6px;
}

.stat-trend.up {
  color: #27ae60;
  background: rgba(39, 174, 96, 0.1);
}

.stat-trend.down {
  color: #e74c3c;
  background: rgba(231, 76, 60, 0.1);
}

.charts-section {
  display: grid;
  grid-template-columns: 2fr 1fr;
  gap: 2rem;
  margin-bottom: 3rem;
}

.chart-container {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.chart-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.chart-placeholder {
  height: 200px;
  display: flex;
  flex-direction: column;
  justify-content: space-between;
}

.chart-bars {
  display: flex;
  align-items: flex-end;
  gap: 1rem;
  height: 160px;
}

.bar {
  flex: 1;
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  border-radius: 4px 4px 0 0;
  transition: all 0.3s ease;
  animation: growUp 1s ease-out;
}

.bar:hover {
  opacity: 0.8;
  transform: scaleY(1.1);
}

@keyframes growUp {
  from { height: 0; }
  to { height: var(--height, 100%); }
}

.chart-labels {
  display: flex;
  justify-content: space-between;
  font-size: 0.8rem;
  color: #7f8c8d;
  margin-top: 0.5rem;
}

.pie-chart {
  height: 200px;
  display: flex;
  flex-direction: column;
  gap: 1rem;
  justify-content: center;
}

.pie-segment {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 0.5rem;
  border-radius: 8px;
  background: rgba(var(--color), 0.1);
}

.pie-segment::before {
  content: '';
  width: 20px;
  height: 20px;
  border-radius: 50%;
  background: var(--color);
}

.pie-label {
  font-weight: 500;
  color: #2c3e50;
}

.activity-section {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
}

.section-title {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.activity-list {
  display: flex;
  flex-direction: column;
  gap: 1rem;
}

.activity-item {
  display: flex;
  align-items: center;
  gap: 1rem;
  padding: 1rem;
  border-radius: 10px;
  transition: background-color 0.3s ease;
}

.activity-item:hover {
  background: #f8f9fa;
}

.activity-icon {
  width: 40px;
  height: 40px;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  font-size: 1.2rem;
}

.activity-icon.user {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.activity-icon.data {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.activity-icon.system {
  background: linear-gradient(135deg, #4facfe 0%, #00f2fe 100%);
}

.activity-icon.warning {
  background: linear-gradient(135deg, #fa709a 0%, #fee140 100%);
}

.activity-icon.success {
  background: linear-gradient(135deg, #a8edea 0%, #fed6e3 100%);
}

.activity-content {
  flex: 1;
}

.activity-text {
  margin: 0 0 0.25rem 0;
  color: #2c3e50;
  font-weight: 500;
}

.activity-time {
  font-size: 0.8rem;
  color: #7f8c8d;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .charts-section {
    grid-template-columns: 1fr;
  }
  
  .stats-grid {
    grid-template-columns: 1fr;
  }
  
  .stat-card {
    padding: 1.5rem;
  }
  
  .page-title {
    font-size: 2rem;
  }
}
</style> 