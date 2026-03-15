package com.shopping.trends.service;

import com.shopping.trends.dto.FrequencyStatsDTO;
import com.shopping.trends.dto.UserStatsDTO;
import java.time.LocalDate;
import java.util.Map;

public interface UserStatsService {
    /**
     * 获取用户统计数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 用户统计数据DTO
     */
    UserStatsDTO getUserStatistics(LocalDate startDate, LocalDate endDate);

    UserStatsDTO getUserStatisticsForAll();

    /**
     * 获取用户统计摘要数据
     * @return 包含用户数量、活跃度等统计数据的Map
     */
    Map<String, Object> getUserSummaryStats();

    /**
     * 获取购买频率统计数据
     * @return 包含日、周、月购买频率的DTO
     */
    FrequencyStatsDTO getPurchaseFrequencyStats();
}