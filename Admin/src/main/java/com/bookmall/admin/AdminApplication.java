package com.bookmall.admin;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.autoconfigure.security.servlet.SecurityAutoConfiguration;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

/**
 * 管理后台服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication(exclude = SecurityAutoConfiguration.class)
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.admin.mapper")
@ComponentScan(basePackages = {"com.bookmall.admin", "com.bookmall.common"})
public class AdminApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdminApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Admin模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8087");
        System.out.println("  📖 API文档: http://localhost:8087/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8087/druid");
        System.out.println("========================================");
    }
}