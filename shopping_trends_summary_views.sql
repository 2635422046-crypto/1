-- 购物趋势数据总结视图
-- 使用shopping_trends数据库
USE shopping_trends;

-- 1. 客户行为模式分析

-- 1.1 购买频率与金额的关系
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
ORDER BY CASE frequency_group
    WHEN '1 purchase' THEN 1
    WHEN '2 purchases' THEN 2
    WHEN '3-5 purchases' THEN 3
    ELSE 4
END;

-- 1.2 购买时间模式分析
SELECT 
    CASE 
        WHEN SUBSTR(transaction_date, 6, 2) IN ('03', '04', '05') THEN 'Spring'
        WHEN SUBSTR(transaction_date, 6, 2) IN ('06', '07', '08') THEN 'Summer'
        WHEN SUBSTR(transaction_date, 6, 2) IN ('09', '10', '11') THEN 'Fall'
        ELSE 'Winter'
    END as calendar_season,
    season as purchase_season,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY 
    CASE 
        WHEN SUBSTR(transaction_date, 6, 2) IN ('03', '04', '05') THEN 'Spring'
        WHEN SUBSTR(transaction_date, 6, 2) IN ('06', '07', '08') THEN 'Summer'
        WHEN SUBSTR(transaction_date, 6, 2) IN ('09', '10', '11') THEN 'Fall'
        ELSE 'Winter'
    END,
    season
ORDER BY 
    CASE calendar_season
        WHEN 'Spring' THEN 1
        WHEN 'Summer' THEN 2
        WHEN 'Fall' THEN 3
        WHEN 'Winter' THEN 4
    END,
    purchase_count DESC;

-- 1.3 客户购买路径分析
WITH customer_journey AS (
    SELECT 
        customer_id,
        category,
        transaction_date,
        ROW_NUMBER() OVER (PARTITION BY customer_id ORDER BY transaction_date) as purchase_sequence
    FROM shopping_data
)
SELECT 
    purchase_sequence,
    category,
    COUNT(*) as category_count,
    ROUND(COUNT(*) * 100.0 / (
        SELECT COUNT(*) FROM customer_journey WHERE purchase_sequence = cj.purchase_sequence
    ), 2) as category_pct
FROM customer_journey cj
WHERE purchase_sequence <= 5
GROUP BY purchase_sequence, category
ORDER BY purchase_sequence, category_count DESC;

