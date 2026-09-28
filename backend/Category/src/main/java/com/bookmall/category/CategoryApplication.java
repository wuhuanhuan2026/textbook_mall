package com.bookmall.category;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 分类服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.category.mapper")
@ComponentScan(basePackages = {"com.bookmall.category", "com.bookmall.common"})
public class CategoryApplication {

    public static void main(String[] args) {
        SpringApplication.run(CategoryApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Category模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8082");
        System.out.println("  📖 API文档: http://localhost:8082/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8082/druid");
        System.out.println("========================================");
    }
}