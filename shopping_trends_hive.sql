-- 创建数据库
CREATE DATABASE IF NOT EXISTS shopping_trends;
USE shopping_trends;

-- 创建表
CREATE EXTERNAL TABLE IF NOT EXISTS shopping_data (
    transaction_date STRING,
    customer_id STRING,
    age INT,
    gender STRING,
    item_purchased STRING,
    category STRING,
    purchase_amount INT,
    location STRING,
    size STRING,
    color STRING,
    season STRING,
    review_rating FLOAT,
    subscription_status STRING,
    discount_applied STRING,
    promo_code_used STRING
)
ROW FORMAT DELIMITED
FIELDS TERMINATED BY ','
LINES TERMINATED BY '\n'
STORED AS TEXTFILE
TBLPROPERTIES ("skip.header.line.count"="1");

-- 加载数据
-- 从HDFS加载
LOAD DATA INPATH '/user/hive/shopping_trends_data.csv' INTO TABLE shopping_data;
-- 从本地文件系统加载（已注释）
-- LOAD DATA LOCAL INPATH './shopping_trends_data.csv' INTO TABLE shopping_data;

-- 1. 消费者行为分析

-- 1.1 会员订阅与非会员的消费差异
SELECT 
    subscription_status,
    COUNT(*) as total_purchases,
    ROUND(AVG(purchase_amount), 2) as avg_purchase_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY subscription_status;

-- 1.2 折扣使用分析
SELECT 
    discount_applied,
    promo_code_used,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY discount_applied, promo_code_used;

-- 1.3 年龄段消费分析
SELECT 
    CASE 
        WHEN age < 25 THEN '18-24'
        WHEN age BETWEEN 25 AND 34 THEN '25-34'
        WHEN age BETWEEN 35 AND 44 THEN '35-44'
        WHEN age BETWEEN 45 AND 54 THEN '45-54'
        ELSE '55+'
    END as age_group,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY CASE 
    WHEN age < 25 THEN '18-24'
    WHEN age BETWEEN 25 AND 34 THEN '25-34'
    WHEN age BETWEEN 35 AND 44 THEN '35-44'
    WHEN age BETWEEN 45 AND 54 THEN '45-54'
    ELSE '55+'
END
ORDER BY age_group;

-- 2. 商品分析

-- 2.1 类别评分和销量分析
SELECT 
    category,
    COUNT(*) as total_sales,
    ROUND(AVG(review_rating), 2) as avg_rating,
    ROUND(AVG(purchase_amount), 2) as avg_price
FROM shopping_data
GROUP BY category
ORDER BY total_sales DESC;

-- 2.2 尺码销售分析
SELECT 
    size,
    COUNT(*) as total_sales,
    ROUND(AVG(purchase_amount), 2) as avg_price
FROM shopping_data
GROUP BY size
ORDER BY total_sales DESC;

-- 2.3 颜色偏好分析
SELECT 
    color,
    COUNT(*) as total_sales,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY color
ORDER BY total_sales DESC;

-- 3. 时间维度分析

-- 3.1 季节性购买趋势
SELECT 
    season,
    COUNT(*) as total_sales,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY season
ORDER BY 
    CASE season 
        WHEN 'Spring' THEN 1 
        WHEN 'Summer' THEN 2 
        WHEN 'Fall' THEN 3 
        WHEN 'Winter' THEN 4 
    END;

-- 3.2 月度销售分析
SELECT 
    substr(transaction_date, 1, 7) as month,
    COUNT(*) as total_sales,
    ROUND(SUM(purchase_amount), 2) as total_amount,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY substr(transaction_date, 1, 7)
ORDER BY month;

-- 4. 地理维度分析

-- 4.1 地区销售排名
SELECT 
    location,
    COUNT(*) as total_sales,
    ROUND(SUM(purchase_amount), 2) as total_revenue,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY location
ORDER BY total_revenue DESC;

