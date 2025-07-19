#!/bin/bash

echo "🚀 启动后端服务..."
echo "📍 当前目录: $(pwd)"

# 进入后端目录
cd backend

# 设置Java环境变量
export PATH="/opt/homebrew/opt/openjdk@17/bin:$PATH"
export JAVA_HOME="/opt/homebrew/opt/openjdk@17"

# 检查环境变量
if [ -z "$SPRING_PROFILES_ACTIVE" ]; then
    export SPRING_PROFILES_ACTIVE=dev
    echo "🔧 设置环境为开发模式: $SPRING_PROFILES_ACTIVE"
fi

# 显示Java版本
echo "☕ Java版本:"
java -version

echo ""
echo "🔧 启动Spring Boot应用..."
echo "📡 后端服务将运行在: http://localhost:8080"
echo "📚 API文档: http://localhost:8080/swagger-ui/index.html"
echo "📊 监控端点: http://localhost:8080/actuator"

if [ "$SPRING_PROFILES_ACTIVE" = "dev" ]; then
    echo "🗄️  H2数据库控制台: http://localhost:8080/h2-console"
fi

echo "👤 测试账号: admin/123456 或 user/123456"
echo ""

# 启动Spring Boot应用
mvn spring-boot:run -Dspring-boot.run.profiles=$SPRING_PROFILES_ACTIVE 