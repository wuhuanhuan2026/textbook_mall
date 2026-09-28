package com.bookmall.payment.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.bookmall.payment.entity.Payment;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;
import org.apache.ibatis.annotations.Select;
import org.apache.ibatis.annotations.Update;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 支付数据访问层
 *
 * @author BookMall Team
 */
@Mapper
public interface PaymentMapper extends BaseMapper<Payment> {

    /**
     * 根据支付单号查询
     */
    @Select("SELECT payment_id, payment_no, order_id, user_id, amount, pay_type, " +
            "status, transaction_id, callback_data, pay_time, expire_time, create_time, update_time " +
            "FROM tb_payment WHERE payment_no = #{paymentNo}")
    Payment selectByPaymentNo(@Param("paymentNo") String paymentNo);

    /**
     * 根据订单号查询
     */
    @Select("SELECT payment_id, payment_no, order_id, user_id, amount, pay_type, " +
            "status, transaction_id, callback_data, pay_time, expire_time, create_time, update_time " +
            "FROM tb_payment WHERE order_id = #{orderId}")
    Payment selectByOrderId(@Param("orderId") String orderId);

    /**
     * 分页查询用户支付记录
     */
    Page<Payment> selectUserPaymentPage(Page<Payment> page,
                                        @Param("userId") Long userId,
                                        @Param("status") Integer status);

    /**
     * 分页查询所有支付记录（管理员）
     */
    Page<Payment> selectAllPaymentPage(Page<Payment> page,
                                       @Param("userId") Long userId,
                                       @Param("status") Integer status,
                                       @Param("startTime") LocalDateTime startTime,
                                       @Param("endTime") LocalDateTime endTime);

    /**
     * 更新支付状态
     */
    @Update("UPDATE tb_payment SET status = #{status} WHERE payment_no = #{paymentNo}")
    int updateStatus(@Param("paymentNo") String paymentNo,
                     @Param("status") Integer status);

    /**
     * 支付成功回调更新
     */
    @Update("UPDATE tb_payment SET status = 2, transaction_id = #{transactionId}, " +
            "callback_data = #{callbackData}, pay_time = NOW() " +
            "WHERE payment_no = #{paymentNo} AND status = 0")
    int paySuccess(@Param("paymentNo") String paymentNo,
                   @Param("transactionId") String transactionId,
                   @Param("callbackData") String callbackData);

    /**
     * 支付失败回调更新
     */
    @Update("UPDATE tb_payment SET status = 3, callback_data = #{callbackData} " +
            "WHERE payment_no = #{paymentNo} AND status = 0")
    int payFailed(@Param("paymentNo") String paymentNo,
                  @Param("callbackData") String callbackData);

    /**
     * 取消支付
     */
    @Update("UPDATE tb_payment SET status = 4 WHERE payment_no = #{paymentNo} AND status = 0")
    int cancelPayment(@Param("paymentNo") String paymentNo);

    /**
     * 开始退款
     */
    @Update("UPDATE tb_payment SET status = 5 WHERE payment_no = #{paymentNo} AND status = 2")
    int startRefund(@Param("paymentNo") String paymentNo);

    /**
     * 退款完成
     */
    @Update("UPDATE tb_payment SET status = 6 WHERE payment_no = #{paymentNo} AND status = 5")
    int refundComplete(@Param("paymentNo") String paymentNo);

    /**
     * 统计总支付笔数
     */
    @Select("SELECT COUNT(*) FROM tb_payment")
    Long countTotal();

    /**
     * 统计成功支付笔数
     */
    @Select("SELECT COUNT(*) FROM tb_payment WHERE status = 2")
    Long countSuccess();

    /**
     * 统计总支付金额
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM tb_payment")
    BigDecimal sumTotalAmount();

    /**
     * 统计成功支付金额
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM tb_payment WHERE status = 2")
    BigDecimal sumSuccessAmount();

    /**
     * 统计今日支付笔数
     */
    @Select("SELECT COUNT(*) FROM tb_payment WHERE DATE(create_time) = CURDATE()")
    Long countTodayPayments();

    /**
     * 统计今日支付金额
     */
    @Select("SELECT IFNULL(SUM(amount), 0) FROM tb_payment " +
            "WHERE DATE(create_time) = CURDATE() AND status = 2")
    BigDecimal sumTodayAmount();

    /**
     * 按支付方式统计
     */
    @Select("SELECT pay_type, COUNT(*) AS count, IFNULL(SUM(amount), 0) AS amount " +
            "FROM tb_payment WHERE status = 2 GROUP BY pay_type")
    List<Map<String, Object>> statsByPayType();

    /**
     * 获取超时未支付记录（30分钟前创建且状态为待支付）
     */
    @Select("SELECT payment_no FROM tb_payment " +
            "WHERE status = 0 AND create_time < DATE_SUB(NOW(), INTERVAL 30 MINUTE)")
    List<String> findTimeoutPendingPayments();
}