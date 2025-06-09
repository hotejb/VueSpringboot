package com.example.demo.util;

import com.example.demo.entity.User;
import org.apache.poi.ss.usermodel.*;
import org.apache.poi.xssf.usermodel.XSSFWorkbook;
import org.springframework.web.multipart.MultipartFile;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.List;

public class ExcelUtil {
    
    private static final String[] HEADERS = {
        "用户名", "姓名", "邮箱", "电话", "部门", "职位", "角色", "状态", "创建时间"
    };
    
    /**
     * 导出用户数据到Excel
     */
    public static byte[] exportUsersToExcel(List<User> users) throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户数据");
        
        // 创建标题样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.GREY_25_PERCENT.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        headerStyle.setBorderBottom(BorderStyle.THIN);
        headerStyle.setBorderTop(BorderStyle.THIN);
        headerStyle.setBorderRight(BorderStyle.THIN);
        headerStyle.setBorderLeft(BorderStyle.THIN);
        
        // 创建数据样式
        CellStyle dataStyle = workbook.createCellStyle();
        dataStyle.setBorderBottom(BorderStyle.THIN);
        dataStyle.setBorderTop(BorderStyle.THIN);
        dataStyle.setBorderRight(BorderStyle.THIN);
        dataStyle.setBorderLeft(BorderStyle.THIN);
        
