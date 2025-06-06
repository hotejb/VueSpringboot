# JWT安全升级说明

## 🔒 安全性对比分析

### 原有Session认证的问题
❌ **不符合现代标准**：未使用JWT或OAuth 2.0  
❌ **CORS配置过于宽松**：允许所有来源（`*`）  
❌ **缺少API限流**：无防暴力破解机制  
❌ **Session固化攻击风险**  
❌ **扩展性差**：难以支持分布式部署  

### 新JWT认证的优势
✅ **符合现代标准**：使用JWT (JSON Web Tokens)  
✅ **无状态认证**：支持分布式部署  
✅ **访问令牌 + 刷新令牌**：安全的令牌管理  
✅ **API限流保护**：防暴力破解  
✅ **严格的CORS配置**：只允许指定域名  
✅ **完整的错误处理**：详细的安全日志  

## 🚀 新增功能

### 1. JWT令牌系统
- **访问令牌**：1小时有效期，用于API访问
- **刷新令牌**：7天有效期，用于获取新的访问令牌
- **令牌刷新机制**：无需重新登录即可延长会话

### 2. API限流保护
- **登录限流**：每分钟最多5次登录尝试
- **API限流**：每分钟最多100次API请求
- **IP级别限制**：基于客户端IP地址

### 3. 增强的安全配置
- **强密钥**：64字符随机密钥
- **HTTPS就绪**：生产环境HTTPS配置
- **严格CORS**：只允许指定的前端域名

## 📡 API端点

### 新JWT认证端点 (推荐)
```
POST /api/v2/auth/login     - JWT登录
POST /api/v2/auth/refresh   - 刷新令牌
POST /api/v2/auth/logout    - 退出登录
GET  /api/v2/auth/me        - 获取当前用户信息
```

### 原Session端点 (兼容性保留)
```
POST /api/auth/login        - Session登录
POST /api/auth/logout       - Session退出
GET  /api/auth/me           - Session用户信息
```

## 🔧 前端集成示例

### JWT认证请求示例
```javascript
// 登录
const response = await fetch('/api/v2/auth/login', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    username: 'admin',
    password: '123456'
  })
});

const data = await response.json();
if (data.success) {
  localStorage.setItem('accessToken', data.data.accessToken);
  localStorage.setItem('refreshToken', data.data.refreshToken);
}

// API请求
const apiResponse = await fetch('/api/users', {
  headers: {
    'Authorization': `Bearer ${localStorage.getItem('accessToken')}`
  }
});

// 令牌刷新
const refreshResponse = await fetch('/api/v2/auth/refresh', {
  method: 'POST',
  headers: {
    'Content-Type': 'application/json'
  },
  body: JSON.stringify({
    refreshToken: localStorage.getItem('refreshToken')
  })
});
```

## 🛡️ 安全最佳实践

### 1. 令牌存储
- **访问令牌**：存储在内存或sessionStorage（推荐）
- **刷新令牌**：存储在httpOnly cookie（最安全）或localStorage

### 2. 生产环境配置
```yaml
jwt:
  secret: ${JWT_SECRET}  # 环境变量
  expiration: 900000     # 15分钟
  
spring:
  servlet:
    session:
      cookie:
        secure: true     # 强制HTTPS
        http-only: true
```

### 3. HTTPS配置
```yaml
server:
  ssl:
    enabled: true
    key-store: classpath:keystore.p12
    key-store-password: ${SSL_PASSWORD}
    key-store-type: PKCS12
```

## 📊 性能对比

| 特性 | Session认证 | JWT认证 |
|------|-------------|---------|
| 服务器状态 | 有状态 | 无状态 |
| 扩展性 | 差 | 优秀 |
| 安全性 | 中等 | 高 |
| 现代标准 | 不符合 | 符合 |
| 移动端支持 | 一般 | 优秀 |

## 🔄 迁移建议

### 阶段1：并行运行
- 保持原有Session端点
- 新增JWT端点
- 前端逐步迁移

### 阶段2：完全迁移
- 前端完全使用JWT
- 移除Session端点
- 优化性能

### 阶段3：生产优化
- 启用HTTPS
- 配置Redis存储
- 监控和日志

## 🚨 注意事项

1. **密钥安全**：生产环境必须使用环境变量
2. **HTTPS强制**：生产环境必须启用HTTPS
3. **令牌黑名单**：考虑实现令牌黑名单机制
4. **监控告警**：配置安全事件监控
5. **定期轮换**：定期更换JWT密钥 