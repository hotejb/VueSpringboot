# VueSpring Admin

**VueSpring Admin** - A modern full-stack admin dashboard built with Vue 3 and Spring Boot.

这是一个前后端分离的现代化管理系统，采用Spring Boot + Vue 3技术栈开发。

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问层
- **H2 Database** - 内存数据库
- **Maven** - 项目管理

### 前端
- **Vue 3** - 前端框架 (Composition API)
- **Vite** - 构建工具
- **Vue Router 4** - 路由管理
- **Axios** - HTTP客户端
- **原生CSS** - 样式设计 (响应式布局)

## 功能特性

- ✅ 用户登录认证
- ✅ 现代化UI设计
- ✅ 响应式布局
- ✅ 安全的API接口
- ✅ 会话管理
- ✅ 多页面导航系统
- ✅ 仪表板 - 数据统计展示和图表
- ✅ 用户管理 - 用户列表和操作
- ✅ 系统设置 - 多标签页配置管理
- ✅ 关于页面 - 项目信息展示
- ✅ 动态导航栏 - 下拉菜单和用户状态
- ✅ 移动端响应式设计
- ✅ 现代化UI设计 - 渐变背景和动画效果

## 环境配置

### Java环境配置
系统已自动配置Java环境变量到 `~/.zshrc`：
```bash
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"
```

### npm镜像源配置
已配置为淘宝镜像源，提升下载速度：
```bash
npm config set registry https://registry.npmmirror.com
```

## 快速开始

### 方式一：使用启动脚本（推荐）

**启动后端服务：**
```bash
./start-backend.sh
```

**启动前端服务：**
```bash
./start-frontend.sh
```

### 方式二：手动启动

**1. 启动后端服务**
```bash
cd backend
mvn spring-boot:run
```

**2. 启动前端服务**
```bash
cd frontend
npm install
npm run dev
```

### 4. 访问系统

#### 访问地址
- **前端应用**: http://localhost:3000 (自动跳转到登录页面)
- **登录页面**: http://localhost:3000/login
- **管理首页**: http://localhost:3000/ (需要登录后访问)
- **后端API**: http://localhost:8080
- **H2数据库控制台**: http://localhost:8080/h2-console

> 📱 支持在局域网内的任何设备（手机、平板、电脑）上访问

## 测试账号

系统已预置以下测试账号：

- **管理员账号**: `admin` / `123456`
- **普通用户**: `user` / `123456`

## 项目结构

```
├── backend/                 # Spring Boot后端
│   ├── src/main/java/
│   │   └── com/example/demo/
│   │       ├── controller/  # 控制器层
│   │       ├── service/     # 服务层
│   │       ├── repository/  # 数据访问层
│   │       ├── entity/      # 实体类
│   │       ├── dto/         # 数据传输对象
│   │       └── config/      # 配置类
│   └── src/main/resources/
│       └── application.yml  # 应用配置
├── frontend/                # Vue 3前端
│   ├── src/
│   │   ├── components/      # 可复用组件
│   │   ├── views/          # 页面组件
│   │   ├── services/       # API服务
│   │   ├── router/         # 路由配置
│   │   ├── App.vue         # 主应用组件
│   │   ├── main.js         # 应用入口
│   │   └── style.css       # 全局样式
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## API接口

### JWT认证接口 (推荐)
- `POST /api/v2/auth/login` - JWT登录 (支持访问令牌+刷新令牌)
- `POST /api/v2/auth/refresh` - 刷新访问令牌
- `POST /api/v2/auth/logout` - 退出登录
- `GET /api/v2/auth/me` - 获取当前用户信息

### Session认证接口 (兼容)
- `POST /api/auth/login` - Session登录
- `POST /api/auth/logout` - Session退出
- `GET /api/auth/me` - 获取用户信息

### 数据接口
- `GET /api/home` - 获取首页数据
- `GET /api/stats` - 获取统计数据

### 安全特性
- ✅ JWT令牌认证 (访问令牌1小时 + 刷新令牌7天)
- ✅ API限流保护 (登录5次/分钟，API 100次/分钟)
- ✅ BCrypt密码加密
- ✅ 严格CORS配置

## 开发说明

### 后端开发
- 使用Spring Boot 3.x，支持Java 17+
- 采用分层架构：Controller -> Service -> Repository
- 使用Spring Security进行安全认证
- H2内存数据库，支持控制台访问：`http://localhost:8080/h2-console`

### 前端开发
- 使用Vue 3 + Vite构建
- Composition API提供现代化开发体验
- 原生CSS实现响应式设计，适配移动端
- 使用Axios进行API调用，支持拦截器
- Vue Router 4提供路由管理

## 部署说明

### 生产环境部署
1. 后端打包：`./mvnw clean package`
2. 前端构建：`npm run build`
3. 部署jar文件和静态资源到服务器
