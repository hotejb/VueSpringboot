<template>
  <div class="about">
    <div class="about-hero">
      <div class="hero-content">
        <h1 class="hero-title">ℹ️ 关于我们</h1>
        <p class="hero-subtitle">了解我们的技术栈和项目愿景</p>
      </div>
    </div>

    <div class="about-content">
      <div class="tech-stack">
        <h2 class="section-title">🛠️ 技术栈</h2>
        <div class="tech-grid">
          <div class="tech-card frontend">
            <div class="tech-icon">🎨</div>
            <h3>前端技术</h3>
            <ul>
              <li>Vue 3 - 渐进式JavaScript框架</li>
              <li>Vite - 极速构建工具</li>
              <li>Vue Router - 官方路由管理器</li>
              <li>Axios - HTTP客户端</li>
              <li>CSS3 - 现代样式设计</li>
            </ul>
          </div>
          
          <div class="tech-card backend">
            <div class="tech-icon">⚙️</div>
            <h3>后端技术</h3>
            <ul>
              <li>Spring Boot - Java企业级框架</li>
              <li>Spring Security - 安全框架</li>
              <li>Spring Data JPA - 数据访问层</li>
              <li>H2 Database - 内存数据库</li>
              <li>Maven - 项目管理工具</li>
            </ul>
          </div>
        </div>
      </div>

      <div class="features">
        <h2 class="section-title">✨ 项目特色</h2>
        <div class="features-grid">
          <div class="feature-item">
            <div class="feature-icon">🚀</div>
            <h4>高性能</h4>
            <p>采用现代化技术栈，确保应用的高性能和快速响应</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🔒</div>
            <h4>安全可靠</h4>
            <p>集成Spring Security，提供完整的认证和授权机制</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">📱</div>
            <h4>响应式设计</h4>
            <p>完美适配各种设备，提供一致的用户体验</p>
          </div>
          <div class="feature-item">
            <div class="feature-icon">🎯</div>
            <h4>易于扩展</h4>
            <p>模块化架构设计，便于功能扩展和维护</p>
          </div>
        </div>
      </div>

      <div class="team">
        <h2 class="section-title">👥 开发团队</h2>
        <div class="team-info">
          <p>本项目由经验丰富的全栈开发团队打造，致力于提供高质量的Web应用解决方案。</p>
          <div class="contact-info">
            <div class="contact-item">
              <span class="contact-icon">📧</span>
              <span>邮箱: contact@example.com</span>
            </div>
            <div class="contact-item">
              <span class="contact-icon">🌐</span>
              <span>网站: https://example.com</span>
            </div>
            <div class="contact-item">
              <span class="contact-icon">📱</span>
              <span>电话: +86 123-4567-8900</span>
            </div>
          </div>
        </div>
      </div>
    </div>
  </div>
</template>

<script>
export default {
  name: 'About'
}
</script>

<style scoped>
.about {
  padding: 0;
}

.about-hero {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
  color: white;
  padding: 4rem 0;
  text-align: center;
}

.hero-content {
  max-width: 800px;
  margin: 0 auto;
  padding: 0 2rem;
}

.hero-title {
  font-size: 3rem;
  font-weight: 700;
  margin-bottom: 1rem;
}

.hero-subtitle {
  font-size: 1.2rem;
  opacity: 0.9;
}

.about-content {
  max-width: 1200px;
  margin: 0 auto;
  padding: 4rem 2rem;
}

.section-title {
  font-size: 2rem;
  font-weight: 600;
  color: #2c3e50;
  text-align: center;
  margin-bottom: 3rem;
}

.tech-stack {
  margin-bottom: 4rem;
}

.tech-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(400px, 1fr));
  gap: 2rem;
}

.tech-card {
  background: white;
  padding: 2rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  position: relative;
  overflow: hidden;
}

.tech-card::before {
  content: '';
  position: absolute;
  top: 0;
  left: 0;
  width: 100%;
  height: 4px;
}

.tech-card.frontend::before {
  background: linear-gradient(135deg, #667eea 0%, #764ba2 100%);
}

.tech-card.backend::before {
  background: linear-gradient(135deg, #f093fb 0%, #f5576c 100%);
}

.tech-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.tech-card h3 {
  font-size: 1.5rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1.5rem;
}

.tech-card ul {
  list-style: none;
  padding: 0;
}

.tech-card li {
  padding: 0.5rem 0;
  color: #7f8c8d;
  position: relative;
  padding-left: 1.5rem;
}

.tech-card li::before {
  content: '✓';
  position: absolute;
  left: 0;
  color: #27ae60;
  font-weight: bold;
}

.features {
  margin-bottom: 4rem;
}

.features-grid {
  display: grid;
  grid-template-columns: repeat(auto-fit, minmax(250px, 1fr));
  gap: 2rem;
}

.feature-item {
  text-align: center;
  padding: 2rem;
  background: white;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  transition: transform 0.3s ease;
}

.feature-item:hover {
  transform: translateY(-5px);
}

.feature-icon {
  font-size: 3rem;
  margin-bottom: 1rem;
}

.feature-item h4 {
  font-size: 1.3rem;
  font-weight: 600;
  color: #2c3e50;
  margin-bottom: 1rem;
}

.feature-item p {
  color: #7f8c8d;
  line-height: 1.6;
}

.team {
  background: white;
  padding: 3rem;
  border-radius: 15px;
  box-shadow: 0 5px 15px rgba(0,0,0,0.1);
  text-align: center;
}

.team-info p {
  font-size: 1.1rem;
  color: #7f8c8d;
  line-height: 1.6;
  margin-bottom: 2rem;
  max-width: 600px;
  margin-left: auto;
  margin-right: auto;
}

.contact-info {
  display: flex;
  justify-content: center;
  gap: 2rem;
  flex-wrap: wrap;
}

.contact-item {
  display: flex;
  align-items: center;
  gap: 0.5rem;
  color: #2c3e50;
  font-weight: 500;
}

.contact-icon {
  font-size: 1.2rem;
}

/* 响应式设计 */
@media (max-width: 768px) {
  .hero-title {
    font-size: 2rem;
  }
  
  .about-content {
    padding: 2rem 1rem;
  }
  
  .tech-grid {
    grid-template-columns: 1fr;
  }
  
  .features-grid {
    grid-template-columns: 1fr;
  }
  
  .contact-info {
    flex-direction: column;
    gap: 1rem;
  }
  
  .team {
    padding: 2rem;
  }
}
</style> 