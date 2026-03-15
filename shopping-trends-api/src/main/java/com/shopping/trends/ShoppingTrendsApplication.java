package com.shopping.trends;

import org.mybatis.spring.annotation.MapperScan;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.transaction.annotation.EnableTransactionManagement;

/**
 * 购物趋势分析系统启动类
 */
@SpringBootApplication
@EnableTransactionManagement
@MapperScan("com.shopping.trends.mapper")
public class ShoppingTrendsApplication {

    public static void main(String[] args) {
        SpringApplication.run(ShoppingTrendsApplication.class, args);
    }
}