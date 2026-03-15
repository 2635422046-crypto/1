package com.shopping.trends.service.impl;

import com.shopping.trends.dto.UserStatsDTO;
import com.shopping.trends.entity.ShoppingRecord;
import com.shopping.trends.mapper.ShoppingRecordMapper;
import com.shopping.trends.service.UserStatsService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.time.LocalDate;
import java.time.ZoneId;
import java.time.format.DateTimeFormatter;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import com.shopping.trends.dto.FrequencyStatsDTO;
import com.shopping.trends.exception.BusinessException;
import com.shopping.trends.util.ShoppingDataNormalizer;

/**
 * 用户统计服务实现：从 shopping_data 表读取数据，
 * 在内存中完成聚合计算（活跃用户数、购买频率、年龄/性别/地区分布等），
 * 全部为只读操作，不修改数据库。
 */
@Slf4j
@Service
@Transactional(readOnly = true)
@RequiredArgsConstructor
public class UserStatsServiceImpl implements UserStatsService {

    private final ShoppingRecordMapper shoppingRecordMapper;

    @Override
    public UserStatsDTO getUserStatistics(LocalDate startDate, LocalDate endDate) {
        return shoppingRecordMapper.selectUserStats(startDate, endDate);
    }

    @Override
    public UserStatsDTO getUserStatisticsForAll() {
        // 读取全量记录并做字段标准化（去空格、枚举统一大小写等）
        List<ShoppingRecord> records = shoppingRecordMapper.selectAll();
        records = ShoppingDataNormalizer.normalizeRecords(records);
        return calculateStats(records);
    }

    @Override
    public Map<String, Object> getUserSummaryStats() {
        UserStatsDTO stats = getUserStatisticsForAll();
        Map<String, Object> result = new HashMap<>();
        result.put("activeUserCount", stats.getActiveUserCount());
        result.put("avgPurchaseFrequency", stats.getAvgPurchaseFrequency());
        // 各分布字段若为 null 则返回空 Map，防止前端解析报错
        result.put("ageGroupDistribution",  stats.getAgeGroupDistribution()  != null ? stats.getAgeGroupDistribution()  : Collections.emptyMap());
        result.put("genderDistribution",    stats.getGenderDistribution()    != null ? stats.getGenderDistribution()    : Collections.emptyMap());
        result.put("locationDistribution",  stats.getLocationDistribution()  != null ? stats.getLocationDistribution()  : Collections.emptyMap());
        return result;
    }

    private static final DateTimeFormatter WEEK_FORMATTER = DateTimeFormatter.ofPattern("YYYY-ww");
    private static final DateTimeFormatter MONTH_FORMATTER = DateTimeFormatter.ofPattern("YYYY-MM");

    @Override
    public FrequencyStatsDTO getPurchaseFrequencyStats() {
        log.info("开始统计购买频率数据");
        
        List<ShoppingRecord> records = shoppingRecordMapper.selectAll();
        if (records == null || records.isEmpty()) {
            log.warn("没有找到购物记录数据");
            return new FrequencyStatsDTO();
        }
        records = ShoppingDataNormalizer.normalizeRecords(records);

        FrequencyStatsDTO stats = new FrequencyStatsDTO();
        
        try {
            // 按日统计
            Map<String, Integer> dailyStats = records.stream()
                .filter(record -> record.getDate() != null)
                .collect(Collectors.groupingBy(
                    record -> record.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().toString(),
                    Collectors.summingInt(r -> 1)
                ));
            stats.setDailyFrequency(dailyStats);
            
            // 按周统计 (格式: YYYY-WW)
            Map<String, Integer> weeklyStats = records.stream()
                .filter(record -> record.getDate() != null)
                .collect(Collectors.groupingBy(
                    record -> record.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(WEEK_FORMATTER),
                    Collectors.summingInt(r -> 1)
                ));
            stats.setWeeklyFrequency(weeklyStats);
            
            // 按月统计 (格式: YYYY-MM)
            Map<String, Integer> monthlyStats = records.stream()
                .filter(record -> record.getDate() != null)
                .collect(Collectors.groupingBy(
                    record -> record.getDate().toInstant().atZone(ZoneId.systemDefault()).toLocalDate().format(MONTH_FORMATTER),
                    Collectors.summingInt(r -> 1)
                ));
            stats.setMonthlyFrequency(monthlyStats);
            
            log.info("成功统计购买频率数据: 日统计{}条, 周统计{}条, 月统计{}条",
                dailyStats.size(), weeklyStats.size(), monthlyStats.size());
        } catch (Exception e) {
            log.error("统计购买频率数据时发生异常", e);
            throw new BusinessException("统计购买频率数据失败");
        }
        
        return stats;
    }

    /**
     * 在内存中聚合计算各项用户统计指标（不发额外 SQL）
     */
    private UserStatsDTO calculateStats(List<ShoppingRecord> records) {
        UserStatsDTO stats = new UserStatsDTO();

        // 按 customerId 去重后计数，得到不重复用户数
        long activeUserCount = records.stream()
                .map(ShoppingRecord::getCustomerId)
                .distinct()
                .count();
        stats.setActiveUserCount((int) activeUserCount);

        // 平均购买频率 = 总订单数 / 不重复用户数
        if (activeUserCount > 0) {
            stats.setAvgPurchaseFrequency((double) records.size() / activeUserCount);
        }

        // 按年龄段分组计数
        Map<String, Integer> ageDistribution = records.stream()
                .collect(Collectors.groupingBy(ShoppingRecord::getAgeGroup, Collectors.summingInt(r -> 1)));
        stats.setAgeGroupDistribution(ageDistribution);

        // 按性别分组计数
        Map<String, Integer> genderDistribution = records.stream()
                .collect(Collectors.groupingBy(ShoppingRecord::getGender, Collectors.summingInt(r -> 1)));
        stats.setGenderDistribution(genderDistribution);

        // 按地区分组计数
        Map<String, Integer> locationDistribution = records.stream()
                .collect(Collectors.groupingBy(ShoppingRecord::getLocation, Collectors.summingInt(r -> 1)));
        stats.setLocationDistribution(locationDistribution);

        return stats;
    }
}