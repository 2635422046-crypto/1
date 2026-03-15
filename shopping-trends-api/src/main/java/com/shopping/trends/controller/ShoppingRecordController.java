package com.shopping.trends.controller;

import com.baomidou.mybatisplus.core.conditions.query.QueryWrapper;
import com.baomidou.mybatisplus.core.metadata.IPage;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.shopping.trends.common.PageResult;
import com.shopping.trends.common.Result;
import com.shopping.trends.dto.LocationDistributionDTO;
import com.shopping.trends.dto.WordCloudItem;
import com.shopping.trends.entity.ShoppingRecord;
import com.shopping.trends.service.ComprehensiveAnalysisService;
import com.shopping.trends.service.ShoppingRecordService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import io.swagger.annotations.ApiParam;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 购物记录控制器
 */
@Api(tags = "购物记录管理API", value = "提供购物记录的查询和统计分析功能")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/shopping-records")
@RequiredArgsConstructor
public class ShoppingRecordController {

    private final ShoppingRecordService shoppingRecordService;
    private final ComprehensiveAnalysisService comprehensiveAnalysisService;

    /**
     * 分页查询购物记录
     * @param page 当前页
     * @param size 每页大小
     * @param category 商品品类（category）
     * @param gender 性别
     * @param season 季节
     * @return 分页结果
     */
    @ApiOperation(value = "分页查询购物记录", 
        notes = "根据条件分页查询购物记录，支持按商品品类、性别和季节筛选")
    @GetMapping
    public Result<PageResult<ShoppingRecord>> getShoppingRecords(
            @ApiParam(value = "页码，默认为1", defaultValue = "1")
            @RequestParam(defaultValue = "1") Integer page,
            
            @ApiParam(value = "每页记录数，默认为10", defaultValue = "10")
            @RequestParam(defaultValue = "10") Integer size,
            
            @ApiParam(value = "商品品类(category)筛选条件")
            @RequestParam(required = false) String category,
            
            @ApiParam(value = "性别筛选条件")
            @RequestParam(required = false) String gender,
            
            @ApiParam(value = "季节筛选条件")
            @RequestParam(required = false) String season) {

        QueryWrapper<ShoppingRecord> queryWrapper = new QueryWrapper<>();
        if (category != null && !category.isEmpty()) {
            queryWrapper.eq("category", category);
        }
        if (gender != null && !gender.isEmpty()) {
            queryWrapper.eq("gender", gender);
        }
        if (season != null && !season.isEmpty()) {
            queryWrapper.eq("season", season);
        }

        IPage<ShoppingRecord> pageResult = shoppingRecordService.page(new Page<>(page, size), queryWrapper);
        PageResult<ShoppingRecord> result = PageResult.of(
                pageResult.getCurrent(),
                pageResult.getSize(),
                pageResult.getTotal(),
                pageResult.getRecords()
        );

        return Result.success(result);
    }

    /**
     * 获取性别统计数据
     * @return 性别统计数据
     */
    @ApiOperation(value = "获取性别分布统计", 
        notes = "按性别统计购物记录分布情况")
    @GetMapping("/stats/gender")
    public Result<List<Map<String, Object>>> getGenderStats() {
        return Result.success(shoppingRecordService.getGenderStats());
    }

    /**
     * 获取年龄段统计数据
     * @return 年龄段统计数据
     */
    @ApiOperation(value = "获取年龄段分布统计", 
        notes = "按年龄段统计购物记录分布情况")
    @GetMapping("/stats/age-group")
    public Result<List<Map<String, Object>>> getAgeGroupStats() {
        return Result.success(shoppingRecordService.getAgeGroupStats());
    }

    /**
     * 获取商品品类统计数据（品类=category）
     * @return 商品品类统计数据
     */
    @ApiOperation(value = "获取商品品类分布统计", 
        notes = "按品类(category)统计购物记录分布情况")
    @GetMapping("/stats/category")
    public Result<List<Map<String, Object>>> getCategoryStats() {
        return Result.success(shoppingRecordService.getCategoryStats());
    }

