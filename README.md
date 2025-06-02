# 现代化管理系统

这是一个前后端分离的现代化管理系统，采用Spring Boot + React技术栈开发。

## 技术栈

### 后端
- **Spring Boot 3.2.0** - 主框架
- **Spring Security** - 安全认证
- **Spring Data JPA** - 数据访问层
- **H2 Database** - 内存数据库
- **Maven** - 项目管理

### 前端
- **React 18** - 前端框架
- **Vite** - 构建工具
- **Ant Design** - UI组件库
- **React Router** - 路由管理
- **Axios** - HTTP客户端
- **ECharts** - 数据可视化图表库

## 功能特性

- ✅ 用户登录认证
- ✅ 现代化UI设计
- ✅ 响应式布局
- ✅ 安全的API接口
- ✅ 会话管理
- ✅ 多页面导航系统
- ✅ 仪表板 - 数据统计展示
- ✅ 数据图表 - 进度条和性能监控
- ✅ 用户管理 - 用户列表和操作
- ✅ 系统设置 - 配置管理和功能开关
- ✅ 侧边栏菜单导航
- ✅ 移动端抽屉菜单
- ✅ 动态内容切换

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

### 3. 系统状态检查

运行系统检查脚本，确认所有服务正常：
```bash
./check-system.sh
```

### 4. 访问系统

#### 本机访问
- **前端应用**: http://localhost:5173 (自动跳转到登录页面)
- **登录页面**: http://localhost:5173/login
- **管理首页**: http://localhost:5173/home (需要登录后访问)
- **后端API**: http://localhost:8080
- **H2数据库控制台**: http://localhost:8080/h2-console

#### 局域网访问
- **前端应用**: http://192.168.4.165:5173
- **登录页面**: http://192.168.4.165:5173/login
- **管理首页**: http://192.168.4.165:5173/home
- **后端API**: http://192.168.4.165:8080
- **H2数据库控制台**: http://192.168.4.165:8080/h2-console

> 📱 支持在局域网内的任何设备（手机、平板、电脑）上访问
> 
> 📋 详细说明请查看 `局域网访问指南.md`

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
├── frontend/                # React前端
│   ├── src/
│   │   ├── components/      # 组件
│   │   ├── pages/          # 页面
│   │   ├── services/       # API服务
│   │   └── utils/          # 工具函数
│   ├── package.json
│   └── vite.config.js
└── README.md
```

## API接口

### 认证接口
- `POST /api/auth/login` - 用户登录
- `POST /api/auth/logout` - 用户退出
- `GET /api/auth/me` - 获取当前用户信息

### 数据接口
- `GET /api/home` - 获取首页数据
- `GET /api/stats` - 获取统计数据

## 开发说明

### 后端开发
- 使用Spring Boot 3.x，支持Java 17+
- 采用分层架构：Controller -> Service -> Repository
- 使用Spring Security进行安全认证
- H2内存数据库，支持控制台访问：`http://localhost:8080/h2-console`

### 前端开发
- 使用React 18 + Vite构建
- Ant Design提供现代化UI组件
- 支持响应式设计，适配移动端
- 使用Axios进行API调用，支持拦截器

## 部署说明

### 生产环境部署
1. 后端打包：`./mvnw clean package`
2. 前端构建：`npm run build`
3. 部署jar文件和静态资源到服务器

### Docker部署
可以创建Dockerfile进行容器化部署。

## 许可证

MIT License 