        // 创建标题行
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < HEADERS.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(HEADERS[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 填充数据
        DateTimeFormatter formatter = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss");
        for (int i = 0; i < users.size(); i++) {
            Row row = sheet.createRow(i + 1);
            User user = users.get(i);
            
            Cell cell0 = row.createCell(0);
            cell0.setCellValue(user.getUsername());
            cell0.setCellStyle(dataStyle);
            
            Cell cell1 = row.createCell(1);
            cell1.setCellValue(user.getFullName());
            cell1.setCellStyle(dataStyle);
            
            Cell cell2 = row.createCell(2);
            cell2.setCellValue(user.getEmail());
            cell2.setCellStyle(dataStyle);
            
            Cell cell3 = row.createCell(3);
            cell3.setCellValue(user.getPhone() != null ? user.getPhone() : "");
            cell3.setCellStyle(dataStyle);
            
            Cell cell4 = row.createCell(4);
            cell4.setCellValue(user.getDepartment() != null ? user.getDepartment() : "");
            cell4.setCellStyle(dataStyle);
            
            Cell cell5 = row.createCell(5);
            cell5.setCellValue(user.getPosition() != null ? user.getPosition() : "");
            cell5.setCellStyle(dataStyle);
            
            Cell cell6 = row.createCell(6);
            cell6.setCellValue(getRoleDisplayName(user.getRole()));
            cell6.setCellStyle(dataStyle);
            
            Cell cell7 = row.createCell(7);
            cell7.setCellValue(getStatusDisplayName(user.getStatus()));
            cell7.setCellStyle(dataStyle);
            
            Cell cell8 = row.createCell(8);
            cell8.setCellValue(user.getCreatedAt() != null ? user.getCreatedAt().format(formatter) : "");
            cell8.setCellStyle(dataStyle);
        }
        
        // 自动调整列宽
        for (int i = 0; i < HEADERS.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    /**
     * 从Excel导入用户数据
     */
    public static List<User> importUsersFromExcel(MultipartFile file) throws IOException {
        List<User> users = new ArrayList<>();
        
        Workbook workbook = WorkbookFactory.create(file.getInputStream());
        Sheet sheet = workbook.getSheetAt(0);
        
        // 跳过标题行，从第二行开始读取数据
        for (int i = 1; i <= sheet.getLastRowNum(); i++) {
            Row row = sheet.getRow(i);
            if (row == null) continue;
            
            // 检查是否为空行
            if (isEmptyRow(row)) continue;
            
            User user = new User();
            
            // 用户名 (必填)
            Cell usernameCell = row.getCell(0);
            String username = usernameCell != null ? getCellValueAsString(usernameCell).trim() : "";
            user.setUsername(username);
            
            // 姓名 (必填)
            Cell fullNameCell = row.getCell(1);
            String fullName = fullNameCell != null ? getCellValueAsString(fullNameCell).trim() : "";
            user.setFullName(fullName);
            
            // 邮箱 (必填)
            Cell emailCell = row.getCell(2);
            String email = emailCell != null ? getCellValueAsString(emailCell).trim() : "";
            user.setEmail(email);
            
            // 电话
            Cell phoneCell = row.getCell(3);
            if (phoneCell != null) {
                String phone = getCellValueAsString(phoneCell).trim();
                user.setPhone(phone.isEmpty() ? null : phone);
            }
            
            // 部门
            Cell departmentCell = row.getCell(4);
            if (departmentCell != null) {
                String department = getCellValueAsString(departmentCell).trim();
                user.setDepartment(department.isEmpty() ? null : department);
            }
            
            // 职位
            Cell positionCell = row.getCell(5);
            if (positionCell != null) {
                String position = getCellValueAsString(positionCell).trim();
                user.setPosition(position.isEmpty() ? null : position);
            }
            
            // 角色
            Cell roleCell = row.getCell(6);
            if (roleCell != null) {
                String roleStr = getCellValueAsString(roleCell).trim();
                user.setRole(parseRole(roleStr));
            } else {
                user.setRole(User.UserRole.USER); // 默认角色
            }
            
            // 状态
            Cell statusCell = row.getCell(7);
            if (statusCell != null) {
                String statusStr = getCellValueAsString(statusCell).trim();
                user.setStatus(parseStatus(statusStr));
            } else {
                user.setStatus(User.UserStatus.ACTIVE); // 默认状态
            }
            
            // 设置默认密码和时间
            user.setPassword("123456"); // 默认密码，后续会被加密
            user.setCreatedAt(LocalDateTime.now());
            user.setUpdatedAt(LocalDateTime.now());
            
            users.add(user);
        }
        
        workbook.close();
        return users;
    }
    
    /**
     * 生成导入模板
     */
    public static byte[] generateImportTemplate() throws IOException {
        Workbook workbook = new XSSFWorkbook();
        Sheet sheet = workbook.createSheet("用户导入模板");
        
        // 创建标题样式
        CellStyle headerStyle = workbook.createCellStyle();
        Font headerFont = workbook.createFont();
        headerFont.setBold(true);
        headerFont.setFontHeightInPoints((short) 12);
        headerStyle.setFont(headerFont);
        headerStyle.setFillForegroundColor(IndexedColors.LIGHT_BLUE.getIndex());
        headerStyle.setFillPattern(FillPatternType.SOLID_FOREGROUND);
        
        // 创建说明样式
        CellStyle noteStyle = workbook.createCellStyle();
        Font noteFont = workbook.createFont();
        noteFont.setColor(IndexedColors.RED.getIndex());
        noteStyle.setFont(noteFont);
        
        // 创建标题行
        String[] templateHeaders = {
            "用户名*", "姓名*", "邮箱*", "电话", "部门", "职位", "角色", "状态"
        };
        
        Row headerRow = sheet.createRow(0);
        for (int i = 0; i < templateHeaders.length; i++) {
            Cell cell = headerRow.createCell(i);
            cell.setCellValue(templateHeaders[i]);
            cell.setCellStyle(headerStyle);
        }
        
        // 添加示例数据
        Row exampleRow = sheet.createRow(1);
        String[] exampleData = {
            "zhangsan", "张三", "zhangsan@example.com", "13800138000", 
            "技术部", "软件工程师", "普通用户", "激活"
        };
        
        for (int i = 0; i < exampleData.length; i++) {
            Cell cell = exampleRow.createCell(i);
            cell.setCellValue(exampleData[i]);
        }
        
        // 添加说明
        Row noteRow1 = sheet.createRow(3);
        Cell noteCell1 = noteRow1.createCell(0);
        noteCell1.setCellValue("说明：");
        noteCell1.setCellStyle(noteStyle);
        
        Row noteRow2 = sheet.createRow(4);
        Cell noteCell2 = noteRow2.createCell(0);
        noteCell2.setCellValue("1. 带*号的字段为必填项");
        
        Row noteRow3 = sheet.createRow(5);
        Cell noteCell3 = noteRow3.createCell(0);
        noteCell3.setCellValue("2. 角色可填：管理员/ADMIN、经理/MANAGER、普通用户/USER");
        
        Row noteRow4 = sheet.createRow(6);
        Cell noteCell4 = noteRow4.createCell(0);
        noteCell4.setCellValue("3. 状态可填：激活/ACTIVE、禁用/INACTIVE、待审核/PENDING");
        
        Row noteRow5 = sheet.createRow(7);
        Cell noteCell5 = noteRow5.createCell(0);
        noteCell5.setCellValue("4. 导入用户的默认密码为：123456");
        
        // 自动调整列宽
        for (int i = 0; i < templateHeaders.length; i++) {
            sheet.autoSizeColumn(i);
        }
        
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        workbook.write(outputStream);
        workbook.close();
        
        return outputStream.toByteArray();
    }
    
    // 辅助方法
    private static String getCellValueAsString(Cell cell) {
        if (cell == null) {
            return "";
        }
        
        try {
            switch (cell.getCellType()) {
                case STRING:
                    String stringValue = cell.getStringCellValue();
                    return stringValue != null ? stringValue : "";
                case NUMERIC:
                    if (DateUtil.isCellDateFormatted(cell)) {
                        return cell.getDateCellValue().toString();
                    } else {
                        // 处理数字，避免科学计数法
                        double numericValue = cell.getNumericCellValue();
                        if (numericValue == (long) numericValue) {
                            return String.valueOf((long) numericValue);
                        } else {
                            return String.valueOf(numericValue);
                        }
                    }
                case BOOLEAN:
                    return String.valueOf(cell.getBooleanCellValue());
                case FORMULA:
                    try {
                        // 尝试获取公式的计算结果
                        return cell.getStringCellValue();
                    } catch (Exception e) {
                        return cell.getCellFormula();
                    }
                case BLANK:
                    return "";
                default:
                    return "";
            }
        } catch (Exception e) {
            System.err.println("Error reading cell value: " + e.getMessage());
            return "";
        }
    }
    
    private static boolean isEmptyRow(Row row) {
        if (row == null) return true;
        
        // 检查前3个必填列是否都为空
        boolean allEmpty = true;
        for (int i = 0; i < 3; i++) {
            Cell cell = row.getCell(i);
            String value = getCellValueAsString(cell).trim();
            if (!value.isEmpty()) {
                allEmpty = false;
                break;
            }
        }
        
        if (allEmpty) return true;
        
        // 检查是否是说明行（以"说明"、数字开头或包含特定关键词）
        Cell firstCell = row.getCell(0);
        if (firstCell != null) {
            String firstValue = getCellValueAsString(firstCell).trim();
            if (firstValue.startsWith("说明") || 
                firstValue.matches("^\\d+\\..*") || 
                firstValue.contains("带*号") ||
                firstValue.contains("角色可填") ||
                firstValue.contains("状态可填") ||
                firstValue.contains("默认密码")) {
                return true;
            }
        }
        
        return false;
    }
    
    private static User.UserRole parseRole(String roleStr) {
        if (roleStr == null || roleStr.trim().isEmpty()) {
            return User.UserRole.USER;
        }
        
        roleStr = roleStr.trim().toUpperCase();
        switch (roleStr) {
            case "管理员":
            case "ADMIN":
                return User.UserRole.ADMIN;
            case "经理":
            case "MANAGER":
                return User.UserRole.MANAGER;
            case "普通用户":
            case "USER":
            default:
                return User.UserRole.USER;
        }
    }
    
    private static User.UserStatus parseStatus(String statusStr) {
        if (statusStr == null || statusStr.trim().isEmpty()) {
            return User.UserStatus.ACTIVE;
        }
        
        statusStr = statusStr.trim().toUpperCase();
        switch (statusStr) {
            case "激活":
            case "ACTIVE":
                return User.UserStatus.ACTIVE;
            case "禁用":
            case "INACTIVE":
                return User.UserStatus.INACTIVE;
            case "待审核":
            case "PENDING":
                return User.UserStatus.PENDING;
            default:
                return User.UserStatus.ACTIVE;
        }
    }
    
    private static String getRoleDisplayName(User.UserRole role) {
        switch (role) {
            case ADMIN:
                return "管理员";
            case MANAGER:
                return "经理";
            case USER:
            default:
                return "普通用户";
        }
    }
    
    private static String getStatusDisplayName(User.UserStatus status) {
        switch (status) {
            case ACTIVE:
                return "激活";
            case INACTIVE:
                return "禁用";
            case PENDING:
                return "待审核";
            default:
                return "激活";
        }
    }
} 