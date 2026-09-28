package com.bookmall.address;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 收货地址服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.address.mapper")
@ComponentScan(basePackages = {"com.bookmall.address", "com.bookmall.common"})
public class AddressApplication {

    public static void main(String[] args) {
        SpringApplication.run(AddressApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Address模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8088");
        System.out.println("  📖 API文档: http://localhost:8088/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8088/druid");
        System.out.println("========================================");
    }
}