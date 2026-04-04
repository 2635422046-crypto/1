package com.shopping.trends;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 电商交易数据分析系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.shopping.trends.mapper")
public class ShoppingTrendsApplication {

    public static void main(String[] args) {
        // 无图形界面环境下生成登录验证码 PNG（CaptchaServiceImpl 使用 AWT）
        System.setProperty("java.awt.headless", "true");
        SpringApplication.run(ShoppingTrendsApplication.class, args);
    }
}