package com.bookmall.book;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 教材服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.book.mapper")
@ComponentScan(basePackages = {"com.bookmall.book", "com.bookmall.common"})
public class BookApplication {

    public static void main(String[] args) {
        SpringApplication.run(BookApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Book模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8083");
        System.out.println("  📖 API文档: http://localhost:8083/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8083/druid");
        System.out.println("========================================");
    }
}