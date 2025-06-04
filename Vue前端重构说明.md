# Vue 前端重构说明

## 🔄 重构概述

本次重构将前端框架从 **React** 迁移到 **Vue 3**，保持与SpringBoot后端的完全兼容性。

## 📋 重构内容

### 🗑️ 删除的React组件
- `frontend/src/App.jsx`
- `frontend/src/pages/Home.jsx`
- `frontend/src/pages/Login.jsx`
- `frontend/src/main.jsx`
- React相关配置文件

### ✨ 新增的Vue组件
- `frontend/src/App.vue` - 主应用组件
- `frontend/src/views/Home.vue` - 首页组件
- `frontend/src/views/Login.vue` - 登录页面组件
- `frontend/src/main.js` - Vue应用入口
- `frontend/src/router/index.js` - Vue Router配置
- `frontend/src/style.css` - 全局样式

## 🛠️ 技术栈更新

### 前端技术栈
- **框架**: Vue 3 (Composition API)
- **构建工具**: Vite
- **路由**: Vue Router 4
- **HTTP客户端**: Axios
- **样式**: 原生CSS + 响应式设计

### 后端技术栈 (保持不变)
- **框架**: SpringBoot 2.7+
- **安全**: Spring Security
- **数据库**: H2 (内存数据库)
- **API**: RESTful API

## 🎨 界面特性

### 🏠 首页 (Home.vue)
- 现代化的渐变背景设计
- 项目特性展示卡片
- API连接测试功能
- 响应式布局设计

### 🔐 登录页面 (Login.vue)
- 优雅的登录表单设计
- 实时表单验证
- 错误和成功消息提示
- 演示账号信息显示

### 🧭 导航栏
- 渐变背景设计
- 路由高亮显示
- 响应式导航菜单

## 🚀 启动方式

### 前端启动
```bash
# 方式1: 使用启动脚本
./start-frontend.sh

# 方式2: 手动启动
cd frontend
npm install  # 首次运行需要安装依赖
npm run dev
```

### 后端启动
```bash
# 使用启动脚本
./start-backend.sh

# 或手动启动
cd backend
mvn spring-boot:run
```

## 🌐 访问地址

- **前端地址**: http://localhost:3000
- **后端地址**: http://localhost:8080
- **API文档**: http://localhost:8080/swagger-ui.html (如果配置了Swagger)

## 👤 测试账号

- **用户名**: admin
- **密码**: password

## 📁 项目结构

```
frontend/
├── src/
│   ├── components/          # 可复用组件 (预留)
│   ├── views/              # 页面组件
│   │   ├── Home.vue        # 首页
│   │   └── Login.vue       # 登录页
│   ├── router/             # 路由配置
│   │   └── index.js        # Vue Router配置
│   ├── services/           # API服务
│   │   └── api.js          # HTTP请求封装
│   ├── App.vue             # 主应用组件
│   ├── main.js             # 应用入口
│   └── style.css           # 全局样式
├── index.html              # HTML模板
├── vite.config.js          # Vite配置
└── package.json            # 项目依赖
```

## 🔧 配置说明

### Vite配置 (vite.config.js)
- Vue插件配置
- 开发服务器端口: 3000
- API代理配置: `/api` -> `http://localhost:8080`

### Vue Router配置
- 使用HTML5 History模式
- 路由懒加载 (可扩展)

### Axios配置
- 自动添加Authorization头
- 响应拦截器处理401错误
- 统一错误处理

## 🎯 API兼容性

Vue前端完全兼容现有的SpringBoot API：

- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户登出
- `GET /api/home/welcome` - 获取欢迎消息

## 📱 响应式设计

- 支持桌面端、平板和移动端
- 自适应布局和字体大小
- 优化的触摸交互体验

## 🔮 后续扩展

### 可添加的功能
- 用户注册页面
- 个人资料管理
- 数据表格组件
- 图表展示组件
- 国际化支持 (i18n)
- 主题切换功能

### 可集成的库
- Element Plus (UI组件库)
- Pinia (状态管理)
- VueUse (组合式工具库)
- Chart.js (图表库)

## 📝 更新日志

- **2024-06-04**: 完成React到Vue 3的前端重构
- 保持与SpringBoot后端的完全兼容性
- 优化用户界面设计和用户体验
- 添加响应式设计支持 