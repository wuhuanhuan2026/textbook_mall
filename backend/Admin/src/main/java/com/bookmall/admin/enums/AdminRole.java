package com.bookmall.admin.enums;

import lombok.AllArgsConstructor;
import lombok.Getter;

/**
 * 管理员角色枚举
 *
 * @author BookMall Team
 */
@Getter
@AllArgsConstructor
public enum AdminRole {

    SUPER_ADMIN("super_admin", "超级管理员"),
    ADMIN("admin", "管理员"),
    OPERATOR("operator", "运营人员"),
    VIEWER("viewer", "查看者");

    private final String code;
    private final String desc;

    public static AdminRole fromCode(String code) {
        for (AdminRole role : values()) {
            if (role.code.equals(code)) {
                return role;
            }
        }
        return null;
    }

    public static String getDesc(String code) {
        AdminRole role = fromCode(code);
        return role != null ? role.desc : "未知";
    }

    /**
     * 判断是否有权限
     */
    public boolean hasPermission(AdminRole requiredRole) {
        return this.ordinal() <= requiredRole.ordinal();
    }

    /**
     * 判断是否是超级管理员
     */
    public boolean isSuperAdmin() {
        return this == SUPER_ADMIN;
    }

    /**
     * 判断是否是管理员或以上
     */
    public boolean isAdminOrAbove() {
        return this.ordinal() <= ADMIN.ordinal();
    }
}