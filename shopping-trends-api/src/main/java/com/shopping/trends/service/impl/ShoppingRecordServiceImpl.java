package com.shopping.trends.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.shopping.trends.dto.LocationDistributionDTO;
import com.shopping.trends.entity.ShoppingRecord;
import com.shopping.trends.mapper.ShoppingRecordMapper;
import com.shopping.trends.service.ShoppingRecordService;
import com.shopping.trends.dto.WordCloudItem;
import com.shopping.trends.util.ShoppingDataNormalizer;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 购物记录服务实现类
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ShoppingRecordServiceImpl extends ServiceImpl<ShoppingRecordMapper, ShoppingRecord> implements ShoppingRecordService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getGenderStats() {
        String sql = "SELECT gender, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY gender";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "gender");
    }

    @Override
    public List<Map<String, Object>> getAgeGroupStats() {
        String sql = "SELECT " +
                "CASE " +
                "  WHEN age < 18 THEN '0-17' " +
                "  WHEN age BETWEEN 18 AND 24 THEN '18-24' " +
                "  WHEN age BETWEEN 25 AND 34 THEN '25-34' " +
                "  WHEN age BETWEEN 35 AND 44 THEN '35-44' " +
                "  WHEN age BETWEEN 45 AND 54 THEN '45-54' " +
                "  WHEN age BETWEEN 55 AND 64 THEN '55-64' " +
                "  ELSE '65+' " +
                "END as age_group, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY age_group " +
                "ORDER BY CASE " +
                "  WHEN age_group = '0-17' THEN 1 " +
                "  WHEN age_group = '18-24' THEN 2 " +
                "  WHEN age_group = '25-34' THEN 3 " +
                "  WHEN age_group = '35-44' THEN 4 " +
                "  WHEN age_group = '45-54' THEN 5 " +
                "  WHEN age_group = '55-64' THEN 6 " +
                "  ELSE 7 " +
                "END";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "age_group");
    }

    @Override
    public List<Map<String, Object>> getCategoryStats() {
        String sql = "SELECT category, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY category " +
                "ORDER BY total_amount DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "category");
    }

    @Override
    public List<Map<String, Object>> getItemPurchasedStats() {
        String sql = "SELECT item_purchased AS name, COUNT(*) AS value " +
                "FROM shopping_data " +
                "GROUP BY item_purchased " +
                "ORDER BY value DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "name");
    }

    @Override
    public List<Map<String, Object>> getColorStats() {
        // 与 getCategoryStats 完全相同模式：原始列名 + purchase_count，不用任何别名
        try {
            String sql = "SELECT color, COUNT(*) as purchase_count " +
                    "FROM shopping_data GROUP BY color ORDER BY purchase_count DESC";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return ShoppingDataNormalizer.normalizeStatsRows(rows, "color");
        } catch (Exception e) {
            log.warn("getColorStats 失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getSizeStats() {
        // 与 getCategoryStats 完全相同模式：原始列名 + purchase_count，不用任何别名
        try {
            String sql = "SELECT size, COUNT(*) as purchase_count " +
                    "FROM shopping_data GROUP BY size ORDER BY purchase_count DESC";
            List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
            return ShoppingDataNormalizer.normalizeStatsRows(rows, "size");
        } catch (Exception e) {
            log.warn("getSizeStats 失败: {}", e.getMessage());
            return new ArrayList<>();
        }
    }

    @Override
    public List<Map<String, Object>> getProductDistribution() {
        String sql = "SELECT category, item_purchased, COUNT(*) AS purchase_count " +
                "FROM shopping_data " +
                "GROUP BY category, item_purchased " +
                "ORDER BY category, purchase_count DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "category");
    }

    @Override
    public List<Map<String, Object>> getSeasonStats() {
        String sql = "SELECT season, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY season";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "season");
    }

    @Override
    public List<Map<String, Object>> getLocationStats() {
        String sql = "SELECT location, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY location " +
                "ORDER BY total_amount DESC";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "location");
    }

    @Override
    public List<Map<String, Object>> getDateRangeStats() {
        String sql = "SELECT DATE_FORMAT(date, '%Y-%m') as date, " +
                "SUM(purchase_amount) as total_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY DATE_FORMAT(date, '%Y-%m') " +
                "ORDER BY date";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "date");
    }

    @Override
    public List<Map<String, Object>> getCustomerStats() {
        String sql = "SELECT customer_id, " +
                "SUM(purchase_amount) as total_amount, " +
                "AVG(purchase_amount) as avg_amount, " +
                "COUNT(*) as purchase_count " +
                "FROM shopping_data " +
                "GROUP BY customer_id " +
                "ORDER BY total_amount DESC " +
                "LIMIT 100";
        List<Map<String, Object>> rows = jdbcTemplate.queryForList(sql);
        return ShoppingDataNormalizer.normalizeStatsRows(rows, "customer_id");
    }

    @Override
    public List<WordCloudItem> getWordCloudData() {
        String sql = "SELECT item, COUNT(*) as frequency " +
                "FROM shopping_data " +
                "GROUP BY item " +
                "ORDER BY frequency DESC " +
                "LIMIT 100";
        
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        List<Map<String, Object>> normalized = ShoppingDataNormalizer.normalizeStatsRows(result, "item");
        List<WordCloudItem> wordCloudItems = new ArrayList<>();
        for (Map<String, Object> row : normalized) {
            String item = (String) row.get("item");
            Object freq = row.get("frequency");
            int f = freq instanceof Number ? ((Number) freq).intValue() : 0;
            wordCloudItems.add(new WordCloudItem(item != null ? item : "", f));
        }
        return wordCloudItems;
    }
    
    @Override
    public List<String> getAllCategories() {
        String sql = "SELECT DISTINCT category FROM shopping_data ORDER BY category";
        List<Map<String, Object>> result = jdbcTemplate.queryForList(sql);
        List<String> categories = new ArrayList<>();
        
        for (Map<String, Object> row : result) {
            String category = (String) row.get("category");
            categories.add(ShoppingDataNormalizer.normalizeCategory(category != null ? category : ""));
        }
        return categories;
    }
    
    @Override
    public Map<String, Object> getSummaryStats() {
        Map<String, Object> summary = new HashMap<>();
        
        // 获取总销售额
        String totalSalesSql = "SELECT SUM(purchase_amount) as total_sales FROM shopping_data";
        Double totalSales = jdbcTemplate.queryForObject(totalSalesSql, Double.class);
        summary.put("total_sales", totalSales != null ? totalSales : 0);
        
        // 获取订单数量
        String orderCountSql = "SELECT COUNT(*) as order_count FROM shopping_data";
        Integer orderCount = jdbcTemplate.queryForObject(orderCountSql, Integer.class);
        summary.put("order_count", orderCount != null ? orderCount : 0);
        
        // 获取客户数量
        String customerCountSql = "SELECT COUNT(DISTINCT customer_id) as customer_count FROM shopping_data";
        Integer customerCount = jdbcTemplate.queryForObject(customerCountSql, Integer.class);
        summary.put("customer_count", customerCount != null ? customerCount : 0);
        
        // 计算平均订单金额
        if (orderCount != null && orderCount > 0 && totalSales != null) {
            summary.put("avg_order_amount", totalSales / orderCount);
        } else {
            summary.put("avg_order_amount", 0);
        }
        
        return summary;
    }

    @Override
    public List<LocationDistributionDTO> getLocationDistribution() {
        // 获取各地区基础统计数据
        List<Map<String, Object>> locationStats = getLocationStats();
        
        // 计算总记录数和总金额
        int totalCount = locationStats.stream()
                .mapToInt(stat -> ((Number) stat.get("purchase_count")).intValue())
                .sum();
        
        double totalAmount = locationStats.stream()
                .mapToDouble(stat -> ((Number) stat.get("total_amount")).doubleValue())
                .sum();
        
        // 转换为DTO格式
        List<LocationDistributionDTO> result = new ArrayList<>();
        for (Map<String, Object> stat : locationStats) {
            LocationDistributionDTO dto = new LocationDistributionDTO();
            dto.setLocation((String) stat.get("location"));
            dto.setCount(((Number) stat.get("purchase_count")).intValue());
            dto.setTotalAmount(((Number) stat.get("total_amount")).doubleValue());
            
            // 计算百分比
            if (totalCount > 0) {
                dto.setPercentage(((double) dto.getCount() / totalCount) * 100);
            } else {
                dto.setPercentage(0.0);
            }
            
            result.add(dto);
        }
        
        return result;
    }
}