    /**
     * 按 item_purchased 统计，用于商品种类分布（种类=item_purchased，只读数据库）
     */
    @GetMapping("/stats/item-purchased")
    public Result<List<Map<String, Object>>> getItemPurchasedStats() {
        return Result.success(shoppingRecordService.getItemPurchasedStats());
    }

    /**
     * 按 color 统计，用于产品分析页颜色分布（只读数据库 color 列）
     */
    @GetMapping("/stats/color")
    public Result<List<Map<String, Object>>> getColorStats() {
        return Result.success(shoppingRecordService.getColorStats());
    }

    /**
     * 按 size 统计，用于产品分析页尺码分布（只读数据库 size 列）
     */
    @GetMapping("/stats/size")
    public Result<List<Map<String, Object>>> getSizeStats() {
        return Result.success(shoppingRecordService.getSizeStats());
    }

    /**
     * 产品分布：按品类+种类统计，用于产品分布图与品类包含种类列表
     */
    @GetMapping("/stats/product-distribution")
    public Result<List<Map<String, Object>>> getProductDistribution() {
        return Result.success(shoppingRecordService.getProductDistribution());
    }

    /**
     * 获取季节统计数据
     * @return 季节统计数据
     */
    @GetMapping("/stats/season")
    public Result<List<Map<String, Object>>> getSeasonStats() {
        return Result.success(shoppingRecordService.getSeasonStats());
    }

    /**
     * 获取地区统计数据
     * @return 地区统计数据
     */
    @ApiOperation(value = "获取地区简单统计", 
        notes = "获取各地区购物记录的简单统计数据")
    @GetMapping("/stats/location")
    public Result<List<Map<String, Object>>> getLocationStats() {
        return Result.success(shoppingRecordService.getLocationStats());
    }

    /**
     * 获取日期范围统计数据
     * @return 日期范围统计数据
     */
    @GetMapping("/stats/date-range")
    public Result<List<Map<String, Object>>> getDateRangeStats() {
        return Result.success(shoppingRecordService.getDateRangeStats());
    }

    /**
     * 获取客户购买统计数据
     * @return 客户购买统计数据
     */
    @GetMapping("/stats/customer")
    public Result<List<Map<String, Object>>> getCustomerStats() {
        return Result.success(shoppingRecordService.getCustomerStats());
    }

    /**
     * 获取词云数据
     * @return 词云数据
     */
    @ApiOperation(value = "获取商品评价词云数据", 
        notes = "获取商品评价的高频词数据，用于生成词云可视化")
    @GetMapping("/stats/word-cloud")
    public Result<List<WordCloudItem>> getWordCloudData() {
        return Result.success(shoppingRecordService.getWordCloudData());
    }
    
    /**
     * 获取所有商品品类（品类=category）
     * @return 商品品类列表
     */
    @GetMapping("/categories")
    public Result<List<String>> getAllCategories() {
        List<String> categories = shoppingRecordService.getAllCategories();
        return Result.success(categories);
    }
    
    /**
     * 获取统计摘要数据
     * @return 包含总销售额、订单数量、客户数量和平均订单金额的统计数据
     */
    @GetMapping("/stats/summary")
    public Result<Map<String, Object>> getSummaryStats() {
        Map<String, Object> summaryStats = shoppingRecordService.getSummaryStats();
        return Result.success(summaryStats);
    }

    /**
     * 获取详细地区分布数据
     * @return 包含地区名称、数量、占比和总金额的地区分布数据
     */
    @ApiOperation(value = "获取详细地区分布数据", 
        notes = "获取包含地区名称、数量、占比和总金额的详细地区分布数据")
    @GetMapping("/locations")
    public Result<List<LocationDistributionDTO>> getLocationDistribution() {
        List<LocationDistributionDTO> distribution = shoppingRecordService.getLocationDistribution();
        return Result.success(distribution);
    }

