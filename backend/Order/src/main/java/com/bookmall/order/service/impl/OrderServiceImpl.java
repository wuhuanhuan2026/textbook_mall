package com.bookmall.order.service.impl;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.common.constant.Constants;
import com.bookmall.common.entity.Result;
import com.bookmall.common.exception.BusinessException;
import com.bookmall.common.exception.ExceptionEnum;
import com.bookmall.common.utils.RedisUtil;
import com.bookmall.common.utils.SnowflakeIdWorker;
import com.bookmall.order.dto.*;
import com.bookmall.order.entity.Order;
import com.bookmall.order.entity.OrderItem;
import com.bookmall.order.enums.OrderStatus;
import com.bookmall.order.feign.*;
import com.bookmall.order.mapper.OrderItemMapper;
import com.bookmall.order.mapper.OrderMapper;
import com.bookmall.order.service.OrderService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

/**
 * 订单服务实现类
 *
 * @author BookMall Team
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class OrderServiceImpl implements OrderService {

    private final OrderMapper orderMapper;
    private final OrderItemMapper orderItemMapper;
    private final SnowflakeIdWorker snowflakeIdWorker;
    private final RedisUtil redisUtil;

    // Feign Clients
    private final BookFeignClient bookFeignClient;
    private final UserFeignClient userFeignClient;
    private final CartFeignClient cartFeignClient;
    private final AddressFeignClient addressFeignClient;

    // Redis Key前缀 - 订单锁
    private static final String REDIS_ORDER_LOCK_KEY = Constants.REDIS_ORDER_LOCK;

    // ==================== 用户端 ====================

    @Override
    @Transactional
    public OrderDTO createOrderFromCart(Long userId, OrderCreateDTO request) {
        // 1. 获取Redis分布式锁（防止重复提交）
        String lockKey = REDIS_ORDER_LOCK_KEY + userId;
        Boolean locked = redisUtil.setIfAbsent(lockKey, "1", 10, TimeUnit.SECONDS);
        if (locked == null || !locked) {
            throw new BusinessException("请勿重复提交订单");
        }

        try {
            // 2. 验证用户
            validateUser(userId);

            // 3. 获取收货地址
            Map<String, Object> address = getAddress(userId, request.getAddressId());

            // 4. 获取购物车商品
            List<Map<String, Object>> cartItems = getCartItems(userId, request.getCartIds());

            if (cartItems == null || cartItems.isEmpty()) {
                throw new BusinessException("购物车为空，请选择商品");
            }

            // 5. 创建订单
            return createOrderFromItems(userId, cartItems, address, request.getRemark());
        } finally {
            // 释放锁
            redisUtil.delete(lockKey);
        }
    }

    @Override
    @Transactional
    public OrderDTO createOrderDirect(Long userId, Long bookId, Integer quantity, Long addressId, String remark) {
        // 1. 验证用户
        validateUser(userId);

        // 2. 验证教材
        Result<Object> bookResult = bookFeignClient.getBookDetail(bookId);
        if (bookResult == null || !bookResult.isSuccess() || bookResult.getData() == null) {
            throw new BusinessException(ExceptionEnum.BOOK_NOT_EXIST);
        }
        Map<String, Object> book = (Map<String, Object>) bookResult.getData();

        // 验证库存
        Integer stock = (Integer) book.get("stock");
        if (stock == null || stock < quantity) {
            throw new BusinessException(ExceptionEnum.STOCK_INSUFFICIENT);
        }

        // 验证是否上架
        Integer status = (Integer) book.get("status");
        if (status == null || status != Constants.BOOK_STATUS_ON) {
            throw new BusinessException(ExceptionEnum.BOOK_OFF_SALE);
        }

        // 3. 获取收货地址
        Map<String, Object> address = getAddress(userId, addressId);

        // 4. 构建购物车项格式（Java 8 兼容）
        List<Map<String, Object>> cartItems = new ArrayList<>();
        Map<String, Object> item = new HashMap<>();
        item.put("bookId", bookId);
        item.put("bookName", book.get("bookName"));
        item.put("bookCover", book.get("coverImage"));
        item.put("price", book.get("price"));
        item.put("quantity", quantity);
        item.put("stock", stock);
        cartItems.add(item);

        // 5. 创建订单
        return createOrderFromItems(userId, cartItems, address, remark);
    }

    /**
     * 从商品列表创建订单
     */
    private OrderDTO createOrderFromItems(Long userId, List<Map<String, Object>> items,
                                          Map<String, Object> address, String remark) {
        // 1. 生成订单号
        String orderId = generateOrderId();

        // 2. 计算总金额
        BigDecimal totalAmount = BigDecimal.ZERO;
        List<OrderItem> orderItems = new ArrayList<>();

        for (Map<String, Object> item : items) {
            Long bookId = ((Number) item.get("bookId")).longValue();
            Integer quantity = (Integer) item.get("quantity");
            BigDecimal price = new BigDecimal(item.get("price").toString());
            BigDecimal subtotal = price.multiply(BigDecimal.valueOf(quantity));
            totalAmount = totalAmount.add(subtotal);

            OrderItem orderItem = new OrderItem();
            orderItem.setOrderId(orderId);
            orderItem.setBookId(bookId);
            orderItem.setBookName((String) item.get("bookName"));
            orderItem.setBookCover((String) item.get("bookCover"));
            orderItem.setPrice(price);
            orderItem.setQuantity(quantity);
            orderItem.setTotalPrice(subtotal);
            orderItems.add(orderItem);

            // 扣减库存
            Result<Void> deductResult = bookFeignClient.deductStock(bookId, quantity);
            if (deductResult == null || !deductResult.isSuccess()) {
                throw new BusinessException("库存扣减失败：" +
                        (deductResult != null ? deductResult.getMessage() : "服务不可用"));
            }

            // 增加销量
            bookFeignClient.addSales(bookId, quantity);
        }

        // 3. 创建订单
        Order order = new Order();
        order.setOrderId(orderId);
        order.setUserId(userId);
        order.setTotalAmount(totalAmount);
        order.setShippingFee(BigDecimal.ZERO);
        order.setOrderStatus(OrderStatus.PENDING.getCode());
        order.setPayType(Constants.PAY_TYPE_BALANCE);
        order.setReceiverName((String) address.get("receiverName"));
        order.setReceiverPhone((String) address.get("receiverPhone"));
        order.setReceiverAddress((String) address.get("fullAddress"));
        order.setRemark(remark);

        orderMapper.insert(order);

        // 4. 插入订单详情
        orderItemMapper.batchInsert(orderItems);

        // 5. 清空购物车（从购物车创建时）
        List<Long> cartIds = items.stream()
                .map(item -> (Long) item.get("cartId"))
                .filter(id -> id != null)
                .collect(Collectors.toList());
        if (!cartIds.isEmpty()) {
            cartFeignClient.batchDeleteCartItems(userId, cartIds);
        }

        log.info("订单创建成功: orderId={}, userId={}, totalAmount={}",
                orderId, userId, totalAmount);

        return convertToDTO(order, orderItems);
    }

    @Override
    public Page<OrderDTO> getUserOrders(Long userId, Integer status, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderMapper.selectUserOrderPage(page, userId, status);

        Page<OrderDTO> dtoPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        List<OrderDTO> dtoList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
                    return convertToDTO(order, items);
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    @Override
    public OrderDTO getOrderDetail(Long userId, String orderId) {
        // 验证订单是否属于该用户
        if (!belongsToUser(orderId, userId)) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectOrderDetail(orderId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public void payOrder(Long userId, String orderId) {
        // 1. 验证订单
        if (!belongsToUser(orderId, userId)) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        // 2. 验证订单状态
        if (order.getOrderStatus() != OrderStatus.PENDING.getCode()) {
            throw new BusinessException(ExceptionEnum.ORDER_CANNOT_PAY);
        }

        // 3. 扣减余额
        Result<Void> balanceResult = userFeignClient.deductBalance(userId, order.getTotalAmount());
        if (balanceResult == null || !balanceResult.isSuccess()) {
            throw new BusinessException(balanceResult != null ? balanceResult.getMessage() : "余额扣减失败");
        }

        // 4. 更新订单状态
        int result = orderMapper.payOrder(orderId);
        if (result <= 0) {
            throw new BusinessException(ExceptionEnum.PAY_ERROR);
        }

        log.info("订单支付成功: orderId={}, userId={}, amount={}",
                orderId, userId, order.getTotalAmount());
    }

    @Override
    @Transactional
    public void cancelOrder(Long userId, String orderId) {
        if (!belongsToUser(orderId, userId)) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        // 只有待付款和待发货可以取消
        if (order.getOrderStatus() != OrderStatus.PENDING.getCode() &&
                order.getOrderStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException(ExceptionEnum.ORDER_CANNOT_CANCEL);
        }

        // 如果已支付，需要退款
        if (order.getOrderStatus() == OrderStatus.PAID.getCode()) {
            // 退还余额
            Result<Void> refundResult = userFeignClient.addBalance(userId, order.getTotalAmount());
            if (refundResult == null || !refundResult.isSuccess()) {
                log.warn("退款失败: orderId={}, userId={}", orderId, userId);
                throw new BusinessException("退款失败，请稍后重试");
            }
        }

        // 更新状态
        int result = orderMapper.cancelOrder(orderId);
        if (result <= 0) {
            throw new BusinessException("取消订单失败");
        }

        log.info("订单取消成功: orderId={}, userId={}", orderId, userId);
    }

    @Override
    @Transactional
    public void confirmReceive(Long userId, String orderId) {
        if (!belongsToUser(orderId, userId)) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        Order order = orderMapper.selectByOrderIdAndUserId(orderId, userId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }

        if (order.getOrderStatus() != OrderStatus.SHIPPED.getCode()) {
            throw new BusinessException("订单状态不正确，无法确认收货");
        }

        int result = orderMapper.receiveOrder(orderId);
        if (result <= 0) {
            throw new BusinessException("确认收货失败");
        }

        log.info("确认收货成功: orderId={}, userId={}", orderId, userId);
    }

    // ==================== 管理员端 ====================

    @Override
    public Page<OrderDTO> getAllOrders(Long userId, Integer status, LocalDateTime startTime,
                                       LocalDateTime endTime, Integer pageNum, Integer pageSize) {
        Page<Order> page = new Page<>(pageNum, pageSize);
        Page<Order> orderPage = orderMapper.selectAllOrderPage(page, userId, status, startTime, endTime);

        Page<OrderDTO> dtoPage = new Page<>(pageNum, pageSize, orderPage.getTotal());
        List<OrderDTO> dtoList = orderPage.getRecords().stream()
                .map(order -> {
                    List<OrderItem> items = orderItemMapper.selectByOrderId(order.getOrderId());
                    return convertToDTO(order, items);
                })
                .collect(Collectors.toList());
        dtoPage.setRecords(dtoList);

        return dtoPage;
    }

    /**
     * 获取订单详情（管理员，不校验 userId）
     */
    @Override
    public OrderDTO getOrderDetailByAdmin(String orderId) {
        Order order = orderMapper.selectOrderDetail(orderId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }
        List<OrderItem> items = orderItemMapper.selectByOrderId(orderId);
        return convertToDTO(order, items);
    }

    @Override
    @Transactional
    public void deliverOrder(String orderId) {
        Order order = getOrderEntity(orderId);
        if (order.getOrderStatus() != OrderStatus.PAID.getCode()) {
            throw new BusinessException("订单状态不正确，无法发货");
        }

        int result = orderMapper.deliverOrder(orderId);
        if (result <= 0) {
            throw new BusinessException("发货失败");
        }

        log.info("发货成功: orderId={}", orderId);
    }

    @Override
    @Transactional
    public void cancelOrderByAdmin(String orderId) {
        Order order = getOrderEntity(orderId);
        if (order.getOrderStatus() == OrderStatus.COMPLETED.getCode()) {
            throw new BusinessException("已完成订单无法取消");
        }

        // 如果已支付，需要退款
        if (order.getOrderStatus() == OrderStatus.PAID.getCode() ||
                order.getOrderStatus() == OrderStatus.SHIPPED.getCode()) {
            Result<Void> refundResult = userFeignClient.addBalance(order.getUserId(), order.getTotalAmount());
            if (refundResult == null || !refundResult.isSuccess()) {
                log.warn("管理员退款失败: orderId={}", orderId);
                throw new BusinessException("退款失败，请稍后重试");
            }
        }

        int result = orderMapper.cancelOrder(orderId);
        if (result <= 0) {
            throw new BusinessException("取消订单失败");
        }

        log.info("管理员取消订单成功: orderId={}", orderId);
    }

    // ==================== 统计 ====================

    @Override
    public OrderStatisticsVO getOrderStatistics() {
        Long totalOrders = orderMapper.countTotal();
        List<Map<String, Object>> statusCounts = orderMapper.countByStatus();

        Long pending = 0L, paid = 0L, shipped = 0L, completed = 0L, cancelled = 0L;
        for (Map<String, Object> map : statusCounts) {
            Integer status = (Integer) map.get("order_status");
            Long count = (Long) map.get("count");
            if (status != null && count != null) {
                switch (status) {
                    case 0: pending = count; break;
                    case 1: paid = count; break;
                    case 2: shipped = count; break;
                    case 3: completed = count; break;
                    case 4: cancelled = count; break;
                    default: break;
                }
            }
        }

        BigDecimal totalSales = orderMapper.sumTotalSales();
        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        Long todayOrders = orderMapper.countTodayOrders();
        if (todayOrders == null) {
            todayOrders = 0L;
        }

        BigDecimal todaySales = orderMapper.sumTodaySales();
        if (todaySales == null) {
            todaySales = BigDecimal.ZERO;
        }

        Long paidCompleted = paid + shipped + completed;
        BigDecimal avgAmount = paidCompleted > 0 ?
                totalSales.divide(BigDecimal.valueOf(paidCompleted), 2, BigDecimal.ROUND_HALF_UP) :
                BigDecimal.ZERO;

        return OrderStatisticsVO.builder()
                .totalOrders(totalOrders != null ? totalOrders : 0L)
                .pendingOrders(pending)
                .paidOrders(paid)
                .shippedOrders(shipped)
                .completedOrders(completed)
                .cancelledOrders(cancelled)
                .totalSales(totalSales)
                .avgAmount(avgAmount)
                .todayOrders(todayOrders)
                .todaySales(todaySales)
                .build();
    }

    @Override
    public List<Map<String, Object>> countUserOrdersByStatus(Long userId) {
        List<Map<String, Object>> result = orderMapper.countByStatusForUser(userId);
        return result != null ? result : new ArrayList<>();
    }

    // ==================== 定时任务 ====================

    @Override
    @Transactional
    public void cancelTimeoutOrders() {
        List<String> timeoutOrderIds = orderMapper.findTimeoutPendingOrders();
        if (timeoutOrderIds == null || timeoutOrderIds.isEmpty()) {
            return;
        }

        int successCount = 0;
        for (String orderId : timeoutOrderIds) {
            try {
                int result = orderMapper.cancelOrder(orderId);
                if (result > 0) {
                    successCount++;
                    log.info("定时任务取消超时订单: orderId={}", orderId);
                }
            } catch (Exception e) {
                log.error("取消超时订单失败: orderId={}, error={}", orderId, e.getMessage(), e);
            }
        }

        log.info("定时任务取消超时订单完成，共处理 {} 个订单，成功取消 {} 个",
                timeoutOrderIds.size(), successCount);
    }

    // ==================== 内部调用 ====================

    @Override
    public Order getOrderEntity(String orderId) {
        Order order = orderMapper.selectById(orderId);
        if (order == null) {
            throw new BusinessException(ExceptionEnum.ORDER_NOT_EXIST);
        }
        return order;
    }

    @Override
    public boolean belongsToUser(String orderId, Long userId) {
        Order order = orderMapper.selectById(orderId);
        return order != null && order.getUserId().equals(userId);
    }

    @Override
    @Transactional
    public void paySuccess(String orderId) {
        // 1. 获取订单
        Order order = getOrderEntity(orderId);

        // 2. 验证订单状态（只有待付款才能支付成功）
        if (order.getOrderStatus() != OrderStatus.PENDING.getCode()) {
            throw new BusinessException("订单状态不正确，无法支付");
        }

        // 3. 更新订单状态为已支付/待发货
        int result = orderMapper.payOrder(orderId);
        if (result <= 0) {
            throw new BusinessException("订单支付状态更新失败");
        }

        log.info("订单支付成功（回调）: orderId={}, userId={}, amount={}",
                orderId, order.getUserId(), order.getTotalAmount());
    }

    // ==================== 私有方法 ====================

    /**
     * 验证用户
     */
    private void validateUser(Long userId) {
        Result<Boolean> result = userFeignClient.checkUserExists(userId);
        if (result == null || !result.isSuccess() || result.getData() == null || !result.getData()) {
            throw new BusinessException(ExceptionEnum.USER_NOT_EXIST);
        }
    }

    /**
     * 获取收货地址
     */
    private Map<String, Object> getAddress(Long userId, Long addressId) {
        Result<Object> result = addressFeignClient.getAddressById(userId, addressId);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new BusinessException("收货地址不存在");
        }
        return (Map<String, Object>) result.getData();
    }

    /**
     * 获取购物车商品列表
     */
    private List<Map<String, Object>> getCartItems(Long userId, List<Long> cartIds) {
        Result<List<Object>> result = cartFeignClient.getCartList(userId);
        if (result == null || !result.isSuccess() || result.getData() == null) {
            throw new BusinessException("获取购物车失败");
        }

        List<Object> allItems = result.getData();
        if (allItems.isEmpty()) {
            throw new BusinessException(ExceptionEnum.CART_EMPTY);
        }

        List<Map<String, Object>> items = new ArrayList<>();
        for (Object obj : allItems) {
            Map<String, Object> item = (Map<String, Object>) obj;
            Long cartId = ((Number) item.get("cartId")).longValue();

            // 如果指定了cartIds，只取选中的
            if (cartIds != null && !cartIds.isEmpty()) {
                if (!cartIds.contains(cartId)) {
                    continue;
                }
            }

            // 验证库存
            Integer stock = (Integer) item.get("stock");
            Integer quantity = (Integer) item.get("quantity");
            if (stock == null || quantity == null || stock < quantity) {
                throw new BusinessException("商品【" + item.get("bookName") + "】库存不足");
            }

            // 验证是否上架
            Boolean onSale = (Boolean) item.get("onSale");
            if (onSale == null || !onSale) {
                throw new BusinessException("商品【" + item.get("bookName") + "】已下架");
            }

            // 添加cartId到item中，用于后续删除
            item.put("cartId", cartId);
            items.add(item);
        }

        if (items.isEmpty()) {
            throw new BusinessException("没有选中的商品");
        }

        return items;
    }

    /**
     * 生成订单号
     * 格式：yyyyMMddHHmmss + 6位雪花ID取模
     */
    private String generateOrderId() {
        String timestamp = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"));
        String seq = String.format("%06d", snowflakeIdWorker.nextId() % 1000000);
        return timestamp + seq;
    }

    /**
     * 实体转DTO
     */
    private OrderDTO convertToDTO(Order order, List<OrderItem> items) {
        if (order == null) {
            return null;
        }

        List<OrderItemDTO> itemDTOs = items != null ? items.stream()
                .map(this::convertItemToDTO)
                .collect(Collectors.toList()) : new ArrayList<>();

        return OrderDTO.builder()
                .orderId(order.getOrderId())
                .userId(order.getUserId())
                .username(order.getUsername())
                .totalAmount(order.getTotalAmount())
                .shippingFee(order.getShippingFee())
                .orderStatus(order.getOrderStatus())
                .statusDesc(order.getStatusDesc() != null ? order.getStatusDesc() :
                        OrderStatus.getDesc(order.getOrderStatus()))
                .payType(order.getPayType())
                .receiverName(order.getReceiverName())
                .receiverPhone(order.getReceiverPhone())
                .receiverAddress(order.getReceiverAddress())
                .remark(order.getRemark())
                .payTime(order.getPayTime())
                .deliverTime(order.getDeliverTime())
                .receiveTime(order.getReceiveTime())
                .createTime(order.getCreateTime())
                .items(itemDTOs)
                .build();
    }

    /**
     * 订单详情实体转DTO
     */
    private OrderItemDTO convertItemToDTO(OrderItem item) {
        if (item == null) {
            return null;
        }
        return OrderItemDTO.builder()
                .itemId(item.getItemId())
                .orderId(item.getOrderId())
                .bookId(item.getBookId())
                .bookName(item.getBookName())
                .bookCover(item.getBookCover())
                .price(item.getPrice())
                .quantity(item.getQuantity())
                .totalPrice(item.getTotalPrice())
                .build();
    }

    // ==================== 管理员补充操作 ====================

    @Override
    @Transactional
    public void updateOrderRemark(String orderId, String remark) {
        Order order = getOrderEntity(orderId);
        order.setRemark(remark);
        orderMapper.updateById(order);
        log.info("订单备注修改成功: orderId={}, remark={}", orderId, remark);
    }

    @Override
    @Transactional
    public void updateReceiverInfo(String orderId, Map<String, String> receiverInfo) {
        Order order = getOrderEntity(orderId);
        if (receiverInfo.containsKey("receiverName")) {
            order.setReceiverName(receiverInfo.get("receiverName"));
        }
        if (receiverInfo.containsKey("receiverPhone")) {
            order.setReceiverPhone(receiverInfo.get("receiverPhone"));
        }
        if (receiverInfo.containsKey("receiverAddress")) {
            order.setReceiverAddress(receiverInfo.get("receiverAddress"));
        }
        orderMapper.updateById(order);
        log.info("订单收货信息修改成功: orderId={}", orderId);
    }

    // ==================== Feign 调用需要的统计数据 ====================

    @Override
    public Map<String, Object> getOrderStatisticsMap() {
        Map<String, Object> result = new HashMap<>();

        Long totalOrders = orderMapper.countTotal();
        List<Map<String, Object>> statusCounts = orderMapper.countByStatus();

        long pending = 0, paid = 0, shipped = 0, completed = 0, cancelled = 0;
        for (Map<String, Object> map : statusCounts) {
            Integer status = (Integer) map.get("order_status");
            Long count = (Long) map.get("count");
            if (status != null && count != null) {
                switch (status) {
                    case 0: pending = count; break;
                    case 1: paid = count; break;
                    case 2: shipped = count; break;
                    case 3: completed = count; break;
                    case 4: cancelled = count; break;
                    default: break;
                }
            }
        }

        BigDecimal totalSales = orderMapper.sumTotalSales();
        if (totalSales == null) {
            totalSales = BigDecimal.ZERO;
        }

        Long todayOrders = orderMapper.countTodayOrders();
        if (todayOrders == null) {
            todayOrders = 0L;
        }

        BigDecimal todaySales = orderMapper.sumTodaySales();
        if (todaySales == null) {
            todaySales = BigDecimal.ZERO;
        }

        result.put("totalOrders", totalOrders != null ? totalOrders : 0L);
        result.put("pendingOrders", pending);
        result.put("paidOrders", paid);
        result.put("shippedOrders", shipped);
        result.put("completedOrders", completed);
        result.put("cancelledOrders", cancelled);
        result.put("totalSales", totalSales);
        result.put("todayOrders", todayOrders);
        result.put("todaySales", todaySales);
        return result;
    }

    @Override
    public Map<Integer, Long> countByStatusMap() {
        Map<Integer, Long> result = new HashMap<>();
        List<Map<String, Object>> statusCounts = orderMapper.countByStatus();
        for (Map<String, Object> map : statusCounts) {
            Integer status = (Integer) map.get("order_status");
            Long count = (Long) map.get("count");
            if (status != null && count != null) {
                result.put(status, count);
            }
        }
        // 保证所有状态都有值
        for (int i = 0; i <= 4; i++) {
            result.putIfAbsent(i, 0L);
        }
        return result;
    }

    @Override
    public Map<String, Object> getUserOrderStatistics(Long userId) {
        Map<String, Object> result = new HashMap<>();
        List<Map<String, Object>> counts = orderMapper.countByStatusForUser(userId);

        long pending = 0, paid = 0, shipped = 0, completed = 0, cancelled = 0;
        for (Map<String, Object> map : counts) {
            Integer status = (Integer) map.get("order_status");
            Long count = (Long) map.get("count");
            if (status != null && count != null) {
                switch (status) {
                    case 0: pending = count; break;
                    case 1: paid = count; break;
                    case 2: shipped = count; break;
                    case 3: completed = count; break;
                    case 4: cancelled = count; break;
                    default: break;
                }
            }
        }

        result.put("userId", userId);
        result.put("pendingOrders", pending);
        result.put("paidOrders", paid);
        result.put("shippedOrders", shipped);
        result.put("completedOrders", completed);
        result.put("cancelledOrders", cancelled);
        result.put("totalOrders", pending + paid + shipped + completed + cancelled);
        return result;
    }

    @Override
    public Map<String, Object> getTodayStatistics() {
        Map<String, Object> result = new HashMap<>();
        Long todayOrders = orderMapper.countTodayOrders();
        BigDecimal todaySales = orderMapper.sumTodaySales();
        result.put("todayOrders", todayOrders != null ? todayOrders : 0L);
        result.put("todaySales", todaySales != null ? todaySales : BigDecimal.ZERO);
        return result;
    }

    @Override
    public Map<String, Object> getRangeStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        Map<String, Object> result = new HashMap<>();
        Long totalOrders = orderMapper.countByDateRange(startTime, endTime);
        BigDecimal totalSales = orderMapper.sumSalesByDateRange(startTime, endTime);
        result.put("startTime", startTime);
        result.put("endTime", endTime);
        result.put("totalOrders", totalOrders != null ? totalOrders : 0L);
        result.put("totalSales", totalSales != null ? totalSales : BigDecimal.ZERO);
        return result;
    }

}