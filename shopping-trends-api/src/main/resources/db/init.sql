-- Active: 1750770635953@@192.168.101.101@3306@bigdata
-- 创建数据库
CREATE DATABASE IF NOT EXISTS bigdata;

-- 使用数据库
USE bigdata;

-- 创建购物数据表
CREATE TABLE IF NOT EXISTS shopping_data (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    date DATE NOT NULL,
    customer_id VARCHAR(10) NOT NULL,
    age INT NOT NULL,
    gender VARCHAR(10) NOT NULL,
    item_purchased VARCHAR(50) NOT NULL,
    category VARCHAR(20) NOT NULL,
    purchase_amount DECIMAL(10,2) NOT NULL,
    location VARCHAR(50) NOT NULL,
    size VARCHAR(5) NOT NULL,
    color VARCHAR(20) NOT NULL,
    season VARCHAR(10) NOT NULL,
    review_rating DECIMAL(3,1) NOT NULL,
    subscription_status VARCHAR(5) NOT NULL,
    discount_applied VARCHAR(5) NOT NULL,
    promo_code_used VARCHAR(5) NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

-- 插入数据
INSERT INTO shopping_data (date, customer_id, age, gender, item_purchased, category, purchase_amount, location, size, color, season, review_rating, subscription_status, discount_applied, promo_code_used)
VALUES
('2023-12-27', 'CG-12520', 55, 'Male', 'Blouse', 'Clothing', 53.00, 'Zhejiang', 'L', 'Gray', 'Winter', 3.1, 'Yes', 'Yes', 'Yes'),
('2023-03-06', 'CG-12520', 19, 'Male', 'Sweater', 'Clothing', 64.00, 'Shandong', 'L', 'Maroon', 'Spring', 3.1, 'Yes', 'Yes', 'Yes'),
('2023-02-05', 'DV-13045', 50, 'Male', 'Jeans', 'Clothing', 73.00, 'Hebei', 'S', 'Maroon', 'Winter', 3.1, 'Yes', 'Yes', 'Yes'),
('2023-07-11', 'SO-20335', 21, 'Male', 'Sandals', 'Footwear', 90.00, 'Heilongjiang', 'M', 'Maroon', 'Summer', 3.5, 'Yes', 'Yes', 'Yes'),
('2023-06-24', 'SO-20335', 45, 'Male', 'Blouse', 'Clothing', 49.00, 'Liaoning', 'M', 'Turquoise', 'Summer', 2.7, 'Yes', 'Yes', 'Yes'),
('2023-01-24', 'BH-11710', 46, 'Male', 'Sneakers', 'Footwear', 20.00, 'Shandong', 'M', 'White', 'Winter', 2.9, 'Yes', 'Yes', 'Yes'),
('2023-11-08', 'BH-11710', 63, 'Male', 'Shirt', 'Clothing', 85.00, 'Hunan', 'M', 'Gray', 'Fall', 3.2, 'Yes', 'Yes', 'Yes'),
('2023-01-14', 'BH-11710', 27, 'Male', 'Shorts', 'Clothing', 34.00, 'Jiangsu', 'L', 'Charcoal', 'Winter', 3.2, 'Yes', 'Yes', 'Yes'),
('2023-08-03', 'BH-11710', 26, 'Male', 'Coat', 'Outerwear', 97.00, 'Chongqing', 'L', 'Silver', 'Summer', 2.6, 'Yes', 'Yes', 'Yes'),
('2023-12-03', 'BH-11710', 57, 'Male', 'Handbag', 'Accessories', 31.00, 'Sichuan', 'M', 'Pink', 'Winter', 4.8, 'Yes', 'Yes', 'Yes'),
('2023-02-09', 'BH-11710', 53, 'Male', 'Shoes', 'Footwear', 34.00, 'Hebei', 'L', 'Purple', 'Winter', 4.1, 'Yes', 'Yes', 'Yes'),
('2023-10-24', 'BH-11710', 30, 'Male', 'Shorts', 'Clothing', 68.00, 'Sichuan', 'S', 'Olive', 'Fall', 4.9, 'Yes', 'Yes', 'Yes'),
('2023-12-20', 'GA-14725', 33, 'Female', 'T-shirt', 'Clothing', 84.00, 'Shanghai', 'S', 'Pink', 'Winter', 4.9, 'No', 'No', 'No'),
('2023-04-23', 'GA-14725', 51, 'Female', 'Pants', 'Clothing', 50.00, 'Liaoning', 'M', 'Charcoal', 'Spring', 5.0, 'No', 'No', 'No'),
('2023-08-26', 'GA-14725', 65, 'Female', 'Shoes', 'Footwear', 88.00, 'Guangdong', 'M', 'Brown', 'Summer', 4.5, 'No', 'No', 'No'),
('2023-07-04', 'TC-21295', 51, 'Female', 'Jacket', 'Outerwear', 50.00, 'Gansu', 'M', 'Blue', 'Summer', 3.7, 'No', 'No', 'No'),
('2023-11-15', 'JB-15400', 70, 'Female', 'Scarf', 'Accessories', 80.00, 'Hunan', 'L', 'Teal', 'Fall', 2.8, 'No', 'No', 'No'),
('2023-07-24', 'CK-12325', 40, 'Female', 'T-shirt', 'Clothing', 47.00, 'Hunan', 'L', 'Pink', 'Summer', 3.5, 'No', 'No', 'No'),
('2023-04-19', 'RL-19615', 19, 'Female', 'Dress', 'Clothing', 33.00, 'Sichuan', 'XL', 'Magenta', 'Spring', 3.0, 'No', 'No', 'No'),
('2023-02-24', 'AC-10660', 28, 'Female', 'Coat', 'Outerwear', 100.00, 'Shanghai', 'L', 'Green', 'Winter', 4.3, 'No', 'No', 'No'),
('2023-02-17', 'BH-11710', 51, 'Female', 'Sweater', 'Clothing', 26.00, 'Anhui', 'M', 'White', 'Winter', 3.6, 'No', 'No', 'No'),
('2023-11-05', 'BG-11695', 32, 'Female', 'Hat', 'Accessories', 79.00, 'Shanghai', 'M', 'Indigo', 'Fall', 4.4, 'No', 'No', 'No'),
('2023-12-05', 'XP-21865', 26, 'Female', 'Backpack', 'Accessories', 77.00, 'Guangdong', 'L', 'Lavender', 'Winter', 4.1, 'No', 'No', 'No'),
('2023-06-17', 'SN-20560', 50, 'Female', 'Socks', 'Clothing', 36.00, 'Shanghai', 'XL', 'Black', 'Summer', 2.8, 'No', 'No', 'No'),
('2023-01-22', 'DM-13345', 68, 'Female', 'Jewelry', 'Accessories', 21.00, 'Shanxi', 'L', 'Charcoal', 'Winter', 2.8, 'No', 'No', 'No'),
('2023-04-23', 'DM-13345', 33, 'Female', 'Boots', 'Footwear', 72.00, 'Sichuan', 'L', 'Gray', 'Spring', 4.9, 'No', 'No', 'No'),
('2023-06-23', 'AJ-10780', 56, 'Female', 'Coat', 'Outerwear', 25.00, 'Shaanxi', 'M', 'Gold', 'Summer', 4.1, 'No', 'No', 'No'),
('2023-11-13', 'KC-16255', 57, 'Female', 'Sweater', 'Clothing', 79.00, 'Shandong', 'M', 'Silver', 'Fall', 3.0, 'No', 'No', 'No'),
('2023-11-29', 'VF-21715', 58, 'Female', 'Blouse', 'Clothing', 73.00, 'Zhejiang', 'L', 'Teal', 'Fall', 3.0, 'No', 'No', 'No'),
('2023-09-05', 'SV-20935', 40, 'Female', 'Belt', 'Accessories', 71.00, 'Guangdong', 'M', 'Magenta', 'Fall', 3.0, 'No', 'No', 'No'),
('2023-01-04', 'SV-20935', 37, 'Female', 'Hat', 'Accessories', 48.00, 'Shanxi', 'M', 'Magenta', 'Winter', 3.2, 'No', 'No', 'No'),
('2023-08-12', 'EM-13810', 36, 'Female', 'Sandals', 'Footwear', 28.00, 'Henan', 'M', 'Beige', 'Summer', 2.7, 'No', 'No', 'No'),
('2023-10-01', 'RB-19360', 29, 'Female', 'Boots', 'Footwear', 95.00, 'Guangxi', 'S', 'Maroon', 'Fall', 3.8, 'No', 'No', 'No'),
('2023-01-04', 'DJ-13510', 65, 'Female', 'Jacket', 'Outerwear', 48.00, 'Sichuan', 'M', 'Indigo', 'Winter', 3.5, 'No', 'No', 'No'),
('2023-03-26', 'CC-12370', 44, 'Female', 'Dress', 'Clothing', 93.00, 'Hubei', 'S', 'Maroon', 'Spring', 3.8, 'No', 'No', 'No'),
('2023-06-30', 'SC-20380', 34, 'Female', 'Jeans', 'Clothing', 87.00, 'Shanghai', 'L', 'Teal', 'Summer', 3.8, 'No', 'No', 'No');

-- 创建索引
CREATE INDEX idx_customer_id ON shopping_data(customer_id);
CREATE INDEX idx_category ON shopping_data(category);
CREATE INDEX idx_date ON shopping_data(date);
CREATE INDEX idx_location ON shopping_data(location);


SELECT * FROM bigdata.shopping_data;