package com.shopping.trends.dto;

import lombok.Data;

/**
 * 词云数据项DTO
 */
@Data
public class WordCloudItem {
    private String name;  // 商品名称
    private Integer value; // 出现频率
    private String color; // 可选颜色值

    public WordCloudItem(String name, Integer value) {
        this.name = name;
        this.value = value;
        // 生成随机颜色
        this.color = String.format("#%06x", (int)(Math.random() * 0xFFFFFF));
    }
}