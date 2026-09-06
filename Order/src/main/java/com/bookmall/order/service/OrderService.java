package com.bookmall.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.order.dto.OrderCreateDTO;
import com.bookmall.order.dto.OrderDTO;
import com.bookmall.order.dto.OrderStatisticsVO;
import com.bookmall.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;

/**
 * 订单服务接口
 *
 * @author BookMall Team
 */
public interface OrderService {

    // ==================== 用户端 ====================

    /**
     * 创建订单（从购物车）
     */
    OrderDTO createOrderFromCart(Long userId, OrderCreateDTO request);

    /**
     * 创建订单（直接购买）
     */
    OrderDTO createOrderDirect(Long userId, Long bookId, Integer quantity, Long addressId, String remark);

    /**
     * 分页查询用户订单列表
     */
    Page<OrderDTO> getUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    /**
     * 获取订单详情
     */
    OrderDTO getOrderDetail(Long userId, String orderId);

    /**
     * 支付订单（余额支付）
     */
    void payOrder(Long userId, String orderId);

    /**
     * 取消订单
     */
    void cancelOrder(Long userId, String orderId);

    /**
     * 确认收货
     */
    void confirmReceive(Long userId, String orderId);

    // ==================== 管理员端 ====================

    /**
     * 分页查询所有订单（管理员）
     */
    Page<OrderDTO> getAllOrders(Long userId, Integer status, LocalDateTime startTime,
                                LocalDateTime endTime, Integer pageNum, Integer pageSize);

    /**
     * 发货（管理员）
     */
    void deliverOrder(String orderId);

    /**
     * 取消订单（管理员）
     */
    void cancelOrderByAdmin(String orderId);

    // ==================== 统计 ====================

    /**
     * 获取订单统计信息
     */
    OrderStatisticsVO getOrderStatistics();

    /**
     * 统计用户各状态订单数量
     */
    List<java.util.Map<String, Object>> countUserOrdersByStatus(Long userId);

    // ==================== 定时任务 ====================

    /**
     * 取消超时未支付订单（30分钟）
     */
    void cancelTimeoutOrders();

    // ==================== 内部调用 ====================

    /**
     * 获取订单实体
     */
    Order getOrderEntity(String orderId);

    /**
     * 检查订单是否属于用户
     */
    boolean belongsToUser(String orderId, Long userId);

    /**
     * 支付成功回调（内部调用）
     */
    void paySuccess(String orderId);
}