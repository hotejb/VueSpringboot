package com.example.demo.config;

import com.example.demo.entity.Role;
import com.example.demo.entity.Permission;
import com.example.demo.service.RoleService;
import com.example.demo.service.PermissionService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;

import java.util.HashSet;
import java.util.List;
import java.util.Set;

@Component
@Order(2)
public class RolePermissionInitializer implements CommandLineRunner {
    
    private static final Logger logger = LoggerFactory.getLogger(RolePermissionInitializer.class);
    
    @Autowired
    private RoleService roleService;
    
    @Autowired
    private PermissionService permissionService;
    
    @Override
    public void run(String... args) throws Exception {
        initializePermissions();
        initializeRoles();
        assignPermissionsToRoles();
    }
    
    private void initializePermissions() {
        logger.info("开始初始化权限数据...");
        
        createPermissionIfNotExists("用户查看", "user:view", "查看用户列表和详情", "用户管理", Permission.PermissionType.MENU, 0);
        createPermissionIfNotExists("用户创建", "user:create", "创建新用户", "用户管理", Permission.PermissionType.BUTTON, 1);
        createPermissionIfNotExists("用户编辑", "user:edit", "编辑用户信息", "用户管理", Permission.PermissionType.BUTTON, 2);
        createPermissionIfNotExists("用户删除", "user:delete", "删除用户", "用户管理", Permission.PermissionType.BUTTON, 3);
        createPermissionIfNotExists("用户导入", "user:import", "批量导入用户", "用户管理", Permission.PermissionType.BUTTON, 4);
        createPermissionIfNotExists("用户导出", "user:export", "导出用户数据", "用户管理", Permission.PermissionType.BUTTON, 5);
        
        createPermissionIfNotExists("角色查看", "role:view", "查看角色列表和详情", "角色管理", Permission.PermissionType.MENU, 10);
        createPermissionIfNotExists("角色创建", "role:create", "创建新角色", "角色管理", Permission.PermissionType.BUTTON, 11);
        createPermissionIfNotExists("角色编辑", "role:edit", "编辑角色信息", "角色管理", Permission.PermissionType.BUTTON, 12);
        createPermissionIfNotExists("角色删除", "role:delete", "删除角色", "角色管理", Permission.PermissionType.BUTTON, 13);
        createPermissionIfNotExists("角色权限分配", "role:assign", "为角色分配权限", "角色管理", Permission.PermissionType.BUTTON, 14);
        
        createPermissionIfNotExists("权限查看", "permission:view", "查看权限列表和详情", "权限管理", Permission.PermissionType.MENU, 20);
        createPermissionIfNotExists("权限创建", "permission:create", "创建新权限", "权限管理", Permission.PermissionType.BUTTON, 21);
        createPermissionIfNotExists("权限编辑", "permission:edit", "编辑权限信息", "权限管理", Permission.PermissionType.BUTTON, 22);
        createPermissionIfNotExists("权限删除", "permission:delete", "删除权限", "权限管理", Permission.PermissionType.BUTTON, 23);
        
        createPermissionIfNotExists("系统设置", "system:settings", "系统配置管理", "系统管理", Permission.PermissionType.MENU, 30);
        createPermissionIfNotExists("系统监控", "system:monitor", "系统监控和日志", "系统管理", Permission.PermissionType.MENU, 31);
        createPermissionIfNotExists("数据备份", "system:backup", "数据备份和恢复", "系统管理", Permission.PermissionType.BUTTON, 32);
        
        createPermissionIfNotExists("仪表板查看", "dashboard:view", "查看系统仪表板", "仪表板", Permission.PermissionType.MENU, 40);
        createPermissionIfNotExists("统计报表", "dashboard:report", "查看统计报表", "仪表板", Permission.PermissionType.MENU, 41);
        
        logger.info("权限数据初始化完成");
    }
    
