#!/bin/bash

echo "🚀 启动后端服务..."
echo "📍 当前目录: $(pwd)"

# 进入后端目录
cd backend

# 设置Java环境变量
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"

# 显示Java版本
echo "☕ Java版本:"
java -version

echo ""
echo "🔧 启动Spring Boot应用..."
echo "📡 后端服务将运行在: http://localhost:8080"
echo "🗄️  H2数据库控制台: http://localhost:8080/h2-console"
echo "👤 测试账号: admin/123456 或 user/123456"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run 