package com.shopping.trends.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.shopping.trends.dto.LocationDistributionDTO;
import com.shopping.trends.entity.ShoppingRecord;
import com.shopping.trends.dto.WordCloudItem;

import java.util.List;
import java.util.Map;

/**
 * 购物记录服务接口
 */
public interface ShoppingRecordService extends IService<ShoppingRecord> {

    /**
     * 获取性别统计数据
     * @return 性别统计数据列表
     */
    List<Map<String, Object>> getGenderStats();

    /**
     * 获取年龄段统计数据
     * @return 年龄段统计数据列表
     */
    List<Map<String, Object>> getAgeGroupStats();

    /**
     * 获取商品品类统计数据（品类=category）
     * @return 商品品类统计数据列表
     */
    List<Map<String, Object>> getCategoryStats();

    /**
     * 按 item_purchased 统计数量，用于商品种类分布（种类=item_purchased，只读）
     * @return 每项为 name(item_purchased)、value(数量)
     */
    List<Map<String, Object>> getItemPurchasedStats();

    /**
     * 按 color 统计数量，用于颜色分布（只读数据库 color 列）
     * @return 每项为 name(color)、value(数量)
     */
    List<Map<String, Object>> getColorStats();

    /**
     * 按 size 统计数量，用于尺码分布（只读数据库 size 列）
     * @return 每项为 name(size)、value(数量)
     */
    List<Map<String, Object>> getSizeStats();

    /**
     * 产品分布：按品类(category)+种类(item_purchased)统计，用于产品分布图与「品类包含种类」列表
     * @return 每项为 category、item_purchased、purchase_count
     */
    List<Map<String, Object>> getProductDistribution();

    /**
     * 获取季节统计数据
     * @return 季节统计数据列表
     */
    List<Map<String, Object>> getSeasonStats();

    /**
     * 获取地区统计数据
     * @return 地区统计数据列表
     */
    List<Map<String, Object>> getLocationStats();

    /**
     * 获取日期范围统计数据
     * @return 日期范围统计数据列表
     */
    List<Map<String, Object>> getDateRangeStats();

    /**
     * 获取客户购买统计数据
     * @return 客户购买统计数据列表
     */
    List<Map<String, Object>> getCustomerStats();
    
    /**
     * 获取词云数据
     * @return 词云数据列表
     */
    List<WordCloudItem> getWordCloudData();
    
    /**
     * 获取所有商品品类（品类=category）
     * @return 商品品类列表
     */
    List<String> getAllCategories();

    /**
     * 获取统计摘要数据
     * @return 包含总销售额、订单数量、客户数量和平均订单金额的Map
     */
    Map<String, Object> getSummaryStats();

    /**
     * 获取详细地区分布数据
     *
     * @return 包含地区名称、数量、占比和总金额的DTO列表
     */
    List<LocationDistributionDTO> getLocationDistribution();
}