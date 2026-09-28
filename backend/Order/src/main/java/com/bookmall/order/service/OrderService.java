package com.bookmall.order.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.order.dto.OrderCreateDTO;
import com.bookmall.order.dto.OrderDTO;
import com.bookmall.order.dto.OrderStatisticsVO;
import com.bookmall.order.entity.Order;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 订单服务接口
 *
 * @author BookMall Team
 */
public interface OrderService {

    // ==================== 用户端 ====================

    OrderDTO createOrderFromCart(Long userId, OrderCreateDTO request);

    OrderDTO createOrderDirect(Long userId, Long bookId, Integer quantity, Long addressId, String remark);

    Page<OrderDTO> getUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize);

    OrderDTO getOrderDetail(Long userId, String orderId);

    void payOrder(Long userId, String orderId);

    void cancelOrder(Long userId, String orderId);

    void confirmReceive(Long userId, String orderId);

    // ==================== 管理员端 ====================

    Page<OrderDTO> getAllOrders(Long userId, Integer status, LocalDateTime startTime,
                                LocalDateTime endTime, Integer pageNum, Integer pageSize);

    OrderDTO getOrderDetailByAdmin(String orderId);

    void deliverOrder(String orderId);

    void cancelOrderByAdmin(String orderId);

    // ✅ 新增：管理员补充操作

    void updateOrderRemark(String orderId, String remark);

    void updateReceiverInfo(String orderId, Map<String, String> receiverInfo);

    // ==================== 统计 ====================

    OrderStatisticsVO getOrderStatistics();

    List<Map<String, Object>> countUserOrdersByStatus(Long userId);

    // ✅ 新增：Feign 调用需要的统计数据

    Map<String, Object> getOrderStatisticsMap();

    Map<Integer, Long> countByStatusMap();

    Map<String, Object> getUserOrderStatistics(Long userId);

    Map<String, Object> getTodayStatistics();

    Map<String, Object> getRangeStatistics(LocalDateTime startTime, LocalDateTime endTime);

    // ==================== 定时任务 ====================

    void cancelTimeoutOrders();

    // ==================== 内部调用 ====================

    Order getOrderEntity(String orderId);

    boolean belongsToUser(String orderId, Long userId);

    void paySuccess(String orderId);
}