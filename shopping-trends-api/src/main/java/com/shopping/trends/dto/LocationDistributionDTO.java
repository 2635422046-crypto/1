package com.shopping.trends.dto;

import lombok.Data;

/**
 * 地区分布数据DTO
 */
@Data
public class LocationDistributionDTO {
    private String location;
    private Integer count;
    private Double percentage;
    private Double totalAmount;
}