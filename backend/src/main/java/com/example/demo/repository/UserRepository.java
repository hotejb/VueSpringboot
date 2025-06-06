package com.example.demo.repository;

import com.example.demo.entity.User;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
    
    Optional<User> findByUsername(String username);
    
    Optional<User> findByEmail(String email);
    
    boolean existsByUsername(String username);
    
    boolean existsByEmail(String email);
    
    // 搜索用户（按用户名、全名或邮箱）
    Page<User> findByUsernameContainingOrFullNameContainingOrEmailContaining(
        String username, String fullName, String email, Pageable pageable);
    
    // 按状态查询用户
    Page<User> findByStatus(User.UserStatus status, Pageable pageable);
    
    // 按角色查询用户
    Page<User> findByRole(User.UserRole role, Pageable pageable);
    
    // 按状态和角色查询用户
    Page<User> findByStatusAndRole(User.UserStatus status, User.UserRole role, Pageable pageable);
    
    // 复合查询：搜索文本 + 状态
    Page<User> findByStatusAndUsernameContainingOrStatusAndFullNameContainingOrStatusAndEmailContaining(
        User.UserStatus status1, String username, 
        User.UserStatus status2, String fullName, 
        User.UserStatus status3, String email, 
        Pageable pageable);
    
    // 复合查询：搜索文本 + 角色
    Page<User> findByRoleAndUsernameContainingOrRoleAndFullNameContainingOrRoleAndEmailContaining(
        User.UserRole role1, String username, 
        User.UserRole role2, String fullName, 
        User.UserRole role3, String email, 
        Pageable pageable);
    
    // 复合查询：搜索文本 + 状态 + 角色
    Page<User> findByStatusAndRoleAndUsernameContainingOrStatusAndRoleAndFullNameContainingOrStatusAndRoleAndEmailContaining(
        User.UserStatus status1, User.UserRole role1, String username,
        User.UserStatus status2, User.UserRole role2, String fullName,
        User.UserStatus status3, User.UserRole role3, String email,
        Pageable pageable);
} 