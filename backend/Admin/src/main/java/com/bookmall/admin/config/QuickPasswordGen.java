package com.bookmall.admin.config;

import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

/**
 * 快速生成 BCrypt 加密密码
 * 运行 main 方法即可生成
 *
 * @author BookMall Team
 */
public class QuickPasswordGen {

    public static void main(String[] args) {
        BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();

        // ============================================================
        // 修改下面的密码即可生成对应的加密密码
        // ============================================================
        String password = "admin123";

        String encoded = encoder.encode(password);

        System.out.println("========================================");
        System.out.println("  明文密码: " + password);
        System.out.println("  加密密码: " + encoded);
        System.out.println("========================================");
        System.out.println();
        System.out.println("SQL 更新语句:");
        System.out.println("UPDATE tb_admin SET password = '" + encoded + "' WHERE username = 'admin';");
        System.out.println();
        System.out.println("或者插入新管理员:");
        System.out.println("INSERT INTO tb_admin (username, password, role, status) VALUES ('admin', '" + encoded + "', 'admin', 1);");
    }
}