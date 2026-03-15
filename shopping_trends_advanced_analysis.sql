-- 高级购物趋势分析
-- 使用shopping_trends数据库
USE shopping_trends;

-- 1. 客户生命周期价值分析

-- 1.1 客户生命周期阶段分析
WITH customer_lifecycle AS (
    SELECT 
        customer_id,
        MIN(transaction_date) as first_purchase,
        MAX(transaction_date) as last_purchase,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        DATEDIFF(MAX(transaction_date), MIN(transaction_date)) as customer_lifespan
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN customer_lifespan = 0 THEN 'New Customer'
        WHEN customer_lifespan <= 30 THEN 'Early Stage'
        WHEN customer_lifespan <= 180 THEN 'Growth Stage'
        ELSE 'Mature Stage'
    END as lifecycle_stage,
    COUNT(*) as customer_count,
    ROUND(AVG(purchase_count), 2) as avg_purchases,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(total_spent) / AVG(customer_lifespan + 1), 2) as avg_daily_value
FROM customer_lifecycle
GROUP BY CASE 
    WHEN customer_lifespan = 0 THEN 'New Customer'
    WHEN customer_lifespan <= 30 THEN 'Early Stage'
    WHEN customer_lifespan <= 180 THEN 'Growth Stage'
    ELSE 'Mature Stage'
END
ORDER BY avg_daily_value DESC;

-- 1.2 客户生命周期价值预测
WITH customer_value AS (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(purchase_amount) as avg_purchase,
        DATEDIFF(MAX(transaction_date), MIN(transaction_date)) / COUNT(*) as avg_purchase_frequency,
        DATEDIFF('2023-12-31', MAX(transaction_date)) as days_since_last_purchase
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN days_since_last_purchase <= avg_purchase_frequency * 1.5 THEN 'Active'
        WHEN days_since_last_purchase <= avg_purchase_frequency * 3 THEN 'At Risk'
        ELSE 'Churned'
    END as customer_status,
    COUNT(*) as customer_count,
    ROUND(AVG(purchase_count), 2) as avg_purchases,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(avg_purchase), 2) as avg_purchase_value,
    -- 预测未来1年的客户价值
    ROUND(AVG(CASE 
        WHEN days_since_last_purchase <= avg_purchase_frequency * 1.5 THEN avg_purchase * (365 / GREATEST(avg_purchase_frequency, 1))
        WHEN days_since_last_purchase <= avg_purchase_frequency * 3 THEN avg_purchase * (365 / GREATEST(avg_purchase_frequency, 1)) * 0.5
        ELSE 0
    END), 2) as predicted_annual_value
FROM customer_value
GROUP BY CASE 
    WHEN days_since_last_purchase <= avg_purchase_frequency * 1.5 THEN 'Active'
    WHEN days_since_last_purchase <= avg_purchase_frequency * 3 THEN 'At Risk'
    ELSE 'Churned'
END
ORDER BY predicted_annual_value DESC;

-- 2. 季节性商品偏好分析

-- 2.1 季节与商品类别的关系
SELECT 
    season,
    category,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM shopping_data s2 WHERE s2.season = s1.season
    ), 2) as category_season_pct
FROM shopping_data s1
GROUP BY season, category
ORDER BY 
    CASE season 
        WHEN 'Spring' THEN 1 
        WHEN 'Summer' THEN 2 
        WHEN 'Fall' THEN 3 
        WHEN 'Winter' THEN 4 
    END, 
    category_season_pct DESC;

-- 2.2 季节性颜色偏好
SELECT 
    season,
    color,
    COUNT(*) as color_count,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM shopping_data s2 WHERE s2.season = s1.season
    ), 2) as color_season_pct
FROM shopping_data s1
GROUP BY season, color
HAVING color_count > 5
ORDER BY 
    CASE season 
        WHEN 'Spring' THEN 1 
        WHEN 'Summer' THEN 2 
        WHEN 'Fall' THEN 3 
        WHEN 'Winter' THEN 4 
    END, 
    color_season_pct DESC;

-- 2.3 季节性尺码偏好
SELECT 
    season,
    size,
    COUNT(*) as size_count,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM shopping_data s2 WHERE s2.season = s1.season
    ), 2) as size_season_pct
FROM shopping_data s1
GROUP BY season, size
ORDER BY 
    CASE season 
        WHEN 'Spring' THEN 1 
        WHEN 'Summer' THEN 2 
        WHEN 'Fall' THEN 3 
        WHEN 'Winter' THEN 4 
    END, 
    size_season_pct DESC;

-- 3. 客户细分分析

