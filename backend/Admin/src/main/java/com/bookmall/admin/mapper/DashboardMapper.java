package com.bookmall.admin.mapper;

import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Select;

import java.math.BigDecimal;

/**
 * 仪表盘统计 Mapper
 */
@Mapper
public interface DashboardMapper {

    // ---------- 用户相关 ----------
    @Select("SELECT COUNT(*) FROM tb_user")
    Long countTotalUsers();

    @Select("SELECT COUNT(*) FROM tb_user WHERE DATE(create_time) = CURDATE()")
    Long countTodayNewUsers();

    // ---------- 教材相关 ----------
    @Select("SELECT COUNT(*) FROM tb_book")
    Long countTotalBooks();

    @Select("SELECT COUNT(*) FROM tb_book WHERE status = 1")
    Long countOnSaleBooks();

    // ---------- 订单相关 ----------
    @Select("SELECT COUNT(*) FROM tb_order")
    Long countTotalOrders();

    @Select("SELECT COUNT(*) FROM tb_order WHERE DATE(create_time) = CURDATE()")
    Long countTodayOrders();

    // 0-待付款 1-待发货
    @Select("SELECT COUNT(*) FROM tb_order WHERE order_status IN (0, 1)")
    Long countPendingOrders();

    // 已完成订单(3)的销售额总和
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM tb_order WHERE order_status = 3")
    BigDecimal sumTotalSales();

    // 今日已完成订单的销售额
    @Select("SELECT IFNULL(SUM(total_amount), 0) FROM tb_order WHERE order_status = 3 AND DATE(create_time) = CURDATE()")
    BigDecimal sumTodaySales();

    // ---------- 评价相关 ----------
    @Select("SELECT COUNT(*) FROM tb_review")
    Long countTotalReviews();

    @Select("SELECT IFNULL(AVG(rating), 0) FROM tb_review")
    Double avgRating();

    // ---------- 支付相关 ----------
    // 2-支付成功
    @Select("SELECT IFNULL(SUM(amount), 0) FROM tb_payment WHERE status = 2")
    BigDecimal sumTotalPaymentAmount();

    @Select("SELECT IFNULL(SUM(amount), 0) FROM tb_payment WHERE status = 2 AND DATE(pay_time) = CURDATE()")
    BigDecimal sumTodayPaymentAmount();
}