-- 1.4 客户价格敏感度分析
WITH customer_price_sensitivity AS (
    SELECT 
        customer_id,
        AVG(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) as discount_ratio,
        AVG(purchase_amount) as avg_purchase,
        COUNT(*) as purchase_count
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN discount_ratio > 0.8 THEN 'Highly Price Sensitive'
        WHEN discount_ratio > 0.5 THEN 'Moderately Price Sensitive'
        WHEN discount_ratio > 0.2 THEN 'Slightly Price Sensitive'
        ELSE 'Price Insensitive'
    END as price_sensitivity,
    COUNT(*) as customer_count,
    ROUND(AVG(avg_purchase), 2) as avg_purchase_amount,
    ROUND(AVG(purchase_count), 2) as avg_purchase_count
FROM customer_price_sensitivity
GROUP BY CASE 
    WHEN discount_ratio > 0.8 THEN 'Highly Price Sensitive'
    WHEN discount_ratio > 0.5 THEN 'Moderately Price Sensitive'
    WHEN discount_ratio > 0.2 THEN 'Slightly Price Sensitive'
    ELSE 'Price Insensitive'
END
ORDER BY avg_purchase_amount;

-- 2. 总结性分析视图

-- 2.1 创建客户分析总结视图
CREATE OR REPLACE VIEW customer_analysis_summary AS
WITH customer_data AS (
    SELECT 
        customer_id,
        AVG(age) as age,
        MAX(gender) as gender,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(purchase_amount) as avg_purchase,
        MIN(transaction_date) as first_purchase,
        MAX(transaction_date) as last_purchase,
        DATEDIFF(MAX(transaction_date), MIN(transaction_date)) as customer_lifespan,
        COUNT(DISTINCT category) as category_diversity,
        AVG(review_rating) as avg_rating,
        SUM(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) / COUNT(*) as discount_ratio,
        MAX(subscription_status) as subscription_status
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    customer_id,
    age,
    gender,
    purchase_count,
    total_spent,
    avg_purchase,
    first_purchase,
    last_purchase,
    customer_lifespan,
    category_diversity,
    avg_rating,
    discount_ratio,
    subscription_status,
    -- 客户细分
    CASE 
        WHEN total_spent >= 300 THEN 'High Value'
        WHEN total_spent >= 100 THEN 'Medium Value'
        ELSE 'Low Value'
    END as value_segment,
    CASE 
        WHEN purchase_count >= 5 THEN 'High Frequency'
        WHEN purchase_count >= 2 THEN 'Medium Frequency'
        ELSE 'Low Frequency'
    END as frequency_segment,
    CASE 
        WHEN discount_ratio > 0.7 THEN 'Discount Seeker'
        WHEN discount_ratio > 0.3 THEN 'Occasional Discount'
        ELSE 'Full Price'
    END as price_sensitivity,
    CASE 
        WHEN customer_lifespan = 0 THEN 'New Customer'
        WHEN customer_lifespan <= 30 THEN 'Early Stage'
        WHEN customer_lifespan <= 180 THEN 'Growth Stage'
        ELSE 'Mature Stage'
    END as lifecycle_stage,
    CASE 
        WHEN DATEDIFF('2023-12-31', last_purchase) <= 30 THEN 'Active'
        WHEN DATEDIFF('2023-12-31', last_purchase) <= 90 THEN 'At Risk'
        ELSE 'Churned'
    END as activity_status
FROM customer_data;

-- 2.2 创建商品分析总结视图
CREATE OR REPLACE VIEW product_analysis_summary AS
SELECT 
    category,
    item_purchased,
    COUNT(*) as purchase_count,
    SUM(purchase_amount) as total_revenue,
    AVG(purchase_amount) as avg_price,
    AVG(review_rating) as avg_rating,
    COUNT(DISTINCT customer_id) as unique_customers,
    COUNT(DISTINCT color) as color_variants,
    COUNT(DISTINCT size) as size_variants,
    SUM(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) / COUNT(*) as discount_ratio,
    -- 季节性分布
    SUM(CASE WHEN season = 'Spring' THEN 1 ELSE 0 END) as spring_sales,
    SUM(CASE WHEN season = 'Summer' THEN 1 ELSE 0 END) as summer_sales,
    SUM(CASE WHEN season = 'Fall' THEN 1 ELSE 0 END) as fall_sales,
    SUM(CASE WHEN season = 'Winter' THEN 1 ELSE 0 END) as winter_sales,
    -- 性别分布
    SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) as male_purchases,
    SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) as female_purchases
FROM shopping_data
GROUP BY category, item_purchased;

-- 2.3 创建地区分析总结视图
CREATE OR REPLACE VIEW location_analysis_summary AS
SELECT 
    location,
    COUNT(*) as total_sales,
    SUM(purchase_amount) as total_revenue,
    AVG(purchase_amount) as avg_purchase,
    COUNT(DISTINCT customer_id) as unique_customers,
    COUNT(DISTINCT category) as category_diversity,
    -- 热门类别
    COLLECT_SET(category_rank.category)[0] as top_category,
    -- 性别分布
    SUM(CASE WHEN gender = 'Male' THEN 1 ELSE 0 END) / COUNT(*) as male_ratio,
    SUM(CASE WHEN gender = 'Female' THEN 1 ELSE 0 END) / COUNT(*) as female_ratio,
    -- 季节性分布
    SUM(CASE WHEN season = 'Spring' THEN 1 ELSE 0 END) as spring_sales,
    SUM(CASE WHEN season = 'Summer' THEN 1 ELSE 0 END) as summer_sales,
    SUM(CASE WHEN season = 'Fall' THEN 1 ELSE 0 END) as fall_sales,
    SUM(CASE WHEN season = 'Winter' THEN 1 ELSE 0 END) as winter_sales,
    -- 促销效果
    SUM(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) / COUNT(*) as discount_ratio,
    AVG(CASE WHEN discount_applied = 'Yes' THEN purchase_amount ELSE NULL END) as avg_discounted_amount,
    AVG(CASE WHEN discount_applied = 'No' THEN purchase_amount ELSE NULL END) as avg_regular_amount