    // ========== 综合分析（挂在此 Controller 下避免 404）==========
    @GetMapping("/stats/comprehensive/summary/frequency-amount")
    public Result<List<Map<String, Object>>> getComprehensiveFrequencyAmount() {
        return Result.success(comprehensiveAnalysisService.getFrequencyAmount());
    }
    @GetMapping("/stats/comprehensive/summary/price-sensitivity")
    public Result<List<Map<String, Object>>> getComprehensivePriceSensitivity() {
        return Result.success(comprehensiveAnalysisService.getPriceSensitivity());
    }
    @GetMapping("/stats/comprehensive/summary/customer-segmentation")
    public Result<List<Map<String, Object>>> getComprehensiveCustomerSegmentationViz() {
        return Result.success(comprehensiveAnalysisService.getCustomerSegmentationViz());
    }
    @GetMapping("/stats/comprehensive/hive/subscription")
    public Result<List<Map<String, Object>>> getComprehensiveSubscriptionStats() {
        return Result.success(comprehensiveAnalysisService.getSubscriptionStats());
    }
    @GetMapping("/stats/comprehensive/hive/age-group")
    public Result<List<Map<String, Object>>> getComprehensiveAgeGroupStats() {
        return Result.success(comprehensiveAnalysisService.getAgeGroupStats());
    }
    @GetMapping("/stats/comprehensive/hive/season")
    public Result<List<Map<String, Object>>> getComprehensiveSeasonStats() {
        return Result.success(comprehensiveAnalysisService.getSeasonStats());
    }
    @GetMapping("/stats/comprehensive/hive/monthly")
    public Result<List<Map<String, Object>>> getComprehensiveMonthlyStats() {
        return Result.success(comprehensiveAnalysisService.getMonthlyStats());
    }
    @GetMapping("/stats/comprehensive/hive/high-value")
    public Result<List<Map<String, Object>>> getComprehensiveHighValueSegment() {
        return Result.success(comprehensiveAnalysisService.getHighValueSegment());
    }
    @GetMapping("/stats/comprehensive/hive/repeat-vs-onetime")
    public Result<List<Map<String, Object>>> getComprehensiveRepeatVsOnetime() {
        return Result.success(comprehensiveAnalysisService.getRepeatVsOnetime());
    }
    @GetMapping("/stats/comprehensive/hive/rfm")
    public Result<List<Map<String, Object>>> getComprehensiveRfmSegment() {
        return Result.success(comprehensiveAnalysisService.getRfmSegment());
    }
    @GetMapping("/stats/comprehensive/hive/rating-dist")
    public Result<List<Map<String, Object>>> getComprehensiveRatingDist() {
        return Result.success(comprehensiveAnalysisService.getRatingDist());
    }
    @GetMapping("/stats/comprehensive/advanced/lifecycle-stage")
    public Result<List<Map<String, Object>>> getComprehensiveLifecycleStage() {
        return Result.success(comprehensiveAnalysisService.getLifecycleStage());
    }
    @GetMapping("/stats/comprehensive/advanced/customer-status")
    public Result<List<Map<String, Object>>> getComprehensiveCustomerStatus() {
        return Result.success(comprehensiveAnalysisService.getCustomerStatus());
    }
    @GetMapping("/stats/comprehensive/advanced/behavior-cluster")
    public Result<List<Map<String, Object>>> getComprehensiveBehaviorCluster() {
        return Result.success(comprehensiveAnalysisService.getBehaviorCluster());
    }
    @GetMapping("/stats/comprehensive/advanced/category-association")
    public Result<List<Map<String, Object>>> getComprehensiveCategoryAssociation() {
        return Result.success(comprehensiveAnalysisService.getCategoryAssociation());
    }
    @GetMapping("/stats/comprehensive/hive/promo-item-effect")
    public Result<List<Map<String, Object>>> getComprehensivePromoItemEffect() {
        return Result.success(comprehensiveAnalysisService.getPromoItemEffect());
    }
    @GetMapping("/stats/comprehensive/hive/discount-sales")
    public Result<List<Map<String, Object>>> getComprehensiveDiscountSalesRelation() {
        return Result.success(comprehensiveAnalysisService.getDiscountSalesRelation());
    }
}