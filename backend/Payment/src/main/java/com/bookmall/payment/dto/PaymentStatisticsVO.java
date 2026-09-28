package com.bookmall.payment.dto;

import io.swagger.v3.oas.annotations.media.Schema;
import lombok.Builder;
import lombok.Data;

import java.math.BigDecimal;
import java.util.Map;

/**
 * 支付统计信息VO
 *
 * @author BookMall Team
 */
@Data
@Builder
@Schema(description = "支付统计信息")
public class PaymentStatisticsVO {

    @Schema(description = "总支付笔数")
    private Long totalPayments;

    @Schema(description = "成功支付笔数")
    private Long successPayments;

    @Schema(description = "失败支付笔数")
    private Long failedPayments;

    @Schema(description = "总支付金额")
    private BigDecimal totalAmount;

    @Schema(description = "成功支付金额")
    private BigDecimal successAmount;

    @Schema(description = "今日支付笔数")
    private Long todayPayments;

    @Schema(description = "今日支付金额")
    private BigDecimal todayAmount;

    @Schema(description = "各支付方式统计")
    private Map<Integer, PaymentMethodStats> methodStats;

    @Data
    @Builder
    public static class PaymentMethodStats {
        private Long count;
        private BigDecimal amount;
        private String methodName;
    }
}