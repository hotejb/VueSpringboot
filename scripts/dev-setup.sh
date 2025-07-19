#!/bin/bash

echo "🔧 VueSpring Admin 开发环境设置"
echo "=================================="

# 检查Node.js
if ! command -v node &> /dev/null; then
    echo "❌ Node.js未安装，请先安装Node.js 18+"
    exit 1
fi

# 检查Java
if ! command -v java &> /dev/null; then
    echo "❌ Java未安装，请先安装Java 17+"
    exit 1
fi

# 检查Maven
if ! command -v mvn &> /dev/null; then
    echo "❌ Maven未安装，请先安装Maven"
    exit 1
fi

echo "✅ 环境检查通过"
echo ""

# 安装前端依赖
echo "📦 安装前端依赖..."
cd frontend
npm install
cd ..

echo "✅ 前端依赖安装完成"
echo ""

# 启动开发环境数据库
echo "🗄️ 启动开发环境数据库..."
docker-compose -f docker-compose.dev.yml up -d

echo "✅ 开发环境设置完成！"
echo ""
echo "🚀 现在可以运行以下命令启动服务："
echo "   ./start-backend.sh  # 启动后端"
echo "   ./start-frontend.sh # 启动前端"
echo ""
echo "🔗 或者使用Docker运行完整环境："
echo "   docker-compose up -d"