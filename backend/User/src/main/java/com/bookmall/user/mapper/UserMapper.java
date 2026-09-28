package com.bookmall.user.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.user.entity.User;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 用户数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface UserMapper extends BaseMapper<User> {

    // ==================== 基础查询 ====================

    /**
     * 根据用户名查询用户
     */
    @Select("SELECT user_id, username, password, phone, email, avatar, balance, status, create_time, update_time " +
            "FROM tb_user WHERE username = #{username}")
    User selectByUsername(@Param("username") String username);

    /**
     * 根据手机号查询用户
     */
    @Select("SELECT user_id, username, password, phone, email, avatar, balance, status, create_time, update_time " +
            "FROM tb_user WHERE phone = #{phone}")
    User selectByPhone(@Param("phone") String phone);

    /**
     * 根据邮箱查询用户
     */
    @Select("SELECT user_id, username, password, phone, email, avatar, balance, status, create_time, update_time " +
            "FROM tb_user WHERE email = #{email}")
    User selectByEmail(@Param("email") String email);

    /**
     * 根据用户ID查询（带缓存）
     */
    @Select("SELECT user_id, username, password, phone, email, avatar, balance, status, create_time, update_time " +
            "FROM tb_user WHERE user_id = #{userId}")
    User selectByUserId(@Param("userId") Long userId);

    // ==================== 余额操作 ====================

    /**
     * 扣减用户余额（带余额校验）
     *
     * @param userId 用户ID
     * @param amount 扣减金额
     * @return 影响行数（0表示余额不足或用户不存在）
     */
    @Update("UPDATE tb_user SET balance = balance - #{amount} " +
            "WHERE user_id = #{userId} AND balance >= #{amount} AND status = 1")
    int deductBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 增加用户余额
     *
     * @param userId 用户ID
     * @param amount 增加金额
     * @return 影响行数
     */
    @Update("UPDATE tb_user SET balance = balance + #{amount} " +
            "WHERE user_id = #{userId} AND status = 1")
    int addBalance(@Param("userId") Long userId, @Param("amount") BigDecimal amount);

    /**
     * 获取用户余额
     *
     * @param userId 用户ID
     * @return 用户余额
     */
    @Select("SELECT balance FROM tb_user WHERE user_id = #{userId}")
    BigDecimal getBalance(@Param("userId") Long userId);

    // ==================== 状态操作 ====================

    /**
     * 启用用户
     */
    @Update("UPDATE tb_user SET status = 1 WHERE user_id = #{userId}")
    int enableUser(@Param("userId") Long userId);

    /**
     * 禁用用户
     */
    @Update("UPDATE tb_user SET status = 0 WHERE user_id = #{userId}")
    int disableUser(@Param("userId") Long userId);

    /**
     * 更新用户头像
     */
    @Update("UPDATE tb_user SET avatar = #{avatar} WHERE user_id = #{userId}")
    int updateAvatar(@Param("userId") Long userId, @Param("avatar") String avatar);

    /**
     * 更新用户手机号
     */
    @Update("UPDATE tb_user SET phone = #{phone} WHERE user_id = #{userId}")
    int updatePhone(@Param("userId") Long userId, @Param("phone") String phone);

    /**
     * 更新用户邮箱
     */
    @Update("UPDATE tb_user SET email = #{email} WHERE user_id = #{userId}")
    int updateEmail(@Param("userId") Long userId, @Param("email") String email);

    // ==================== 验证检查 ====================

    /**
     * 检查用户名是否存在
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE username = #{username}")
    int countByUsername(@Param("username") String username);

    /**
     * 检查手机号是否存在
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE phone = #{phone}")
    int countByPhone(@Param("phone") String phone);

    /**
     * 检查邮箱是否存在
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE email = #{email}")
    int countByEmail(@Param("email") String email);

    /**
     * 检查用户名是否存在（排除指定用户ID）
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE username = #{username} AND user_id != #{userId}")
    int countByUsernameExcludeId(@Param("username") String username, @Param("userId") Long userId);

    /**
     * 检查手机号是否存在（排除指定用户ID）
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE phone = #{phone} AND user_id != #{userId}")
    int countByPhoneExcludeId(@Param("phone") String phone, @Param("userId") Long userId);

    /**
     * 检查邮箱是否存在（排除指定用户ID）
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE email = #{email} AND user_id != #{userId}")
    int countByEmailExcludeId(@Param("email") String email, @Param("userId") Long userId);

    /**
     * 检查用户是否存在且状态正常
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE user_id = #{userId} AND status = 1")
    int countActiveUser(@Param("userId") Long userId);

    // ==================== 批量操作 ====================

    /**
     * 批量启用/禁用用户（XML实现）
     */
    int batchUpdateStatus(@Param("userIds") List<Long> userIds, @Param("status") Integer status);

    /**
     * 批量删除用户（物理删除，XML实现）
     */
    int batchDelete(@Param("userIds") List<Long> userIds);

    // ==================== 统计查询 ====================

    /**
     * 统计各状态用户数量
     */
    @Select("SELECT status, COUNT(*) AS count FROM tb_user GROUP BY status")
    List<Map<String, Object>> countByStatus();

    /**
     * 统计今日注册用户数
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE DATE(create_time) = CURDATE()")
    Long countTodayRegistered();

    /**
     * 统计本月注册用户数
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE YEAR(create_time) = YEAR(CURDATE()) " +
            "AND MONTH(create_time) = MONTH(CURDATE())")
    Long countMonthRegistered();

    /**
     * 统计指定时间段注册用户数
     */
    @Select("SELECT COUNT(*) FROM tb_user WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    Long countRegisteredBetween(@Param("startTime") LocalDateTime startTime,
                                @Param("endTime") LocalDateTime endTime);

    /**
     * 统计用户总数（带条件）
     */
    @Select("<script>" +
            "SELECT COUNT(*) FROM tb_user " +
            "<where>" +
            "<if test='username != null and username != \"\"'>" +
            "AND username LIKE CONCAT('%', #{username}, '%') " +
            "</if>" +
            "<if test='status != null'>" +
            "AND status = #{status} " +
            "</if>" +
            "</where>" +
            "</script>")
    Long countUsers(@Param("username") String username, @Param("status") Integer status);

    // ==================== 分页查询 ====================

    /**
     * 分页查询用户列表（XML实现）
     */
    List<User> selectUserPage(@Param("username") String username,
                              @Param("phone") String phone,
                              @Param("status") Integer status,
                              @Param("startTime") LocalDateTime startTime,
                              @Param("endTime") LocalDateTime endTime);

    /**
     * 查询所有正常用户
     */
    @Select("SELECT user_id, username, phone, email, avatar, balance " +
            "FROM tb_user WHERE status = 1 ORDER BY user_id")
    List<User> selectAllActiveUsers();

    /**
     * 根据用户ID列表查询用户
     */
    @Select("<script>" +
            "SELECT user_id, username, phone, email, avatar, balance, status " +
            "FROM tb_user WHERE user_id IN " +
            "<foreach collection='userIds' item='id' open='(' separator=',' close=')'>" +
            "#{id}" +
            "</foreach>" +
            "</script>")
    List<User> selectByIds(@Param("userIds") List<Long> userIds);
}