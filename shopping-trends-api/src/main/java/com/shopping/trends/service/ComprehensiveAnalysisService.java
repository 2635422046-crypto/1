package com.shopping.trends.service;

import java.util.List;
import java.util.Map;

/**
 * 综合分析服务
 */
public interface ComprehensiveAnalysisService {

    // ========== summary_views 相关 ==========
    /** 购买频率与金额关系 */
    List<Map<String, Object>> getFrequencyAmount();
    /** 客户价格敏感度 */
    List<Map<String, Object>> getPriceSensitivity();
    /** 时间序列按日 */
    List<Map<String, Object>> getTimeSeries();
    /** 客户细分可视化（年龄×性别×价值） */
    List<Map<String, Object>> getCustomerSegmentationViz();

    // ========== hive 相关 ==========
    /** 会员 vs 非会员消费 */
    List<Map<String, Object>> getSubscriptionStats();
    /** 年龄段消费 */
    List<Map<String, Object>> getAgeGroupStats();
    /** 季节性购买 */
    List<Map<String, Object>> getSeasonStats();
    /**  月度销售 */
    List<Map<String, Object>> getMonthlyStats();
    /** 高价值客户分段 */
    List<Map<String, Object>> getHighValueSegment();
    /** 复购 vs 一次性客户 */
    List<Map<String, Object>> getRepeatVsOnetime();
    /** RFM 分段 */
    List<Map<String, Object>> getRfmSegment();
    /** 评分分布 */
    List<Map<String, Object>> getRatingDist();

    // ========== advanced_analysis 相关 ==========
    /** 客户生命周期阶段 */
    List<Map<String, Object>> getLifecycleStage();
    /**客户状态 Active/At Risk/Churned */
    List<Map<String, Object>> getCustomerStatus();
    /** 行为聚类 */
    List<Map<String, Object>> getBehaviorCluster();
    /** 品类关联 */
    List<Map<String, Object>> getCategoryAssociation();
    /** 优惠码对购买商品种类的影响（promo_code_used × item_purchased） */
    List<Map<String, Object>> getPromoItemEffect();
    /** 折扣和销售的关系（discount_applied × purchase_amount） */
    List<Map<String, Object>> getDiscountSalesRelation();
}
