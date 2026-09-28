package com.bookmall.gateway;

import com.bookmall.common.config.WebMvcConfig;
import com.bookmall.common.interceptor.AuthInterceptor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.FilterType;

/**
 * 网关服务启动类
 *
 * @author BookMall Team
 */
@SpringBootApplication
@EnableDiscoveryClient
@EnableConfigurationProperties
@ComponentScan(
        basePackages = {"com.bookmall.gateway", "com.bookmall.common"},
        excludeFilters = {
                @ComponentScan.Filter(
                        type = FilterType.ASSIGNABLE_TYPE,
                        classes = {WebMvcConfig.class, AuthInterceptor.class}
                ),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com.bookmall.common.config.*"
                ),
                @ComponentScan.Filter(
                        type = FilterType.REGEX,
                        pattern = "com.bookmall.common.interceptor.*"
                )
        }
)
public class GatewayApplication {

    public static void main(String[] args) {
        SpringApplication.run(GatewayApplication.class, args);
        System.out.println("========================================");
        System.out.println("  📚 教材商城系统 - Gateway模块启动成功！");
        System.out.println("  🔗 网关地址: http://localhost:8090");
        System.out.println("========================================");
    }
}