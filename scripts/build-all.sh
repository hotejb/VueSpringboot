#!/bin/bash

echo "🏗️ 构建VueSpring Admin项目"
echo "=========================="

# 构建后端
echo "📦 构建后端..."
cd backend
mvn clean package -DskipTests
if [ $? -ne 0 ]; then
    echo "❌ 后端构建失败"
    exit 1
fi
cd ..

# 构建前端
echo "📦 构建前端..."
cd frontend
npm run build
if [ $? -ne 0 ]; then
    echo "❌ 前端构建失败"
    exit 1
fi
cd ..

echo "✅ 项目构建完成！"
echo ""
echo "📁 构建产物："
echo "   backend/target/*.jar"
echo "   frontend/dist/"