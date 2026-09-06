package com.bookmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.order.entity.Order;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface OrderMapper extends BaseMapper<Order> {

    /**
     * 分页查询用户订单列表
     */
    Page<Order> selectUserOrderPage(Page<Order> page,
                                    @Param("userId") Long userId,
                                    @Param("status") Integer status);

    /**
     * 分页查询所有订单（管理员）
     */
    Page<Order> selectAllOrderPage(Page<Order> page,
                                   @Param("userId") Long userId,
                                   @Param("status") Integer status,
                                   @Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 查询订单详情（含商品列表）
     */
    Order selectOrderDetail(@Param("orderId") String orderId);

    /**
     * 根据用户ID和订单号查询
     */
    @Select("SELECT order_id, user_id, total_amount, shipping_fee, order_status, " +
            "pay_type, receiver_name, receiver_phone, receiver_address, remark, " +
            "pay_time, deliver_time, receive_time, create_time " +
            "FROM tb_order WHERE order_id = #{orderId} AND user_id = #{userId}")
    Order selectByOrderIdAndUserId(@Param("orderId") String orderId,
                                   @Param("userId") Long userId);

    /**
     * 更新订单状态
     */
    @Update("UPDATE tb_order SET order_status = #{status} WHERE order_id = #{orderId}")
    int updateStatus(@Param("orderId") String orderId, @Param("status") Integer status);

    /**
     * 支付订单
     */
    @Update("UPDATE tb_order SET order_status = 1, pay_time = NOW() " +
            "WHERE order_id = #{orderId} AND order_status = 0")
    int payOrder(@Param("orderId") String orderId);

    /**
     * 发货
     */
    @Update("UPDATE tb_order SET order_status = 2, deliver_time = NOW() " +
            "WHERE order_id = #{orderId} AND order_status = 1")
    int deliverOrder(@Param("orderId") String orderId);

    /**
     * 确认收货
     */
    @Update("UPDATE tb_order SET order_status = 3, receive_time = NOW() " +
            "WHERE order_id = #{orderId} AND order_status = 2")
    int receiveOrder(@Param("orderId") String orderId);

    /**
     * 取消订单
     */
    @Update("UPDATE tb_order SET order_status = 4 " +
            "WHERE order_id = #{orderId} AND order_status IN (0, 1)")
    int cancelOrder(@Param("orderId") String orderId);

    /**
     * 统计用户各状态订单数量
     */
    @Select("SELECT order_status, COUNT(*) AS count FROM tb_order " +
            "WHERE user_id = #{userId} GROUP BY order_status")
    List<java.util.Map<String, Object>> countByStatusForUser(@Param("userId") Long userId);

    /**
     * 统计所有订单（管理员）
     */
    @Select("SELECT COUNT(*) FROM tb_order")
    Long countTotal();

    /**
     * 统计各状态订单数量
     */
    @Select("SELECT order_status, COUNT(*) AS count FROM tb_order GROUP BY order_status")
    List<java.util.Map<String, Object>> countByStatus();

    /**
     * 统计总销售额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM tb_order " +
            "WHERE order_status IN (1, 2, 3)")
    BigDecimal sumTotalSales();

    /**
     * 统计今日订单数
     */
    @Select("SELECT COUNT(*) FROM tb_order WHERE DATE(create_time) = CURDATE()")
    Long countTodayOrders();

    /**
     * 统计今日销售额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM tb_order " +
            "WHERE DATE(create_time) = CURDATE() AND order_status IN (1, 2, 3)")
    BigDecimal sumTodaySales();

    /**
     * 统计指定时间段订单数
     */
    @Select("SELECT COUNT(*) FROM tb_order " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime}")
    Long countByDateRange(@Param("startTime") LocalDateTime startTime,
                          @Param("endTime") LocalDateTime endTime);

    /**
     * 统计指定时间段销售额
     */
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM tb_order " +
            "WHERE create_time BETWEEN #{startTime} AND #{endTime} " +
            "AND order_status IN (1, 2, 3)")
    BigDecimal sumSalesByDateRange(@Param("startTime") LocalDateTime startTime,
                                   @Param("endTime") LocalDateTime endTime);

    /**
     * 获取超时未支付订单（30分钟前创建且未支付）
     */
    @Select("SELECT order_id FROM tb_order " +
            "WHERE order_status = 0 AND create_time < DATE_SUB(NOW(), INTERVAL 30 MINUTE)")
    List<String> findTimeoutPendingOrders();
}