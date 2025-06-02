#!/bin/bash

echo "🔍 系统状态检查"
echo "=================="

# 检查Java环境
echo "☕ Java环境检查:"
if command -v java &> /dev/null; then
    java -version
    echo "✅ Java环境正常"
else
    echo "❌ Java未安装或未配置"
fi

echo ""

# 检查Node.js环境
echo "📦 Node.js环境检查:"
if command -v node &> /dev/null; then
    echo "Node.js版本: $(node -v)"
    echo "npm版本: $(npm -v)"
    echo "npm镜像源: $(npm config get registry)"
    echo "✅ Node.js环境正常"
else
    echo "❌ Node.js未安装"
fi

echo ""

# 检查端口占用
echo "🔌 端口状态检查:"
if lsof -i :8080 &> /dev/null; then
    echo "✅ 后端服务 (8080端口) 正在运行"
    echo "   进程信息: $(lsof -i :8080 | tail -1)"
else
    echo "❌ 后端服务 (8080端口) 未运行"
fi

if lsof -i :5173 &> /dev/null; then
    echo "✅ 前端服务 (5173端口) 正在运行"
    echo "   进程信息: $(lsof -i :5173 | tail -1)"
elif lsof -i :5174 &> /dev/null; then
    echo "✅ 前端服务 (5174端口) 正在运行"
    echo "   进程信息: $(lsof -i :5174 | tail -1)"
elif lsof -i :3000 &> /dev/null; then
    echo "✅ 前端服务 (3000端口) 正在运行"
    echo "   进程信息: $(lsof -i :3000 | tail -1)"
else
    echo "❌ 前端服务未运行"
fi

echo ""

# 检查API连通性
echo "🌐 API连通性检查:"
if curl -s http://localhost:8080/api/home &> /dev/null; then
    echo "✅ 后端API响应正常"
    echo "   API响应: $(curl -s http://localhost:8080/api/home | jq -r '.message' 2>/dev/null || echo '数据获取成功')"
else
    echo "❌ 后端API无响应"
fi

if curl -s http://localhost:5173 &> /dev/null; then
    echo "✅ 前端页面响应正常 (5173端口)"
elif curl -s http://localhost:5174 &> /dev/null; then
    echo "✅ 前端页面响应正常 (5174端口)"
elif curl -s http://localhost:3000 &> /dev/null; then
    echo "✅ 前端页面响应正常 (3000端口)"
else
    echo "❌ 前端页面无响应"
fi

echo ""
echo "🎯 访问地址:"
echo "📍 本机访问:"
if lsof -i :5173 &> /dev/null; then
    echo "   前端应用: http://localhost:5173"
elif lsof -i :5174 &> /dev/null; then
    echo "   前端应用: http://localhost:5174"
else
    echo "   前端应用: http://localhost:3000 (默认)"
fi
echo "   后端API: http://localhost:8080"
echo "   H2控制台: http://localhost:8080/h2-console"
echo ""
echo "🌐 局域网访问:"
LOCAL_IP=$(ifconfig | grep "inet " | grep -v 127.0.0.1 | awk '{print $2}' | head -1)
if [ ! -z "$LOCAL_IP" ]; then
    echo "   前端应用: http://$LOCAL_IP:5173"
    echo "   后端API: http://$LOCAL_IP:8080"
    echo "   H2控制台: http://$LOCAL_IP:8080/h2-console"
else
    echo "   无法获取局域网IP地址"
fi
echo ""
echo "👤 测试账号:"
echo "   管理员: admin / 123456"
echo "   普通用户: user / 123456" 