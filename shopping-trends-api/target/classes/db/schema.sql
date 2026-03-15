-- 创建数据库
CREATE DATABASE IF NOT EXISTS shopping_trends DEFAULT CHARACTER SET utf8mb4 COLLATE utf8mb4_general_ci;

USE shopping_trends;

-- 购物记录表
CREATE TABLE IF NOT EXISTS `shopping_data` (
  `id` bigint(20) NOT NULL AUTO_INCREMENT COMMENT '主键ID',
  `gender` varchar(10) DEFAULT NULL COMMENT '性别',
  `age` int(11) DEFAULT NULL COMMENT '年龄',
  `category` varchar(50) DEFAULT NULL COMMENT '商品类别',
  `item_purchased` varchar(100) DEFAULT NULL COMMENT '购买的商品',
  `payment_method` varchar(50) DEFAULT NULL COMMENT '支付方式',
  `amount` decimal(10, 2) DEFAULT NULL COMMENT '金额',
  `create_time` datetime DEFAULT NULL COMMENT '创建时间',
  `update_time` datetime DEFAULT NULL COMMENT '更新时间',
  `deleted` tinyint(1) DEFAULT '0' COMMENT '是否删除',
  PRIMARY KEY (`id`),
  KEY `idx_gender` (`gender`),
  KEY `idx_category` (`category`),
  KEY `idx_payment_method` (`payment_method`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COMMENT='购物记录表';