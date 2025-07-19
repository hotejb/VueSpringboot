#!/bin/bash

echo "🐳 构建Docker镜像"
echo "=================="

# 构建后端镜像
echo "📦 构建后端Docker镜像..."
docker build -t vuespring-admin-backend:latest ./backend
if [ $? -ne 0 ]; then
    echo "❌ 后端镜像构建失败"
    exit 1
fi

# 构建前端镜像
echo "📦 构建前端Docker镜像..."
docker build -t vuespring-admin-frontend:latest ./frontend
if [ $? -ne 0 ]; then
    echo "❌ 前端镜像构建失败"
    exit 1
fi

echo "✅ Docker镜像构建完成！"
echo ""
echo "🐳 可用镜像："
echo "   vuespring-admin-backend:latest"
echo "   vuespring-admin-frontend:latest"
echo ""
echo "🚀 启动完整环境："
echo "   docker-compose up -d"