#!/bin/bash

echo "🎨 启动Vue前端服务..."
echo "📍 当前目录: $(pwd)"

# 进入前端目录
cd frontend

# 检查是否已安装依赖
if [ ! -d "node_modules" ]; then
    echo "📦 安装前端依赖..."
    npm install
fi

# 显示npm配置
echo "📦 npm镜像源:"
npm config get registry

echo ""
echo "🔧 启动Vue开发服务器..."
echo "🌐 前端服务将运行在: http://localhost:3000"
echo "👤 测试账号: admin/password"
echo ""

# 启动开发服务器
npm run dev 