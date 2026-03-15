package com.shopping.trends.controller;

import com.shopping.trends.common.Result;
import com.shopping.trends.dto.ProductAnalysisDTO;
import com.shopping.trends.service.ProductAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;

/**
 * 产品分析控制器：提供销量趋势、品类分布、热销排行三个只读接口。
 * 使用构造器注入（{@code @RequiredArgsConstructor}），便于单元测试与依赖追踪。
 */
@Api(tags = "产品分析接口")
@CrossOrigin(origins = "*", allowedHeaders = "*",
        methods = {RequestMethod.GET, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/product")
@RequiredArgsConstructor
public class ProductAnalysisController {

    // 由 Lombok @RequiredArgsConstructor 自动生成构造器注入，等价于 @Autowired 但更推荐
    private final ProductAnalysisService productAnalysisService;

    /**
     * 获取产品销量趋势数据
     * @param days 统计天数，默认最近 7 天
     * @return 包含日期列表与对应销量列表的趋势数据
     */
    @GetMapping("/trend")
    @ApiOperation("获取产品销量趋势数据")
    public Result<ProductAnalysisDTO.TrendData> getProductTrend(
            @RequestParam(defaultValue = "7") int days) {
        return Result.success(productAnalysisService.getProductTrendData(days));
    }

    /**
     * 获取产品品类分布数据（按 category 分组统计销量）
     * @return 品类名称与销量的分布列表
     */
    @GetMapping("/category")
    @ApiOperation("获取产品品类分布数据")
    public Result<List<ProductAnalysisDTO.CategoryDistribution>> getCategoryDistribution() {
        return Result.success(productAnalysisService.getCategoryDistribution());
    }

    /**
     * 获取热销产品排行榜（按 item_purchased 销量降序）
     * @param limit 排行榜数量，默认取前 10 名
     * @return 热销产品名称与销量的列表
     */
    @GetMapping("/top")
    @ApiOperation("获取热销产品排行榜")
    public Result<List<ProductAnalysisDTO.TopProduct>> getTopProducts(
            @RequestParam(defaultValue = "10") int limit) {
        return Result.success(productAnalysisService.getTopProducts(limit));
    }
}