-- 4.2 地区商品类别偏好
SELECT 
    location,
    category,
    COUNT(*) as category_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY location, category
HAVING category_count > 5
ORDER BY location, category_count DESC;

-- 5. 交叉分析

-- 5.1 性别与商品类别的关系
SELECT 
    gender,
    category,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY gender, category
ORDER BY gender, purchase_count DESC;

-- 5.2 季节与商品颜色的关系
SELECT 
    season,
    color,
    COUNT(*) as color_count
FROM shopping_data
GROUP BY season, color
HAVING color_count > 3
ORDER BY season, color_count DESC;

-- 5.3 会员状态与评分的关系
SELECT 
    subscription_status,
    ROUND(AVG(review_rating), 2) as avg_rating,
    COUNT(*) as total_reviews,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY subscription_status;

-- 5.4 高价值客户分析
WITH customer_stats AS (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count,
        SUM(purchase_amount) as total_spent,
        AVG(review_rating) as avg_rating
    FROM shopping_data
    GROUP BY customer_id
)
SELECT 
    CASE 
        WHEN total_spent >= 500 THEN 'High Value'
        WHEN total_spent >= 200 THEN 'Medium Value'
        ELSE 'Low Value'
    END as customer_segment,
    COUNT(*) as customer_count,
    ROUND(AVG(total_spent), 2) as avg_total_spent,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM customer_stats
GROUP BY CASE 
    WHEN total_spent >= 500 THEN 'High Value'
    WHEN total_spent >= 200 THEN 'Medium Value'
    ELSE 'Low Value'
END
ORDER BY avg_total_spent DESC;

-- 6. 复购行为分析

-- 6.1 客户购买频次分布
SELECT 
    purchase_count,
    COUNT(*) as customer_count
FROM (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count
    FROM shopping_data
    GROUP BY customer_id
) t
GROUP BY purchase_count
ORDER BY purchase_count;

-- 6.2 复购客户与一次性客户的消费差异
SELECT 
    CASE WHEN purchase_count > 1 THEN 'Repeat Customer' ELSE 'One-time Customer' END as customer_type,
    COUNT(*) as customer_count,
    ROUND(AVG(avg_amount), 2) as avg_purchase_amount,
    ROUND(AVG(avg_rating), 2) as avg_rating
FROM (
    SELECT 
        customer_id,
        COUNT(*) as purchase_count,
        AVG(purchase_amount) as avg_amount,
        AVG(review_rating) as avg_rating
    FROM shopping_data
    GROUP BY customer_id
) t
GROUP BY CASE WHEN purchase_count > 1 THEN 'Repeat Customer' ELSE 'One-time Customer' END;

-- 7. RFM分析 (Recency, Frequency, Monetary)

-- 7.1 RFM分段
WITH customer_rfm AS (
    SELECT 
        customer_id,
        MAX(transaction_date) as last_purchase_date,
        COUNT(*) as frequency,
        SUM(purchase_amount) as monetary
    FROM shopping_data
    GROUP BY customer_id
),
rfm_scores AS (
    SELECT 
        customer_id,
        NTILE(5) OVER (ORDER BY last_purchase_date DESC) as recency_score,
        NTILE(5) OVER (ORDER BY frequency) as frequency_score,
        NTILE(5) OVER (ORDER BY monetary) as monetary_score
    FROM customer_rfm
)
SELECT 
    CASE 
        WHEN (recency_score + frequency_score + monetary_score) >= 13 THEN 'Champions'
        WHEN (recency_score + frequency_score + monetary_score) >= 10 THEN 'Loyal Customers'
        WHEN (recency_score + frequency_score + monetary_score) >= 7 THEN 'Potential Loyalists'
        WHEN (recency_score + frequency_score + monetary_score) >= 5 THEN 'At Risk Customers'
        ELSE 'Need Attention'
    END as customer_segment,
    COUNT(*) as customer_count,
    ROUND(AVG(recency_score), 2) as avg_recency,
    ROUND(AVG(frequency_score), 2) as avg_frequency,
    ROUND(AVG(monetary_score), 2) as avg_monetary
