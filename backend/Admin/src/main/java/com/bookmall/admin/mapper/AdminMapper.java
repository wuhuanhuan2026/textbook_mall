package com.bookmall.admin.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.admin.entity.Admin;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.util.List;

/**
 * 管理员数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface AdminMapper extends BaseMapper<Admin> {

    /**
     * 根据用户名查询管理员
     */
    @Select("SELECT admin_id, username, password, role, status, real_name, phone, email, " +
            "avatar, last_login_ip, last_login_time, create_time, update_time " +
            "FROM tb_admin WHERE username = #{username}")
    Admin selectByUsername(@Param("username") String username);

    /**
     * 查询所有管理员
     */
    @Select("SELECT admin_id, username, role, status, real_name, phone, email, " +
            "avatar, last_login_ip, last_login_time, create_time, update_time " +
            "FROM tb_admin ORDER BY create_time DESC")
    List<Admin> selectAllAdmins();

    /**
     * 查询启用的管理员
     */
    @Select("SELECT admin_id, username, role, status, real_name, phone, email, " +
            "avatar, last_login_ip, last_login_time, create_time, update_time " +
            "FROM tb_admin WHERE status = 1 ORDER BY create_time DESC")
    List<Admin> selectActiveAdmins();

    /**
     * 更新登录信息
     */
    @Update("UPDATE tb_admin SET last_login_ip = #{ip}, last_login_time = NOW() " +
            "WHERE admin_id = #{adminId}")
    int updateLoginInfo(@Param("adminId") Long adminId,
                        @Param("ip") String ip);

    /**
     * 更新密码
     */
    @Update("UPDATE tb_admin SET password = #{password} WHERE admin_id = #{adminId}")
    int updatePassword(@Param("adminId") Long adminId,
                       @Param("password") String password);

    /**
     * 启用管理员
     */
    @Update("UPDATE tb_admin SET status = 1 WHERE admin_id = #{adminId}")
    int enableAdmin(@Param("adminId") Long adminId);

    /**
     * 禁用管理员
     */
    @Update("UPDATE tb_admin SET status = 0 WHERE admin_id = #{adminId}")
    int disableAdmin(@Param("adminId") Long adminId);

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM tb_admin WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 检查用户名是否存在（排除自身）
     */
    @Select("SELECT COUNT(*) FROM tb_admin WHERE username = #{username} AND admin_id != #{adminId}")
    int countByUsernameExcludeId(@Param("username") String username,
                                 @Param("adminId") Long adminId);

    /**
     * 统计管理员总数
     */
    @Select("SELECT COUNT(*) FROM tb_admin")
    Long countTotal();

    /**
     * 统计启用的管理员数
     */
    @Select("SELECT COUNT(*) FROM tb_admin WHERE status = 1")
    Long countActive();

    /**
     * 批量删除管理员
     */
    int batchDelete(@Param("adminIds") List<Long> adminIds);
}