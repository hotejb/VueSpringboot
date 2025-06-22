package com.example.demo.config;

import com.example.demo.entity.User;
import com.example.demo.service.UserService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

@Component
@Order(1) // 在角色权限初始化之前执行
public class DataInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(DataInitializer.class);
    
    @Autowired
    private UserService userService;
    
    @Override
    public void run(String... args) throws Exception {
        // 创建默认管理员用户
        if (!userService.existsByUsername("admin")) {
            User admin = new User();
            admin.setUsername("admin");
            admin.setPassword("123456");
            admin.setEmail("admin@vuespring.com");
            admin.setFullName("系统管理员");
            admin.setPhone("13800138000");
            admin.setDepartment("信息技术部");
            admin.setPosition("系统管理员");
            admin.setRole(User.UserRole.ADMIN);
            admin.setStatus(User.UserStatus.ACTIVE);
            
            userService.createUser(admin);
            logger.info("默认管理员用户已创建: admin/123456");
        }
        
        // 创建示例用户数据
        createSampleUsers();
    }
    
    private void createSampleUsers() {
        // 用户数据数组
        String[][] userData = {
            {"zhangsan", "123456", "zhangsan@vuespring.com", "张三", "13800138001", "技术部", "前端工程师", "USER", "ACTIVE"},
            {"lisi", "123456", "lisi@vuespring.com", "李四", "13800138002", "技术部", "后端工程师", "USER", "ACTIVE"},
            {"wangwu", "123456", "wangwu@vuespring.com", "王五", "13800138003", "产品部", "产品经理", "MANAGER", "ACTIVE"},
            {"zhaoliu", "123456", "zhaoliu@vuespring.com", "赵六", "13800138004", "设计部", "UI设计师", "USER", "ACTIVE"},
            {"sunqi", "123456", "sunqi@vuespring.com", "孙七", "13800138005", "市场部", "市场专员", "USER", "ACTIVE"},
            {"zhouba", "123456", "zhouba@vuespring.com", "周八", "13800138006", "销售部", "销售经理", "MANAGER", "ACTIVE"},
            {"wujiu", "123456", "wujiu@vuespring.com", "吴九", "13800138007", "人事部", "人事专员", "USER", "ACTIVE"},
            {"zhengshi", "123456", "zhengshi@vuespring.com", "郑十", "13800138008", "财务部", "财务分析师", "USER", "ACTIVE"},
            {"liuyi", "123456", "liuyi@vuespring.com", "刘一", "13800138009", "技术部", "测试工程师", "USER", "INACTIVE"},
            {"chener", "123456", "chener@vuespring.com", "陈二", "13800138010", "运营部", "运营专员", "USER", "PENDING"}
        };
        
        for (String[] data : userData) {
            if (!userService.existsByUsername(data[0])) {
                try {
                    User user = new User();
                    user.setUsername(data[0]);
                    user.setPassword(data[1]);
                    user.setEmail(data[2]);
                    user.setFullName(data[3]);
                    user.setPhone(data[4]);
                    user.setDepartment(data[5]);
                    user.setPosition(data[6]);
                    user.setRole(User.UserRole.valueOf(data[7]));
                    user.setStatus(User.UserStatus.valueOf(data[8]));
                    
                    userService.createUser(user);
                    logger.info("示例用户已创建: {} - {}", data[0], data[3]);
                } catch (Exception e) {
                    logger.error("创建用户失败: {} - {}", data[0], e.getMessage());
                }
            }
        }
    }
} 