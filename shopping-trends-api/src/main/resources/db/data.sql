USE shopping_trends;

-- 插入示例数据
INSERT INTO shopping_data 
(gender, age, category, item_purchased, payment_method, amount, create_time, update_time) 
VALUES
-- 服装类
('Male', 25, 'Clothing', 'T-Shirt', 'Credit Card', 29.99, NOW(), NOW()),
('Female', 30, 'Clothing', 'Dress', 'Debit Card', 89.99, NOW(), NOW()),
('Female', 22, 'Clothing', 'Jeans', 'PayPal', 59.99, NOW(), NOW()),
('Male', 35, 'Clothing', 'Jacket', 'Credit Card', 129.99, NOW(), NOW()),

-- 电子产品
('Male', 28, 'Electronics', 'Headphones', 'Credit Card', 199.99, NOW(), NOW()),
('Female', 42, 'Electronics', 'Smartphone', 'Credit Card', 899.99, NOW(), NOW()),
('Male', 19, 'Electronics', 'Gaming Console', 'PayPal', 499.99, NOW(), NOW()),
('Female', 33, 'Electronics', 'Tablet', 'Debit Card', 399.99, NOW(), NOW()),

-- 食品杂货
('Female', 45, 'Groceries', 'Organic Food', 'Cash', 78.50, NOW(), NOW()),
('Male', 52, 'Groceries', 'Beverages', 'Debit Card', 45.99, NOW(), NOW()),
('Female', 29, 'Groceries', 'Snacks', 'Credit Card', 32.99, NOW(), NOW()),
('Male', 38, 'Groceries', 'Fresh Produce', 'Cash', 65.75, NOW(), NOW()),

-- 美妆护理
('Female', 27, 'Beauty', 'Skincare Set', 'Credit Card', 89.99, NOW(), NOW()),
('Female', 35, 'Beauty', 'Makeup Kit', 'PayPal', 129.99, NOW(), NOW()),
('Male', 31, 'Beauty', 'Grooming Kit', 'Debit Card', 49.99, NOW(), NOW()),
('Female', 24, 'Beauty', 'Perfume', 'Credit Card', 79.99, NOW(), NOW()),

-- 运动户外
('Male', 29, 'Sports', 'Running Shoes', 'Credit Card', 119.99, NOW(), NOW()),
('Female', 32, 'Sports', 'Yoga Mat', 'PayPal', 29.99, NOW(), NOW()),
('Male', 45, 'Sports', 'Fitness Equipment', 'Credit Card', 299.99, NOW(), NOW()),
('Female', 28, 'Sports', 'Sports Wear', 'Debit Card', 89.99, NOW(), NOW()),

-- 书籍文具
('Female', 23, 'Books', 'Novel', 'Cash', 19.99, NOW(), NOW()),
('Male', 34, 'Books', 'Technical Book', 'Credit Card', 49.99, NOW(), NOW()),
('Female', 41, 'Books', 'Self-Help Book', 'PayPal', 24.99, NOW(), NOW()),
('Male', 27, 'Books', 'Magazine Subscription', 'Debit Card', 15.99, NOW(), NOW()),

-- 家居用品
('Female', 38, 'Home', 'Bedding Set', 'Credit Card', 149.99, NOW(), NOW()),
('Male', 43, 'Home', 'Kitchen Appliance', 'Debit Card', 199.99, NOW(), NOW()),
('Female', 29, 'Home', 'Decorative Items', 'PayPal', 79.99, NOW(), NOW()),
('Male', 36, 'Home', 'Furniture', 'Credit Card', 499.99, NOW(), NOW()),

-- 玩具娱乐
('Female', 31, 'Toys', 'Board Game', 'PayPal', 39.99, NOW(), NOW()),
('Male', 26, 'Toys', 'Video Game', 'Credit Card', 59.99, NOW(), NOW()),
('Female', 35, 'Toys', 'Educational Toy', 'Debit Card', 45.99, NOW(), NOW()),
('Male', 33, 'Toys', 'Collectible Item', 'Credit Card', 89.99, NOW(), NOW());