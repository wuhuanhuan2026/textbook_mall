package com.bookmall.user;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.cloud.openfeign.EnableFeignClients;
import org.springframework.context.annotation.ComponentScan;

@SpringBootApplication
@EnableDiscoveryClient
@EnableFeignClients
@MapperScan("com.bookmall.user.mapper")
@ComponentScan(basePackages = {"com.bookmall.user", "com.bookmall.common"})
public class UserApplication {
    public static void main(String[] args) {
        SpringApplication.run(UserApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - User模块启动成功！");
        System.out.println("  🔗 访问地址: http://localhost:8081/api/user/test");
        System.out.println("  📖 API文档: http://localhost:8081/swagger-ui.html");
        System.out.println("  💾 Druid监控: http://localhost:8081/druid");
        System.out.println("========================================");
    }
}