FROM shopping_data
LEFT JOIN (
    SELECT 
        location,
        category,
        ROW_NUMBER() OVER (PARTITION BY location ORDER BY COUNT(*) DESC) as rank
    FROM shopping_data
    GROUP BY location, category
) category_rank ON shopping_data.location = category_rank.location AND category_rank.rank = 1
GROUP BY location;

-- 3. 数据可视化准备视图

-- 3.1 创建时间序列分析视图
CREATE OR REPLACE VIEW time_series_analysis AS
SELECT 
    transaction_date,
    COUNT(*) as daily_sales,
    SUM(purchase_amount) as daily_revenue,
    AVG(purchase_amount) as avg_purchase,
    COUNT(DISTINCT customer_id) as unique_customers,
    COUNT(DISTINCT category) as category_count,
    SUM(CASE WHEN discount_applied = 'Yes' THEN 1 ELSE 0 END) as discounted_sales,
    SUM(CASE WHEN subscription_status = 'Yes' THEN 1 ELSE 0 END) as subscription_sales
FROM shopping_data
GROUP BY transaction_date
ORDER BY transaction_date;

-- 3.2 创建客户细分可视化视图
CREATE OR REPLACE VIEW customer_segmentation_viz AS
SELECT 
    CASE 
        WHEN age < 25 THEN '18-24'
        WHEN age BETWEEN 25 AND 34 THEN '25-34'
        WHEN age BETWEEN 35 AND 44 THEN '35-44'
        WHEN age BETWEEN 45 AND 54 THEN '45-54'
        WHEN age BETWEEN 55 AND 64 THEN '55-64'
        ELSE '65+'
    END as age_group,
    gender,
    CASE 
        WHEN total_spent >= 300 THEN 'High Value'
        WHEN total_spent >= 100 THEN 'Medium Value'
        ELSE 'Low Value'
    END as value_segment,
    COUNT(*) as customer_count,
    ROUND(AVG(purchase_count), 2) as avg_purchases,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM (
    SELECT 
        customer_id,
        AVG(age) as age,
        MAX(gender) as gender,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(review_rating) as avg_rating
    FROM shopping_data
    GROUP BY customer_id
) customer_data
GROUP BY 
    CASE 
        WHEN age < 25 THEN '18-24'
        WHEN age BETWEEN 25 AND 34 THEN '25-34'
        WHEN age BETWEEN 35 AND 44 THEN '35-44'
        WHEN age BETWEEN 45 AND 54 THEN '45-54'
        WHEN age BETWEEN 55 AND 64 THEN '55-64'
        ELSE '65+'
    END,
    gender,
    CASE 
        WHEN total_spent >= 300 THEN 'High Value'
        WHEN total_spent >= 100 THEN 'Medium Value'
        ELSE 'Low Value'
    END;

-- 3.3 创建商品热度地图视图
CREATE OR REPLACE VIEW product_heatmap_viz AS
SELECT 
    category,
    color,
    size,
    COUNT(*) as purchase_count,
    SUM(purchase_amount) as total_revenue,
    AVG(review_rating) as avg_rating
FROM shopping_data
GROUP BY category,
    color，
    size
ORDER BY purchase_count DESC;