package com.shopping.trends.dto;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.List;

/**
 * 产品分析数据传输对象：包含销量趋势、品类分布、热销排行三个内部类。
 * 各内部类均使用 Lombok {@code @Data}（生成 getter/setter/toString/equals/hashCode）
 * 和 {@code @AllArgsConstructor} / {@code @NoArgsConstructor}（生成有参和无参构造器），
 * 代替原先手写的 getter 方法，减少样板代码。
 */
public class ProductAnalysisDTO {

    /**
     * 产品销量趋势数据：日期列表与对应每日销量列表一一对应。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TrendData {
        /** 日期字符串列表，格式 yyyy-MM-dd，与 sales 下标对齐 */
        private List<String> dates;
        /** 每日销量列表，与 dates 下标对齐 */
        private List<Integer> sales;
    }

    /**
     * 产品品类分布数据：单个品类的名称与销量。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class CategoryDistribution {
        /** 品类名称（即 category 字段值，前端经 displayLabels 转为中文显示） */
        private String name;
        /** 该品类的销量（订单数） */
        private Integer value;
    }

    /**
     * 热销产品数据：单个产品的名称与销量。
     */
    @Data
    @NoArgsConstructor
    @AllArgsConstructor
    public static class TopProduct {
        /** 产品名称（即 item_purchased 字段值） */
        private String name;
        /** 该产品的销量（订单数） */
        private Integer value;
    }
}
