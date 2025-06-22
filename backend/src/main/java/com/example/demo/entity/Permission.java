package com.example.demo.entity;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import java.time.LocalDateTime;

@Entity
@Table(name = "permissions")
public class Permission {
    
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    
    @NotBlank(message = "权限名称不能为空")
    @Size(min = 2, max = 50, message = "权限名称长度必须在2-50个字符之间")
    @Column(unique = true)
    private String name;
    
    @NotBlank(message = "权限代码不能为空")
    @Size(min = 2, max = 50, message = "权限代码长度必须在2-50个字符之间")
    @Column(unique = true)
    private String code;
    
    @Size(max = 200, message = "描述长度不能超过200个字符")
    private String description;
    
    @NotBlank(message = "权限模块不能为空")
    @Size(max = 50, message = "权限模块长度不能超过50个字符")
    private String module;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "type")
    private PermissionType type = PermissionType.MENU;
    
    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private PermissionStatus status = PermissionStatus.ACTIVE;
    
    @Column(name = "sort_order")
    private Integer sortOrder = 0;
    
    @Column(name = "created_at")
    private LocalDateTime createdAt;
    
    @Column(name = "updated_at")
    private LocalDateTime updatedAt;
    
    // 权限类型枚举
    public enum PermissionType {
        MENU("菜单"),
        BUTTON("按钮"),
        API("接口");
        
        private final String displayName;
        
        PermissionType(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 权限状态枚举
    public enum PermissionStatus {
        ACTIVE("启用"),
        INACTIVE("禁用");
        
        private final String displayName;
        
        PermissionStatus(String displayName) {
            this.displayName = displayName;
        }
        
        public String getDisplayName() {
            return displayName;
        }
    }
    
    // 构造函数
    public Permission() {
        this.createdAt = LocalDateTime.now();
        this.updatedAt = LocalDateTime.now();
    }
    
    public Permission(String name, String code, String description, String module, PermissionType type) {
        this();
        this.name = name;
        this.code = code;
        this.description = description;
        this.module = module;
        this.type = type;
    }
    
    // Getters and Setters
    public Long getId() {
        return id;
    }
    
    public void setId(Long id) {
        this.id = id;
    }
    
    public String getName() {
        return name;
    }
    
    public void setName(String name) {
        this.name = name;
    }
    
    public String getCode() {
        return code;
    }
    
    public void setCode(String code) {
        this.code = code;
    }
    
    public String getDescription() {
        return description;
    }
    
    public void setDescription(String description) {
        this.description = description;
    }
    
    public String getModule() {
        return module;
    }
    
    public void setModule(String module) {
        this.module = module;
    }
    
    public PermissionType getType() {
        return type;
    }
    
    public void setType(PermissionType type) {
        this.type = type;
    }
    
    public PermissionStatus getStatus() {
        return status;
    }
    
    public void setStatus(PermissionStatus status) {
        this.status = status;
    }
    
    public Integer getSortOrder() {
        return sortOrder;
    }
    
    public void setSortOrder(Integer sortOrder) {
        this.sortOrder = sortOrder;
    }
    
    public LocalDateTime getCreatedAt() {
        return createdAt;
    }
    
    public void setCreatedAt(LocalDateTime createdAt) {
        this.createdAt = createdAt;
    }
    
    public LocalDateTime getUpdatedAt() {
        return updatedAt;
    }
    
    public void setUpdatedAt(LocalDateTime updatedAt) {
        this.updatedAt = updatedAt;
    }
    
    @PreUpdate
    public void preUpdate() {
        this.updatedAt = LocalDateTime.now();
    }
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        Permission that = (Permission) o;
        return id != null && id.equals(that.id);
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
    
    @Override
    public String toString() {
        return "Permission{" +
                "id=" + id +
                ", name='" + name + '\'' +
                ", code='" + code + '\'' +
                ", description='" + description + '\'' +
                ", module='" + module + '\'' +
                ", type=" + type +
                ", status=" + status +
                ", sortOrder=" + sortOrder +
                '}';
    }
} 