import React, { useState, useEffect } from 'react';
import { 
  Layout, 
  Card, 
  Row, 
  Col, 
  Statistic, 
  Typography, 
  Space, 
  Button, 
  message,
  Avatar,
  List,
  Menu,
  Drawer,
  Table,
  Tag,
  Progress,
  Divider,
  Switch,
  Form,
  Input,
  Select
} from 'antd';
import { 
  UserOutlined, 
  ShoppingCartOutlined, 
  DollarOutlined, 
  TeamOutlined,
  LogoutOutlined,
  HomeOutlined,
  ClockCircleOutlined,
  MenuOutlined,
  DashboardOutlined,
  BarChartOutlined,
  SettingOutlined,
  EyeOutlined,
  UserAddOutlined,
  ThunderboltOutlined
} from '@ant-design/icons';
import { useNavigate } from 'react-router-dom';
import { authAPI, homeAPI } from '../services/api';

const { Header, Content, Sider } = Layout;
const { Title, Text, Paragraph } = Typography;

const Home = () => {
  const [user, setUser] = useState(null);
  const [homeData, setHomeData] = useState(null);
  const [stats, setStats] = useState(null);
  const [loading, setLoading] = useState(true);
  const [drawerVisible, setDrawerVisible] = useState(false);
  const [selectedMenu, setSelectedMenu] = useState('dashboard');
  const [siderCollapsed, setSiderCollapsed] = useState(false);
  const navigate = useNavigate();

  useEffect(() => {
    loadData();
  }, []);

  const loadData = async () => {
    try {
      const [userResponse, homeResponse, statsResponse] = await Promise.all([
        authAPI.getCurrentUser(),
        homeAPI.getHomeData(),
        homeAPI.getStats()
      ]);

      if (userResponse.success) {
        setUser(userResponse.data);
      }
      if (homeResponse.success) {
        setHomeData(homeResponse.data);
      }
      if (statsResponse.success) {
        setStats(statsResponse.data);
      }
    } catch (error) {
      message.error('加载数据失败');
    } finally {
      setLoading(false);
    }
  };

  const handleLogout = async () => {
    try {
      const response = await authAPI.logout();
      if (response.success) {
        message.success(response.message);
        navigate('/login');
      }
    } catch (error) {
      message.error('退出登录失败');
    }
  };

  const formatCurrency = (value) => {
    return new Intl.NumberFormat('zh-CN', {
      style: 'currency',
      currency: 'CNY'
    }).format(value);
  };

  const handleMenuClick = (e) => {
    setSelectedMenu(e.key);
    setDrawerVisible(false); // 移动端点击后关闭抽屉
  };

  // 渲染仪表板内容
  const renderDashboard = () => (
    <>
      {/* 欢迎区域 */}
      <Card 
        className="welcome-card"
        style={{ 
          marginBottom: '24px',
          border: 'none',
          color: 'white'
        }}
        bodyStyle={{ padding: '32px' }}
      >
        <Row align="middle" gutter={[16, 16]}>
          <Col xs={24} sm={16} md={18}>
            <Title level={2} style={{ color: 'white', margin: 0, fontSize: 'clamp(20px, 4vw, 32px)' }}>
              {homeData?.title || '欢迎来到系统'}
            </Title>
            <Paragraph style={{ 
              color: 'rgba(255,255,255,0.8)', 
              fontSize: 'clamp(14px, 2.5vw, 16px)', 
              margin: '8px 0 0 0' 
            }}>
              {homeData?.description || '现代化的管理系统'}
            </Paragraph>
          </Col>
          <Col xs={24} sm={8} md={6}>
            <Space direction="vertical" align="end" style={{ width: '100%' }}>
              <Text style={{ color: 'rgba(255,255,255,0.8)', fontSize: '12px' }}>
                <ClockCircleOutlined /> 当前时间
              </Text>
              <Text style={{ color: 'white', fontSize: 'clamp(12px, 2vw, 16px)' }}>
                {homeData?.currentTime ? new Date(homeData.currentTime).toLocaleString('zh-CN') : ''}
              </Text>
            </Space>
          </Col>
        </Row>
      </Card>

      {/* 统计卡片 */}
      <Row gutter={[16, 16]} style={{ marginBottom: '24px' }}>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="总用户数"
              value={stats?.totalUsers || 0}
              prefix={<TeamOutlined className="cool-icon" style={{ color: '#1890ff' }} />}
              valueStyle={{ color: '#1890ff', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="活跃用户"
              value={stats?.activeUsers || 0}
              prefix={<UserOutlined className="cool-icon" style={{ color: '#52c41a' }} />}
              valueStyle={{ color: '#52c41a', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="总订单数"
              value={stats?.totalOrders || 0}
              prefix={<ShoppingCartOutlined className="cool-icon" style={{ color: '#faad14' }} />}
              valueStyle={{ color: '#faad14', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
        <Col xs={24} sm={12} lg={6}>
          <Card className="stat-card">
            <Statistic
              title="总收入"
              value={stats?.revenue || 0}
              prefix={<DollarOutlined className="cool-icon" style={{ color: '#f5222d' }} />}
              formatter={(value) => formatCurrency(value)}
              valueStyle={{ color: '#f5222d', fontWeight: 'bold' }}
            />
          </Card>
        </Col>
      </Row>

      {/* 功能特性和用户信息 */}
      <Row gutter={[16, 16]}>
        <Col xs={24} lg={12}>
          <Card className="cool-card" title="系统特性" style={{ height: '100%' }}>
            <List
              dataSource={homeData?.features || []}
              renderItem={(item, index) => (
                <List.Item>
                  <Space>
                    <Text strong style={{ color: '#1890ff' }}>{index + 1}.</Text>
                    <Text>{item}</Text>
                  </Space>
                </List.Item>
              )}
            />
          </Card>
        </Col>
        <Col xs={24} lg={12}>
          <Card className="cool-card" title="用户信息" style={{ height: '100%' }}>
            {user && (
              <Space direction="vertical" size="middle" style={{ width: '100%' }}>
                <div>
                  <Text type="secondary">用户名：</Text>
                  <Text strong>{user.username}</Text>
                </div>
                <div>
                  <Text type="secondary">姓名：</Text>
                  <Text strong>{user.fullName}</Text>
                </div>
                <div>
                  <Text type="secondary">邮箱：</Text>
                  <Text strong>{user.email}</Text>
                </div>
                <div>
                  <Text type="secondary">用户ID：</Text>
                  <Text code>{user.id}</Text>
                </div>
              </Space>
            )}
          </Card>
        </Col>
      </Row>
    </>
  );

  // 渲染数据图表内容
  const renderCharts = () => (
    <Row gutter={[16, 16]}>
      <Col xs={24} lg={12}>
        <Card className="cool-card" title="用户增长趋势" style={{ marginBottom: '16px' }}>
          <div style={{ textAlign: 'center', padding: '40px' }}>
            <Progress 
              type="circle" 
              percent={75} 
              format={() => '75%'} 
              strokeColor={{
                '0%': '#667eea',
                '100%': '#764ba2',
              }}
              size={120}
            />
            <div style={{ marginTop: '16px' }}>
              <Text strong>本月用户增长率</Text>
            </div>
          </div>
        </Card>
      </Col>
      <Col xs={24} lg={12}>
        <Card className="cool-card" title="系统性能" style={{ marginBottom: '16px' }}>
          <Space direction="vertical" style={{ width: '100%' }}>
            <div>
              <Text strong>CPU使用率</Text>
              <Progress 
                percent={30} 
                status="active" 
                strokeColor={{
                  '0%': '#52c41a',
                  '100%': '#73d13d',
                }}
              />
            </div>
            <div>
              <Text strong>内存使用率</Text>
              <Progress 
                percent={60} 
                status="active" 
                strokeColor={{
                  '0%': '#faad14',
                  '100%': '#ffc53d',
                }}
              />
            </div>
            <div>
              <Text strong>磁盘使用率</Text>
              <Progress 
                percent={45} 
                status="active" 
                strokeColor={{
                  '0%': '#1890ff',
                  '100%': '#40a9ff',
                }}
              />
            </div>
          </Space>
        </Card>
      </Col>
      <Col xs={24}>
        <Card className="cool-card" title="数据统计">
          <Row gutter={[16, 16]}>
            <Col xs={24} sm={8}>
              <Statistic 
                title="今日访问量" 
                value={1234} 
                prefix={<EyeOutlined className="cool-icon" style={{ color: '#1890ff' }} />}
                valueStyle={{ color: '#1890ff', fontWeight: 'bold' }}
              />
            </Col>
            <Col xs={24} sm={8}>
              <Statistic 
                title="本周新增用户" 
                value={89} 
                prefix={<UserAddOutlined className="cool-icon" style={{ color: '#52c41a' }} />}
                valueStyle={{ color: '#52c41a', fontWeight: 'bold' }}
              />
            </Col>
            <Col xs={24} sm={8}>
              <Statistic 
                title="系统响应时间" 
                value={120} 
                suffix="ms" 
                prefix={<ThunderboltOutlined className="cool-icon" style={{ color: '#faad14' }} />}
                valueStyle={{ color: '#faad14', fontWeight: 'bold' }}
              />
            </Col>
          </Row>
        </Card>
      </Col>
    </Row>
  );

  // 渲染用户管理内容
  const renderUsers = () => {
    const columns = [
      {
        title: 'ID',
        dataIndex: 'id',
        key: 'id',
        width: 80,
      },
      {
        title: '用户名',
        dataIndex: 'username',
        key: 'username',
      },
      {
        title: '姓名',
        dataIndex: 'fullName',
        key: 'fullName',
      },
      {
        title: '邮箱',
        dataIndex: 'email',
        key: 'email',
      },
      {
        title: '状态',
        dataIndex: 'status',
        key: 'status',
        render: (status) => (
          <Tag color={status === 'active' ? 'green' : 'red'}>
            {status === 'active' ? '活跃' : '禁用'}
          </Tag>
        ),
      },
      {
        title: '操作',
        key: 'action',
        render: (_, record) => (
          <Space size="middle">
            <Button type="link" size="small">编辑</Button>
            <Button type="link" size="small" danger>删除</Button>
          </Space>
        ),
      },
    ];

    const userData = [
      {
        id: 1,
        username: 'admin',
        fullName: '管理员',
        email: 'admin@example.com',
        status: 'active',
      },
      {
        id: 2,
        username: 'user',
        fullName: '普通用户',
        email: 'user@example.com',
        status: 'active',
      },
      {
        id: 3,
        username: 'test',
        fullName: '测试用户',
        email: 'test@example.com',
        status: 'inactive',
      },
    ];

    return (
      <Card className="cool-card" title="用户列表">
        <Table 
          columns={columns} 
          dataSource={userData} 
          rowKey="id"
          pagination={{ pageSize: 10 }}
        />
      </Card>
    );
  };

  // 渲染系统设置内容
  const renderSettings = () => (
    <Row gutter={[16, 16]}>
      <Col xs={24} lg={12}>
        <Card className="cool-card" title="系统配置">
          <Form layout="vertical">
            <Form.Item label="系统名称">
              <Input defaultValue="现代化管理系统" />
            </Form.Item>
            <Form.Item label="系统描述">
              <Input.TextArea defaultValue="基于React和Spring Boot的现代化管理系统" rows={3} />
            </Form.Item>
            <Form.Item label="时区">
              <Select defaultValue="Asia/Shanghai">
                <Select.Option value="Asia/Shanghai">Asia/Shanghai</Select.Option>
                <Select.Option value="UTC">UTC</Select.Option>
              </Select>
            </Form.Item>
            <Form.Item>
              <Button 
                type="primary" 
                className="cool-button"
                style={{
                  background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                  border: 'none'
                }}
              >
                保存设置
              </Button>
            </Form.Item>
          </Form>
        </Card>
      </Col>
      <Col xs={24} lg={12}>
        <Card className="cool-card" title="功能开关">
          <Space direction="vertical" style={{ width: '100%' }}>
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong>用户注册</Text>
              <Switch defaultChecked />
            </div>
            <Divider />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong>邮件通知</Text>
              <Switch defaultChecked />
            </div>
            <Divider />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong>系统维护模式</Text>
              <Switch />
            </div>
            <Divider />
            <div style={{ display: 'flex', justifyContent: 'space-between', alignItems: 'center' }}>
              <Text strong>调试模式</Text>
              <Switch />
            </div>
          </Space>
        </Card>
      </Col>
    </Row>
  );

  // 根据选中的菜单渲染内容
  const renderContent = () => {
    switch (selectedMenu) {
      case 'dashboard':
        return renderDashboard();
      case 'charts':
        return renderCharts();
      case 'users':
        return renderUsers();
      case 'settings':
        return renderSettings();
      default:
        return renderDashboard();
    }
  };

  const menuItems = [
    {
      key: 'dashboard',
      icon: <DashboardOutlined />,
      label: '仪表板',
    },
    {
      key: 'charts',
      icon: <BarChartOutlined />,
      label: '数据图表',
    },
    {
      key: 'users',
      icon: <TeamOutlined />,
      label: '用户管理',
    },
    {
      key: 'settings',
      icon: <SettingOutlined />,
      label: '系统设置',
    },
  ];

  return (
    <Layout style={{ minHeight: '100vh' }}>
      {/* 移动端抽屉菜单 */}
      <Drawer
        title="菜单"
        placement="left"
        onClose={() => setDrawerVisible(false)}
        open={drawerVisible}
        bodyStyle={{ padding: 0 }}
      >
        <Menu
          mode="inline"
          selectedKeys={[selectedMenu]}
          items={menuItems}
          onClick={handleMenuClick}
          style={{ border: 'none' }}
        />
      </Drawer>

      {/* 桌面端侧边栏 */}
      <Sider 
        width={200}
        className="cool-sider"
        style={{
          boxShadow: '2px 0 8px rgba(0,0,0,0.1)'
        }}
        breakpoint="sm"
        collapsedWidth="0"
        collapsed={siderCollapsed}
        onBreakpoint={(broken) => {
          setSiderCollapsed(broken);
        }}
      >
        <div style={{ 
          height: '64px', 
          display: 'flex', 
          alignItems: 'center', 
          justifyContent: 'center',
          borderBottom: '1px solid #f0f0f0'
        }}>
          <HomeOutlined className="cool-icon" style={{ fontSize: '24px', color: '#667eea' }} />
          <Text strong className="gradient-text" style={{ marginLeft: '8px', fontSize: '16px' }}>
            管理系统
          </Text>
        </div>
        <Menu
          mode="inline"
          selectedKeys={[selectedMenu]}
          items={menuItems}
          onClick={handleMenuClick}
          style={{ borderRight: 0, marginTop: '16px' }}
        />
      </Sider>

      <Layout>
        <Header 
          className="cool-header"
          style={{
            padding: '0 16px',
            boxShadow: '0 2px 8px rgba(0,0,0,0.1)',
            display: 'flex',
            alignItems: 'center',
            justifyContent: 'space-between',
            height: '64px'
          }}
        >
          <Space>
            <Button
              type="text"
              icon={<MenuOutlined />}
              onClick={() => setDrawerVisible(true)}
              className="mobile-menu-btn"
            />
            <Title level={4} style={{ margin: 0, color: '#333' }}>
              {menuItems.find(item => item.key === selectedMenu)?.label || '仪表板'}
            </Title>
          </Space>
          
          <Space size="small">
            {user && (
              <Space size="small" className="user-info">
                <Avatar icon={<UserOutlined />} size="small" />
                <Text strong className="user-name">{user.fullName || user.username}</Text>
              </Space>
            )}
            <Button 
              type="primary" 
              icon={<LogoutOutlined />} 
              onClick={handleLogout}
              size="small"
              className="cool-button"
              style={{
                background: 'linear-gradient(135deg, #667eea 0%, #764ba2 100%)',
                border: 'none'
              }}
            >
              <span className="logout-text">退出登录</span>
            </Button>
          </Space>
        </Header>

        <Content 
          className="cool-content"
          style={{ 
            padding: '16px', 
            minHeight: 'calc(100vh - 64px)',
            overflow: 'auto'
          }}
        >
          {renderContent()}
        </Content>
      </Layout>
    </Layout>
  );
};

export default Home; 