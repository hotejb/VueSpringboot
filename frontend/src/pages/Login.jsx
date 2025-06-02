import React, { useState } from 'react';
import { Form, Input, Button, Card, Typography, message, Space, Alert } from 'antd';
import { UserOutlined, LockOutlined, LoginOutlined } from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { authAPI } from '../services/api';

const { Title, Text } = Typography;

const Login = () => {
  const [loading, setLoading] = useState(false);
  const navigate = useNavigate();

  const onFinish = async (values) => {
    setLoading(true);
    try {
      const response = await authAPI.login(values);
      if (response.success) {
        message.success(response.message || '登录成功');
        navigate('/home');
      } else {
        message.error(response.message || '登录失败');
      }
    } catch (error) {
      message.error('登录失败，请检查网络连接');
    } finally {
      setLoading(false);
    }
  };

  return (
    <div style={{
      minHeight: '100vh',
      background: 'linear-gradient(135deg, #667eea 0%, #764ba2 50%, #f093fb 100%)',
      display: 'flex',
      alignItems: 'center',
      justifyContent: 'center',
      padding: '20px',
      position: 'relative',
      overflow: 'hidden'
    }}>
      {/* 背景动画元素 */}
      <div style={{
        position: 'absolute',
        top: '10%',
        left: '10%',
        width: '100px',
        height: '100px',
        background: 'rgba(255,255,255,0.1)',
        borderRadius: '50%',
        animation: 'float 6s ease-in-out infinite'
      }} />
      <div style={{
        position: 'absolute',
        top: '60%',
        right: '15%',
        width: '150px',
        height: '150px',
        background: 'rgba(255,255,255,0.05)',
        borderRadius: '50%',
        animation: 'float 8s ease-in-out infinite reverse'
      }} />
      <div style={{
        position: 'absolute',
        bottom: '20%',
        left: '20%',
        width: '80px',
        height: '80px',
        background: 'rgba(255,255,255,0.08)',
        borderRadius: '50%',
        animation: 'float 7s ease-in-out infinite'
      }} />
      
      <Card
        className="cool-card"
        style={{
          width: '100%',
          maxWidth: '400px',
          borderRadius: '16px',
          boxShadow: '0 20px 60px rgba(0,0,0,0.2)',
          border: 'none',
          backdropFilter: 'blur(20px)',
          background: 'rgba(255,255,255,0.95)',
          animation: 'fadeInUp 0.8s ease-out'
        }}
        bodyStyle={{ padding: '40px' }}
      >
        <div style={{ textAlign: 'center', marginBottom: '32px' }}>
          <Title level={2} style={{ color: '#333', marginBottom: '8px' }}>
            欢迎登录
          </Title>
          <Text type="secondary">现代化管理系统</Text>
        </div>

        <Alert
          message="测试账号"
          description={
            <div>
              <div>管理员: admin / 123456</div>
              <div>普通用户: user / 123456</div>
            </div>
          }
          type="info"
          showIcon
          style={{ marginBottom: '24px' }}
        />

        <Form
          name="login"
          onFinish={onFinish}
          autoComplete="off"
          size="large"
        >
          <Form.Item
            name="username"
            rules={[
              { required: true, message: '请输入用户名!' },
              { min: 3, message: '用户名至少3个字符!' }
            ]}
          >
            <Input
              prefix={<UserOutlined />}
              placeholder="用户名"
              style={{ borderRadius: '8px' }}
            />
          </Form.Item>

          <Form.Item
            name="password"
            rules={[
              { required: true, message: '请输入密码!' },
              { min: 6, message: '密码至少6个字符!' }
            ]}
          >
            <Input.Password
              prefix={<LockOutlined />}
              placeholder="密码"
              style={{ borderRadius: '8px' }}
            />
          </Form.Item>

          <Form.Item>
            <Button
              type="primary"
              htmlType="submit"
              loading={loading}
              icon={<LoginOutlined />}
              className="cool-button"
              style={{
                width: '100%',
                height: '48px',
                borderRadius: '12px',
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                border: 'none',
                fontSize: '16px',
                fontWeight: '500',
                boxShadow: '0 4px 15px rgba(102, 126, 234, 0.4)'
              }}
            >
              {loading ? '登录中...' : '登录'}
            </Button>
          </Form.Item>
        </Form>

        <div style={{ textAlign: 'center', marginTop: '24px' }}>
          <Space direction="vertical" size="small">
            <Text type="secondary" style={{ fontSize: '12px' }}>
              © 2024 现代化管理系统
            </Text>
            <Text type="secondary" style={{ fontSize: '12px' }}>
              Powered by React + Spring Boot
            </Text>
          </Space>
        </div>
      </Card>
    </div>
  );
};

export default Login; 