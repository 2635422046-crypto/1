package com.shopping.trends.entity;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import com.baomidou.mybatisplus.annotation.TableName;
import lombok.Data;
import java.math.BigDecimal;
import java.util.Date;

/**
 * 购物记录实体类
 */
@Data
@TableName("shopping_data")
public class ShoppingRecord {

    @TableId(type = IdType.AUTO)
    private Long id;

    @TableField("date")
    private Date date;

    @TableField("customer_id")
    private String customerId;

    @TableField("age")
    private Integer age;

    @TableField("gender")
    private String gender;

    @TableField("item_purchased")
    private String item;

    @TableField("category")
    private String category;

    @TableField("purchase_amount")
    private BigDecimal purchaseAmount;

    @TableField("location")
    private String location;

    @TableField("size")
    private String size;

    @TableField("color")
    private String color;

    @TableField("season")
    private String season;

    @TableField("review_rating")
    private Double rating;

    @TableField("subscription_status")
    private String subscriptionStatus;

    @TableField("discount_applied")
    private String discountApplied;

    @TableField("promo_code_used")
    private String promoCodeUsed;

    @TableField("created_at")
    private Date createdAt;

    @TableField("updated_at")
    private Date updatedAt;

    public String getAgeGroup() {
        if (age == null) return "Unknown";
        if (age < 18) return "Under 18";
        if (age < 25) return "18-24";
        if (age < 35) return "25-34";
        if (age < 45) return "35-44";
        if (age < 55) return "45-54";
        return "55+";
    }
}