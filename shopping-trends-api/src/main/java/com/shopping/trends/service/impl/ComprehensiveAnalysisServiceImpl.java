package com.shopping.trends.service.impl;

import com.shopping.trends.service.ComprehensiveAnalysisService;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

/**
 * 综合分析服务实现（MySQL 只读查询，对应三个 SQL 文件逻辑）
 */
@Service
@RequiredArgsConstructor
public class ComprehensiveAnalysisServiceImpl implements ComprehensiveAnalysisService {

    private final JdbcTemplate jdbcTemplate;

    @Override
    public List<Map<String, Object>> getFrequencyAmount() {
        String sql = "WITH customer_metrics AS (" +
                "SELECT customer_id, COUNT(*) as purchase_count, SUM(purchase_amount) as total_spent, " +
                "AVG(purchase_amount) as avg_purchase, AVG(review_rating) as avg_rating FROM shopping_data GROUP BY customer_id" +
                ") SELECT CASE WHEN purchase_count = 1 THEN '1次' WHEN purchase_count = 2 THEN '2次' WHEN purchase_count <= 5 THEN '3-5次' ELSE '6次以上' END as frequency_group, " +
                "COUNT(*) as customer_count, ROUND(AVG(total_spent), 2) as avg_total_spent, ROUND(AVG(avg_purchase), 2) as avg_purchase_amount, ROUND(AVG(avg_rating), 2) as avg_rating " +
                "FROM customer_metrics GROUP BY CASE WHEN purchase_count = 1 THEN '1次' WHEN purchase_count = 2 THEN '2次' WHEN purchase_count <= 5 THEN '3-5次' ELSE '6次以上' END " +
                "ORDER BY CASE frequency_group WHEN '1次' THEN 1 WHEN '2次' THEN 2 WHEN '3-5次' THEN 3 ELSE 4 END";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getPriceSensitivity() {
        String sql = "WITH t AS (SELECT customer_id, AVG(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) as discount_ratio, AVG(purchase_amount) as avg_purchase, COUNT(*) as purchase_count " +
                "FROM shopping_data GROUP BY customer_id) " +
                "SELECT CASE WHEN discount_ratio > 0.8 THEN '高敏感' WHEN discount_ratio > 0.5 THEN '中敏感' WHEN discount_ratio > 0.2 THEN '低敏感' ELSE '不敏感' END as price_sensitivity, " +
                "COUNT(*) as customer_count, ROUND(AVG(avg_purchase), 2) as avg_purchase_amount, ROUND(AVG(purchase_count), 2) as avg_purchase_count FROM t " +
                "GROUP BY CASE WHEN discount_ratio > 0.8 THEN '高敏感' WHEN discount_ratio > 0.5 THEN '中敏感' WHEN discount_ratio > 0.2 THEN '低敏感' ELSE '不敏感' END ORDER BY avg_purchase_amount";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getCustomerSegmentationViz() {
        String sql = "SELECT CASE WHEN age < 25 THEN '18-24' WHEN age BETWEEN 25 AND 34 THEN '25-34' WHEN age BETWEEN 35 AND 44 THEN '35-44' WHEN age BETWEEN 45 AND 54 THEN '45-54' WHEN age BETWEEN 55 AND 64 THEN '55-64' ELSE '65+' END as age_group, " +
                "gender, CASE WHEN total_spent >= 300 THEN '高价值' WHEN total_spent >= 100 THEN '中价值' ELSE '低价值' END as value_segment, COUNT(*) as customer_count, " +
                "ROUND(AVG(purchase_count), 2) as avg_purchases, ROUND(AVG(total_spent), 2) as avg_total_spent, ROUND(AVG(avg_rating), 2) as avg_rating " +
                "FROM (SELECT customer_id, AVG(age) as age, MAX(gender) as gender, COUNT(*) as purchase_count, SUM(purchase_amount) as total_spent, AVG(review_rating) as avg_rating FROM shopping_data GROUP BY customer_id) customer_data " +
                "GROUP BY age_group, gender, value_segment";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getTimeSeries() {
        String sql = "SELECT DATE(date) as date, COUNT(*) as total_sales, ROUND(SUM(purchase_amount), 2) as total_amount, ROUND(AVG(purchase_amount), 2) as avg_amount FROM shopping_data GROUP BY DATE(date) ORDER BY date";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getSubscriptionStats() {
        String sql = "SELECT subscription_status, COUNT(*) as total_purchases, ROUND(AVG(purchase_amount), 2) as avg_purchase_amount, ROUND(AVG(review_rating), 2) as avg_rating FROM shopping_data GROUP BY subscription_status";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getAgeGroupStats() {
        String sql = "SELECT CASE WHEN age < 25 THEN '18-24' WHEN age BETWEEN 25 AND 34 THEN '25-34' WHEN age BETWEEN 35 AND 44 THEN '35-44' WHEN age BETWEEN 45 AND 54 THEN '45-54' ELSE '55+' END as age_group, " +
                "COUNT(*) as purchase_count, ROUND(AVG(purchase_amount), 2) as avg_amount, ROUND(AVG(review_rating), 2) as avg_rating FROM shopping_data " +
                "GROUP BY age_group ORDER BY age_group";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getSeasonStats() {
        String sql = "SELECT season, COUNT(*) as total_sales, ROUND(AVG(purchase_amount), 2) as avg_amount, ROUND(AVG(review_rating), 2) as avg_rating FROM shopping_data GROUP BY season " +
                "ORDER BY CASE season WHEN 'Spring' THEN 1 WHEN 'Summer' THEN 2 WHEN 'Fall' THEN 3 WHEN 'Winter' THEN 4 END";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getMonthlyStats() {
        String sql = "SELECT DATE_FORMAT(date, '%Y-%m') as month, COUNT(*) as total_sales, ROUND(SUM(purchase_amount), 2) as total_amount, ROUND(AVG(purchase_amount), 2) as avg_amount FROM shopping_data GROUP BY month ORDER BY month";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getHighValueSegment() {
        String sql = "WITH customer_stats AS (SELECT customer_id, COUNT(*) as purchase_count, SUM(purchase_amount) as total_spent, AVG(review_rating) as avg_rating FROM shopping_data GROUP BY customer_id) " +
                "SELECT CASE WHEN total_spent >= 500 THEN '高价值' WHEN total_spent >= 200 THEN '中价值' ELSE '低价值' END as customer_segment, COUNT(*) as customer_count, " +
                "ROUND(AVG(total_spent), 2) as avg_total_spent, ROUND(AVG(avg_rating), 2) as avg_rating FROM customer_stats " +
                "GROUP BY customer_segment ORDER BY avg_total_spent DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getRepeatVsOnetime() {
        String sql = "SELECT CASE WHEN purchase_count > 1 THEN '复购客户' ELSE '一次性客户' END as customer_type, COUNT(*) as customer_count, " +
                "ROUND(AVG(avg_amount), 2) as avg_purchase_amount, ROUND(AVG(avg_rating), 2) as avg_rating " +
                "FROM (SELECT customer_id, COUNT(*) as purchase_count, AVG(purchase_amount) as avg_amount, AVG(review_rating) as avg_rating FROM shopping_data GROUP BY customer_id) t " +
                "GROUP BY customer_type";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getRfmSegment() {
        String sql = "WITH customer_rfm AS (SELECT customer_id, MAX(date) as last_purchase_date, COUNT(*) as frequency, SUM(purchase_amount) as monetary FROM shopping_data GROUP BY customer_id), " +
                "rfm_scores AS (SELECT customer_id, NTILE(5) OVER (ORDER BY last_purchase_date DESC) as recency_score, NTILE(5) OVER (ORDER BY frequency) as frequency_score, NTILE(5) OVER (ORDER BY monetary) as monetary_score FROM customer_rfm) " +
                "SELECT CASE WHEN (recency_score + frequency_score + monetary_score) >= 13 THEN 'Champions' WHEN (recency_score + frequency_score + monetary_score) >= 10 THEN 'Loyal' WHEN (recency_score + frequency_score + monetary_score) >= 7 THEN 'Potential' WHEN (recency_score + frequency_score + monetary_score) >= 5 THEN 'At Risk' ELSE 'Need Attention' END as customer_segment, " +
                "COUNT(*) as customer_count, ROUND(AVG(recency_score), 2) as avg_recency, ROUND(AVG(frequency_score), 2) as avg_frequency, ROUND(AVG(monetary_score), 2) as avg_monetary FROM rfm_scores " +
                "GROUP BY customer_segment ORDER BY avg_recency + avg_frequency + avg_monetary DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getRatingDist() {
        String sql = "SELECT FLOOR(review_rating) as rating_bucket, COUNT(*) as rating_count, ROUND(AVG(purchase_amount), 2) as avg_purchase_amount FROM shopping_data GROUP BY FLOOR(review_rating) ORDER BY rating_bucket";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getLifecycleStage() {
        String sql = "WITH customer_lifecycle AS (SELECT customer_id, MIN(date) as first_purchase, MAX(date) as last_purchase, COUNT(*) as purchase_count, SUM(purchase_amount) as total_spent, " +
                "DATEDIFF(MAX(date), MIN(date)) as customer_lifespan FROM shopping_data GROUP BY customer_id) " +
                "SELECT CASE WHEN customer_lifespan = 0 THEN '新客' WHEN customer_lifespan <= 30 THEN '早期' WHEN customer_lifespan <= 180 THEN '成长' ELSE '成熟' END as lifecycle_stage, COUNT(*) as customer_count, " +
                "ROUND(AVG(purchase_count), 2) as avg_purchases, ROUND(AVG(total_spent), 2) as avg_total_spent FROM customer_lifecycle " +
                "GROUP BY lifecycle_stage ORDER BY avg_total_spent DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getCustomerStatus() {
        String sql = "WITH customer_value AS (SELECT customer_id, COUNT(*) as purchase_count, SUM(purchase_amount) as total_spent, AVG(purchase_amount) as avg_purchase, " +
                "DATEDIFF(MAX(date), MIN(date)) / GREATEST(COUNT(*)-1, 1) as avg_purchase_frequency, DATEDIFF((SELECT MAX(date) FROM shopping_data), MAX(date)) as days_since_last_purchase FROM shopping_data GROUP BY customer_id) " +
                "SELECT CASE WHEN days_since_last_purchase <= avg_purchase_frequency * 1.5 THEN '活跃' WHEN days_since_last_purchase <= avg_purchase_frequency * 3 THEN '风险' ELSE '流失' END as customer_status, " +
                "COUNT(*) as customer_count, ROUND(AVG(purchase_count), 2) as avg_purchases, ROUND(AVG(total_spent), 2) as avg_total_spent FROM customer_value " +
                "GROUP BY customer_status";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getBehaviorCluster() {
        String sql = "WITH customer_behavior AS (SELECT customer_id, COUNT(*) as purchase_count, AVG(purchase_amount) as avg_purchase, COUNT(DISTINCT category) as category_diversity, " +
                "AVG(review_rating) as avg_rating FROM shopping_data GROUP BY customer_id) " +
                "SELECT CASE WHEN purchase_count >= 5 AND category_diversity >= 3 THEN '多样高频' WHEN purchase_count >= 5 THEN '专注高频' WHEN purchase_count >= 2 AND category_diversity >= 2 THEN '多样中频' WHEN purchase_count >= 2 THEN '专注中频' ELSE '低频' END as behavior_cluster, " +
                "COUNT(*) as customer_count, ROUND(AVG(purchase_count), 2) as avg_purchases, ROUND(AVG(avg_purchase), 2) as avg_purchase_amount, ROUND(AVG(category_diversity), 2) as avg_category_diversity, ROUND(AVG(avg_rating), 2) as avg_rating " +
                "FROM customer_behavior GROUP BY behavior_cluster ORDER BY avg_purchases DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getCategoryAssociation() {
        String sql = "SELECT a.category as category1, b.category as category2, COUNT(*) as purchased_together FROM shopping_data a " +
                "JOIN shopping_data b ON a.customer_id = b.customer_id AND a.category < b.category GROUP BY a.category, b.category HAVING purchased_together > 5 ORDER BY purchased_together DESC LIMIT 30";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getPromoItemEffect() {
        String sql = "SELECT item_purchased, " +
                "SUM(CASE WHEN promo_code_used = 'Yes' THEN 1 ELSE 0 END) AS promo_count, " +
                "SUM(CASE WHEN promo_code_used = 'No'  THEN 1 ELSE 0 END) AS no_promo_count, " +
                "COUNT(*) AS total_count, " +
                "ROUND(SUM(CASE WHEN promo_code_used = 'Yes' THEN 1 ELSE 0 END) * 100.0 / COUNT(*), 1) AS promo_rate " +
                "FROM shopping_data " +
                "GROUP BY item_purchased " +
                "ORDER BY promo_rate DESC";
        return jdbcTemplate.queryForList(sql);
    }

    @Override
    public List<Map<String, Object>> getDiscountSalesRelation() {
        String sql = "SELECT discount_applied, " +
                "COUNT(*) AS order_count, " +
                "ROUND(SUM(purchase_amount), 2) AS total_amount, " +
                "ROUND(AVG(purchase_amount), 2) AS avg_amount " +
                "FROM shopping_data " +
                "GROUP BY discount_applied " +
                "ORDER BY total_amount DESC";
        return jdbcTemplate.queryForList(sql);
    }
}