FROM rfm_scores
GROUP BY CASE 
    WHEN (recency_score + frequency_score + monetary_score) >= 13 THEN 'Champions'
    WHEN (recency_score + frequency_score + monetary_score) >= 10 THEN 'Loyal Customers'
    WHEN (recency_score + frequency_score + monetary_score) >= 7 THEN 'Potential Loyalists'
    WHEN (recency_score + frequency_score + monetary_score) >= 5 THEN 'At Risk Customers'
    ELSE 'Need Attention'
END
ORDER BY avg_recency + avg_frequency + avg_monetary DESC;

-- 8. 评分分析

-- 8.1 评分分布
SELECT 
    FLOOR(review_rating) as rating_bucket,
    COUNT(*) as rating_count,
    ROUND(AVG(purchase_amount), 2) as avg_purchase_amount
FROM shopping_data
GROUP BY FLOOR(review_rating)
ORDER BY rating_bucket;

-- 8.2 评分与购买金额的关系
SELECT 
    CASE 
        WHEN purchase_amount < 30 THEN 'Low Price'
        WHEN purchase_amount BETWEEN 30 AND 70 THEN 'Medium Price'
        ELSE 'High Price'
    END as price_range,
    ROUND(AVG(review_rating), 2) as avg_rating,
    COUNT(*) as purchase_count
FROM shopping_data
GROUP BY CASE 
    WHEN purchase_amount < 30 THEN 'Low Price'
    WHEN purchase_amount BETWEEN 30 AND 70 THEN 'Medium Price'
    ELSE 'High Price'
END
ORDER BY avg_rating DESC;

-- 9. 促销效果分析

-- 9.1 促销对购买金额的影响
SELECT 
    discount_applied,
    promo_code_used,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY discount_applied, promo_code_used
ORDER BY avg_amount DESC;

-- 9.2 促销在不同类别中的效果
SELECT 
    category,
    discount_applied,
    COUNT(*) as purchase_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount
FROM shopping_data
GROUP BY category, discount_applied
ORDER BY category, discount_applied;

-- 10. 趋势分析

-- 10.1 月度类别趋势
SELECT 
    substr(transaction_date, 1, 7) as month,
    category,
    COUNT(*) as category_count,
    ROUND(SUM(purchase_amount), 2) as total_amount
FROM shopping_data
GROUP BY substr(transaction_date, 1, 7), category
ORDER BY month, total_amount DESC;

-- 10.2 季节性商品分析
SELECT 
    season,
    category,
    COUNT(*) as category_count,
    ROUND(AVG(purchase_amount), 2) as avg_amount,
    ROUND(AVG(review_rating), 2) as avg_rating
FROM shopping_data
GROUP BY season, category
ORDER BY 
    CASE season 
        WHEN 'Spring' THEN 1 
        WHEN 'Summer' THEN 2 
        WHEN 'Fall' THEN 3 
        WHEN 'Winter' THEN 4 
    END, 
    category_count DESC;

-- 11. 商品组合分析

-- 11.1 同一客户购买的商品组合
SELECT 
    a.category as category1,
    b.category as category2,
    COUNT(*) as combination_count
FROM shopping_data a
JOIN shopping_data b ON a.customer_id = b.customer_id AND a.category < b.category
GROUP BY a.category, b.category
HAVING combination_count > 5
ORDER BY combination_count DESC;

-- 11.2 同一客户购买的颜色组合
SELECT 
    a.color as color1,
    b.color as color2,
    COUNT(*) as combination_count
FROM shopping_data a
JOIN shopping_data b ON a.customer_id = b.customer_id AND a.color < b.color
GROUP BY a.color, b.color
HAVING combination_count > 5
ORDER BY combination_count DESC;