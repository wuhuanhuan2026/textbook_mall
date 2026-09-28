package com.bookmall.cart;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 购物车服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.cart.mapper")
@ComponentScan(basePackages = {"com.bookmall.cart", "com.bookmall.common"})
public class CartApplication {

    public static void main(String[] args) {
        SpringApplication.run(CartApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Cart模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8084");
        System.out.println("  📖 API文档: http://localhost:8084/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8084/druid");
        System.out.println("========================================");
    }
}