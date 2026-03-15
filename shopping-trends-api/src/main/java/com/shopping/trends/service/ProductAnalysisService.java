package com.shopping.trends.service;

import com.shopping.trends.dto.ProductAnalysisDTO;
import java.util.List;

public interface ProductAnalysisService {
    /**
     * 获取产品销量趋势数据
     * @param days 天数
     * @return 趋势数据
     */
    ProductAnalysisDTO.TrendData getProductTrendData(int days);

    /**
     * 获取产品品类分布数据
     * @return 品类分布数据
     */
    List<ProductAnalysisDTO.CategoryDistribution> getCategoryDistribution();

    /**
     * 获取热销产品排行
     * @param limit 排名数量
     * @return 热销产品列表
     */
    List<ProductAnalysisDTO.TopProduct> getTopProducts(int limit);
}