    private void createPermissionIfNotExists(String name, String code, String description, String module, Permission.PermissionType type, Integer sortOrder) {
        if (!permissionService.existsByCode(code)) {
            try {
                Permission permission = new Permission(name, code, description, module, type);
                permission.setSortOrder(sortOrder);
                permissionService.createPermission(permission);
                logger.info("权限已创建: {} - {}", code, name);
            } catch (Exception e) {
                logger.error("创建权限失败: {} - {}", code, e.getMessage());
            }
        }
    }
    
    private void initializeRoles() {
        logger.info("开始初始化角色数据...");
        
        createRoleIfNotExists("超级管理员", "SUPER_ADMIN", "系统超级管理员，拥有所有权限", 0);
        createRoleIfNotExists("系统管理员", "ADMIN", "系统管理员，拥有大部分管理权限", 1);
        createRoleIfNotExists("部门经理", "MANAGER", "部门经理，拥有部门内用户管理权限", 2);
        createRoleIfNotExists("普通用户", "USER", "普通用户，拥有基本查看权限", 3);
        createRoleIfNotExists("访客用户", "GUEST", "访客用户，只能查看公开信息", 4);
        
        logger.info("角色数据初始化完成");
    }
    
    private void createRoleIfNotExists(String name, String code, String description, Integer sortOrder) {
        if (!roleService.existsByCode(code)) {
            try {
                Role role = new Role(name, code, description);
                role.setSortOrder(sortOrder);
                roleService.createRole(role);
                logger.info("角色已创建: {} - {}", code, name);
            } catch (Exception e) {
                logger.error("创建角色失败: {} - {}", code, e.getMessage());
            }
        }
    }
    
    private void assignPermissionsToRoles() {
        logger.info("开始为角色分配权限...");
        
        try {
            List<Permission> allPermissions = permissionService.getActivePermissions();
            
            // 超级管理员 - 拥有所有权限
            assignPermissionsToRole("SUPER_ADMIN", allPermissions);
            
            // 系统管理员 - 拥有除数据备份外的所有权限
            List<Permission> adminPermissions = allPermissions.stream()
                .filter(p -> !p.getCode().equals("system:backup"))
                .toList();
            assignPermissionsToRole("ADMIN", adminPermissions);
            
            // 部门经理 - 拥有用户管理和查看权限
            List<Permission> managerPermissions = allPermissions.stream()
                .filter(p -> p.getModule().equals("用户管理") || 
                           p.getModule().equals("仪表板") ||
                           p.getCode().equals("dashboard:view"))
                .toList();
            assignPermissionsToRole("MANAGER", managerPermissions);
            
            // 普通用户 - 只有查看权限
            List<Permission> userPermissions = allPermissions.stream()
                .filter(p -> p.getCode().contains("view") || 
                           p.getCode().equals("dashboard:view"))
                .toList();
            assignPermissionsToRole("USER", userPermissions);
            
            // 访客用户 - 只有仪表板查看权限
            List<Permission> guestPermissions = allPermissions.stream()
                .filter(p -> p.getCode().equals("dashboard:view"))
                .toList();
            assignPermissionsToRole("GUEST", guestPermissions);
            
        } catch (Exception e) {
            logger.error("角色权限分配失败: {}", e.getMessage());
        }
        
        logger.info("角色权限分配完成");
    }
    
    private void assignPermissionsToRole(String roleCode, List<Permission> permissions) {
        try {
            Role role = roleService.findByCode(roleCode).orElse(null);
            if (role != null) {
                Set<Long> permissionIds = new HashSet<>();
                for (Permission permission : permissions) {
                    permissionIds.add(permission.getId());
                }
                roleService.assignPermissions(role.getId(), permissionIds);
                logger.info("为角色 {} 分配了 {} 个权限", roleCode, permissions.size());
            }
        } catch (Exception e) {
            logger.error("为角色 {} 分配权限失败: {}", roleCode, e.getMessage());
        }
    }
} 