package com.shopping.trends.service.impl;

import com.shopping.trends.dto.ProductAnalysisDTO;
import com.shopping.trends.mapper.ShoppingRecordMapper;
import com.shopping.trends.service.ProductAnalysisService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * 产品分析服务实现：通过 Mapper 执行只读 SQL 查询，
 * 在内存中完成数据转换（Map → DTO），不修改数据库。
 * 使用构造器注入（{@code @RequiredArgsConstructor}）替代字段注入（@Autowired），
 * 更易于单元测试和 IDEA 依赖分析。
 */
@Slf4j
@Service
@RequiredArgsConstructor
public class ProductAnalysisServiceImpl implements ProductAnalysisService {

    // 由 Lombok @RequiredArgsConstructor 自动生成构造器注入
    private final ShoppingRecordMapper shoppingRecordMapper;

    /**
     * 查询指定天数内的产品日销量趋势。
     * 计算区间：[今天 - (days-1), 今天]，闭区间，传 7 即最近一周。
     */
    @Override
    public ProductAnalysisDTO.TrendData getProductTrendData(int days) {
        LocalDate endDate   = LocalDate.now();
        // 向前推 (days-1) 天，确保共 days 个自然日
        LocalDate startDate = endDate.minusDays(days - 1);

        List<Map<String, Object>> trendData =
                shoppingRecordMapper.selectProductTrendData(startDate, endDate);

        // 提取日期列表
        List<String> dates = trendData.stream()
                .map(data -> data.get("date").toString())
                .collect(Collectors.toList());

        // 提取销量列表，先转 Number 再取 int，兼容 Long / BigInteger 等数据库返回类型
        List<Integer> sales = trendData.stream()
                .map(data -> ((Number) data.get("salesCount")).intValue())
                .collect(Collectors.toList());

        return new ProductAnalysisDTO.TrendData(dates, sales);
    }

    /**
     * 查询全量品类分布（按 category 字段分组），
     * 将 Map<String,Object> 转为强类型 CategoryDistribution DTO。
     */
    @Override
    public List<ProductAnalysisDTO.CategoryDistribution> getCategoryDistribution() {
        return shoppingRecordMapper.selectCategoryDistribution().stream()
                .map(data -> new ProductAnalysisDTO.CategoryDistribution(
                        data.get("name").toString(),
                        ((Number) data.get("value")).intValue()
                ))
                .collect(Collectors.toList());
    }

    /**
     * 查询热销产品排行榜（按 item_purchased 分组，取销量前 limit 名）。
     */
    @Override
    public List<ProductAnalysisDTO.TopProduct> getTopProducts(int limit) {
        return shoppingRecordMapper.selectTopProducts(limit).stream()
                .map(data -> new ProductAnalysisDTO.TopProduct(
                        data.get("name").toString(),
                        ((Number) data.get("value")).intValue()
                ))
                .collect(Collectors.toList());
    }
}
