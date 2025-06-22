#!/bin/bash

echo "=== JWT权限控制功能测试 ==="
echo

# 测试管理员登录
echo "1. 测试管理员登录..."
ADMIN_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"admin","password":"123456"}')

if echo "$ADMIN_RESPONSE" | grep -q '"success":true'; then
  echo "✅ 管理员登录成功"
  ADMIN_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.accessToken')
else
  echo "❌ 管理员登录失败"
  echo "$ADMIN_RESPONSE"
  exit 1
fi

# 测试普通用户登录
echo
echo "2. 测试普通用户登录..."
USER_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/auth/login \
  -H "Content-Type: application/json" \
  -d '{"username":"zhangsan","password":"123456"}')

if echo "$USER_RESPONSE" | grep -q '"success":true'; then
  echo "✅ 普通用户登录成功"
  USER_TOKEN=$(echo "$USER_RESPONSE" | jq -r '.data.accessToken')
else
  echo "❌ 普通用户登录失败"
  echo "$USER_RESPONSE"
  exit 1
fi

# 测试管理员权限
echo
echo "3. 测试管理员权限..."

# 管理员访问用户管理
USER_API_ADMIN=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $ADMIN_TOKEN" -o /dev/null)
if [ "$USER_API_ADMIN" = "200" ]; then
  echo "✅ 管理员可以访问用户管理API"
else
  echo "❌ 管理员无法访问用户管理API (HTTP $USER_API_ADMIN)"
fi

# 管理员访问权限管理
PERM_API_ADMIN=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/permissions \
  -H "Authorization: Bearer $ADMIN_TOKEN" -o /dev/null)
if [ "$PERM_API_ADMIN" = "200" ]; then
  echo "✅ 管理员可以访问权限管理API"
else
  echo "❌ 管理员无法访问权限管理API (HTTP $PERM_API_ADMIN)"
fi

# 管理员访问角色管理
ROLE_API_ADMIN=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer $ADMIN_TOKEN" -o /dev/null)
if [ "$ROLE_API_ADMIN" = "200" ]; then
  echo "✅ 管理员可以访问角色管理API"
else
  echo "❌ 管理员无法访问角色管理API (HTTP $ROLE_API_ADMIN)"
fi

# 测试普通用户权限限制
echo
echo "4. 测试普通用户权限限制..."

# 普通用户访问用户管理（应该被拒绝）
USER_API_USER=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer $USER_TOKEN" -o /dev/null)
if [ "$USER_API_USER" = "403" ]; then
  echo "✅ 普通用户正确被拒绝访问用户管理API"
else
  echo "❌ 普通用户不应该能访问用户管理API (HTTP $USER_API_USER)"
fi

# 普通用户访问权限管理（应该被拒绝）
PERM_API_USER=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/permissions \
  -H "Authorization: Bearer $USER_TOKEN" -o /dev/null)
if [ "$PERM_API_USER" = "403" ]; then
  echo "✅ 普通用户正确被拒绝访问权限管理API"
else
  echo "❌ 普通用户不应该能访问权限管理API (HTTP $PERM_API_USER)"
fi

# 普通用户访问角色管理（应该被拒绝）
ROLE_API_USER=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/roles \
  -H "Authorization: Bearer $USER_TOKEN" -o /dev/null)
if [ "$ROLE_API_USER" = "403" ]; then
  echo "✅ 普通用户正确被拒绝访问角色管理API"
else
  echo "❌ 普通用户不应该能访问角色管理API (HTTP $ROLE_API_USER)"
fi

# 测试JWT令牌刷新
echo
echo "5. 测试JWT令牌刷新..."
REFRESH_TOKEN=$(echo "$ADMIN_RESPONSE" | jq -r '.data.refreshToken')
REFRESH_RESPONSE=$(curl -s -X POST http://localhost:8080/api/v2/auth/refresh \
  -H "Content-Type: application/json" \
  -d "{\"refreshToken\":\"$REFRESH_TOKEN\"}")

if echo "$REFRESH_RESPONSE" | grep -q '"success":true'; then
  echo "✅ JWT令牌刷新成功"
else
  echo "❌ JWT令牌刷新失败"
  echo "$REFRESH_RESPONSE"
fi

# 测试无效令牌处理
echo
echo "6. 测试无效令牌处理..."
INVALID_TOKEN_RESPONSE=$(curl -s -w "%{http_code}" -X GET http://localhost:8080/api/users \
  -H "Authorization: Bearer invalid_token" -o /dev/null)
if [ "$INVALID_TOKEN_RESPONSE" = "403" ] || [ "$INVALID_TOKEN_RESPONSE" = "401" ]; then
  echo "✅ 无效令牌正确被拒绝"
else
  echo "❌ 无效令牌处理异常 (HTTP $INVALID_TOKEN_RESPONSE)"
fi

echo
echo "=== 测试完成 ===" 