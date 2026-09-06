package com.bookmall.order;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableScheduling;

/**
 * 订单服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@EnableScheduling
@MapperScan("com.bookmall.order.mapper")
@ComponentScan(basePackages = {"com.bookmall.order", "com.bookmall.common"})
public class OrderApplication {

    public static void main(String[] args) {
        SpringApplication.run(OrderApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Order模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8085");
        System.out.println("  📖 API文档: http://localhost:8085/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8085/druid");
        System.out.println("========================================");
    }
}