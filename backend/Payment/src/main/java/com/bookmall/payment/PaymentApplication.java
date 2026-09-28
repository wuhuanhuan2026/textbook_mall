package com.bookmall.payment;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 支付服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@MapperScan("com.bookmall.payment.mapper")
@ComponentScan(basePackages = {"com.bookmall.payment", "com.bookmall.common"})
public class PaymentApplication {

    public static void main(String[] args) {
        SpringApplication.run(PaymentApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Payment模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8086");
        System.out.println("  📖 API文档: http://localhost:8086/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8086/druid");
        System.out.println("========================================");
    }
}