-- 3.1 多维度客户细分
WITH customer_segments AS (
    SELECT 
        customer_id,
        AVG(age) as age,
        MAX(gender) as gender,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(review_rating) as avg_rating,
        MAX(subscription_status) as subscription_status,
        SUM(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) / COUNT(*) as discount_ratio
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN age < 30 THEN 'Young'
        WHEN age < 50 THEN 'Middle-aged'
        ELSE 'Senior'
    END as age_group,
    gender,
    CASE 
        WHEN total_spent >= 300 THEN 'High Spender'
        WHEN total_spent >= 100 THEN 'Medium Spender'
        ELSE 'Low Spender'
    END as spending_group,
    CASE 
        WHEN discount_ratio > 0.7 THEN 'Discount Seeker'
        WHEN discount_ratio > 0.3 THEN 'Occasional Discount'
        ELSE 'Full Price'
    END as price_sensitivity,
    COUNT(*) as customer_count,
    ROUND(AVG(purchase_count), 2) as avg_purchases,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM customer_segments
GROUP BY 
    CASE 
        WHEN age < 30 THEN 'Young'
        WHEN age < 50 THEN 'Middle-aged'
        ELSE 'Senior'
    END,
    gender,
    CASE 
        WHEN total_spent >= 300 THEN 'High Spender'
        WHEN total_spent >= 100 THEN 'Medium Spender'
        ELSE 'Low Spender'
    END,
    CASE 
        WHEN discount_ratio > 0.7 THEN 'Discount Seeker'
        WHEN discount_ratio > 0.3 THEN 'Occasional Discount'
        ELSE 'Full Price'
    END
HAVING customer_count > 5
ORDER BY avg_total_spent DESC;

-- 3.2 购物行为聚类分析
WITH customer_behavior AS (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count,
        AVG(purchase_amount) as avg_purchase,
        STDDEV_POP(purchase_amount) as purchase_stddev,
        COUNT(DISTINCT category) as category_diversity,
        COUNT(DISTINCT color) as color_diversity,
        COUNT(DISTINCT size) as size_diversity,
        AVG(review_rating) as avg_rating
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN purchase_count >= 5 AND category_diversity >= 3 THEN 'Diverse High-frequency'
        WHEN purchase_count >= 5 THEN 'Focused High-frequency'
        WHEN purchase_count >= 2 AND category_diversity >= 2 THEN 'Diverse Medium-frequency'
        WHEN purchase_count >= 2 THEN 'Focused Medium-frequency'
        ELSE 'Low-frequency'
    END as behavior_cluster,
    COUNT(*) as customer_count,
    ROUND(AVG(purchase_count), 2) as avg_purchases,
    ROUND(AVG(avg_purchase), 2) as avg_purchase_amount,
    ROUND(AVG(purchase_stddev), 2) as avg_purchase_stddev,
    ROUND(AVG(category_diversity), 2) as avg_category_diversity,
    ROUND(AVG(color_diversity), 2) as avg_color_diversity,
    ROUND(AVG(size_diversity), 2) as avg_size_diversity,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM customer_behavior
GROUP BY CASE 
    WHEN purchase_count >= 5 AND category_diversity >= 3 THEN 'Diverse High-frequency'
    WHEN purchase_count >= 5 THEN 'Focused High-frequency'
    WHEN purchase_count >= 2 AND category_diversity >= 2 THEN 'Diverse Medium-frequency'
    WHEN purchase_count >= 2 THEN 'Focused Medium-frequency'
    ELSE 'Low-frequency'
END
ORDER BY avg_purchases DESC;

-- 4. 商品组合推荐分析

-- 4.1 商品关联规则分析
SELECT 
    a.item_purchased as item1,
    b.item_purchased as item2,
    COUNT(*) as purchased_together,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM shopping_data WHERE item_purchased = a.item_purchased
    ), 2) as confidence_pct,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(DISTINCT customer_id) FROM shopping_data
    ), 2) as support_pct
FROM shopping_data a
JOIN shopping_data b ON a.customer_id = b.customer_id AND a.item_purchased < b.item_purchased
GROUP BY a.item_purchased, b.item_purchased
HAVING purchased_together > 3
ORDER BY confidence_pct DESC, support_pct DESC;

-- 4.2 类别关联规则分析
SELECT 
    a.category as category1,
    b.category as category2,
    COUNT(*) as purchased_together,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM shopping_data WHERE category = a.category
    ), 2) as confidence_pct,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(DISTINCT customer_id) FROM shopping_data
    ), 2) as support_pct
FROM shopping_data a
JOIN shopping_data b ON a.customer_id = b.customer_id AND a.category < b.category
GROUP BY a.category, b.category
HAVING purchased_together > 5
ORDER BY confidence_pct DESC, support_pct DESC;

-- 4.3 基于购买历史的个性化推荐
WITH customer_preferences AS (
    SELECT 
        customer_id,
        COLLECT_SET(category) as preferred_categories,
        COLLECT_SET(color) as preferred_colors,
        COLLECT_SET(size) as preferred_sizes,
        AVG(purchase_amount) as avg_purchase
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    cp.customer_id,
    s.item_purchased as recommended_item,
    s.category,
    s.color,
    s.size,
    s.purchase_amount,
    s.review_rating
FROM customer_preferences cp
JOIN shopping_data s ON 
    array_contains(cp.preferred_categories, s.category) AND
    array_contains(cp.preferred_colors, s.color) AND
    array_contains(cp.preferred_sizes, s.size) AND
    ABS(s.purchase_amount - cp.avg_purchase) / cp.avg_purchase <= 0.2
WHERE s.review_rating >= 4.0
GROUP BY cp.customer_id, s.item_purchased, s.category, s.color, s.size, s.purchase_amount, s.review_rating
ORDER BY cp.customer_id, s.review_rating DESC;

-- 5. 客户行为模式分析

-- 5.1 购买频率与金额的关系
WITH customer_metrics AS (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(purchase_amount) as avg_purchase,
        AVG(review_rating) as avg_rating
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN purchase_count = 1 THEN '1 purchase'
        WHEN purchase_count = 2 THEN '2 purchases'
        WHEN purchase_count <= 5 THEN '3-5 purchases'
        ELSE '6+ purchases'
    END as frequency_group,
    COUNT(*) as customer_count,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(avg_purchase), 2) as avg_purchase_amount,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM customer_metrics
GROUP BY CASE 
    WHEN purchase_count = 1 THEN '1 purchase'
    WHEN purchase_count = 2 THEN '2 purchases'
    WHEN purchase_count <= 5 THEN '3-5 purchases'
    ELSE '6+ purchases'
END
ORDER