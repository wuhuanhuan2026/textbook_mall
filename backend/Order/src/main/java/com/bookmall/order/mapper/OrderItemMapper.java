package com.bookmall.order.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.bookmall.order.entity.OrderItem;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;

import java.util.List;

/**
 * 订单详情数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface OrderItemMapper extends BaseMapper<OrderItem> {

    /**
     * 根据订单号查询订单详情列表
     */
    @Select("SELECT item_id, order_id, book_id, book_name, book_cover, " +
            "price, quantity, total_price " +
            "FROM tb_order_item WHERE order_id = #{orderId}")
    List<OrderItem> selectByOrderId(@Param("orderId") String orderId);

    /**
     * 批量插入订单详情
     */
    int batchInsert(@Param("items") List<OrderItem> items);

    /**
     * 根据订单号删除订单详情
     */
    @Select("DELETE FROM tb_order_item WHERE order_id = #{orderId}")
    int deleteByOrderId(@Param("orderId") String orderId);

    /**
     * 统计某个教材的销量
     */
    @Select("SELECT IFNULL(SUM(quantity), 0) FROM tb_order_item " +
            "WHERE book_id = #{bookId}")
    Integer sumSalesByBookId(@Param("bookId") Long bookId);

    /**
     * 统计某个用户购买某教材的总数（用于评价检测）
     */
    @Select("SELECT IFNULL(SUM(quantity), 0) FROM tb_order_item oi " +
            "INNER JOIN tb_order o ON oi.order_id = o.order_id " +
            "WHERE oi.book_id = #{bookId} AND o.user_id = #{userId} " +
            "AND o.order_status IN (1, 2, 3)")
    Integer countUserPurchased(@Param("userId") Long userId,
                               @Param("bookId") Long bookId);
}