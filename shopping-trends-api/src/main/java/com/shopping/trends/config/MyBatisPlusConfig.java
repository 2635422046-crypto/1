package com.shopping.trends.config;

import com.baomidou.mybatisplus.annotation.DbType;
import com.baomidou.mybatisplus.extension.plugins.MybatisPlusInterceptor;
import com.baomidou.mybatisplus.extension.plugins.inner.PaginationInnerInterceptor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

/**
 * MyBatis-Plus 配置类：注册分页拦截器，支持物理分页（MySQL 方言）。
 * 注意：@MapperScan 已在主启动类 {@link com.shopping.trends.ShoppingTrendsApplication} 上声明，
 * 此处无需重复添加，避免引起 Spring 上下文扫描冲突。
 */
@Configuration
public class MyBatisPlusConfig {

    /**
     * 注册 MyBatis-Plus 拦截器，当前包含 MySQL 物理分页插件。
     * 如需扩展其他插件（乐观锁、防全表更新等），在此方法中追加 addInnerInterceptor 即可。
     */
    @Bean
    public MybatisPlusInterceptor mybatisPlusInterceptor() {
        MybatisPlusInterceptor interceptor = new MybatisPlusInterceptor();
        // MySQL 物理分页插件：自动在 SQL 末尾追加 LIMIT 子句，无需手动拼接
        interceptor.addInnerInterceptor(new PaginationInnerInterceptor(DbType.MYSQL));
        return interceptor;
    }
}