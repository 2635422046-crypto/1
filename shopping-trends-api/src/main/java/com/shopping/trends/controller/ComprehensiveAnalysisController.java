package com.shopping.trends.controller;

import com.shopping.trends.common.Result;
import com.shopping.trends.service.ComprehensiveAnalysisService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/**
 * 综合分析 API（对应三个 SQL 文件的可视化，只读不修改数据库）
 */
@Api(tags = "综合分析API")
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/comprehensive")
@RequiredArgsConstructor
public class ComprehensiveAnalysisController {

    private final ComprehensiveAnalysisService comprehensiveAnalysisService;

    // ========== 总结视图 summary_views ==========
    @GetMapping("/summary/frequency-amount")
    @ApiOperation("购买频率与金额关系")
    public Result<List<Map<String, Object>>> getFrequencyAmount() {
        return Result.success(comprehensiveAnalysisService.getFrequencyAmount());
    }

    @GetMapping("/summary/price-sensitivity")
    @ApiOperation("客户价格敏感度")
    public Result<List<Map<String, Object>>> getPriceSensitivity() {
        return Result.success(comprehensiveAnalysisService.getPriceSensitivity());
    }

    @GetMapping("/summary/time-series")
    @ApiOperation("时间序列按日")
    public Result<List<Map<String, Object>>> getTimeSeries() {
        return Result.success(comprehensiveAnalysisService.getTimeSeries());
    }

    @GetMapping("/summary/customer-segmentation")
    @ApiOperation("客户细分可视化")
    public Result<List<Map<String, Object>>> getCustomerSegmentationViz() {
        return Result.success(comprehensiveAnalysisService.getCustomerSegmentationViz());
    }

    // ========== Hive 分析 ==========
    @GetMapping("/hive/subscription")
    @ApiOperation("会员vs非会员")
    public Result<List<Map<String, Object>>> getSubscriptionStats() {
        return Result.success(comprehensiveAnalysisService.getSubscriptionStats());
    }

    @GetMapping("/hive/age-group")
    @ApiOperation("年龄段消费")
    public Result<List<Map<String, Object>>> getAgeGroupStats() {
        return Result.success(comprehensiveAnalysisService.getAgeGroupStats());
    }

    @GetMapping("/hive/season")
    @ApiOperation("季节性购买")
    public Result<List<Map<String, Object>>> getSeasonStats() {
        return Result.success(comprehensiveAnalysisService.getSeasonStats());
    }

    @GetMapping("/hive/monthly")
    @ApiOperation("月度销售")
    public Result<List<Map<String, Object>>> getMonthlyStats() {
        return Result.success(comprehensiveAnalysisService.getMonthlyStats());
    }

    @GetMapping("/hive/high-value")
    @ApiOperation("高价值客户分段")
    public Result<List<Map<String, Object>>> getHighValueSegment() {
        return Result.success(comprehensiveAnalysisService.getHighValueSegment());
    }

    @GetMapping("/hive/repeat-vs-onetime")
    @ApiOperation("复购vs一次性")
    public Result<List<Map<String, Object>>> getRepeatVsOnetime() {
        return Result.success(comprehensiveAnalysisService.getRepeatVsOnetime());
    }

    @GetMapping("/hive/rfm")
    @ApiOperation("RFM分段")
    public Result<List<Map<String, Object>>> getRfmSegment() {
        return Result.success(comprehensiveAnalysisService.getRfmSegment());
    }

    @GetMapping("/hive/rating-dist")
    @ApiOperation("评分分布")
    public Result<List<Map<String, Object>>> getRatingDist() {
        return Result.success(comprehensiveAnalysisService.getRatingDist());
    }

    // ========== 高级分析 advanced ==========
    @GetMapping("/advanced/lifecycle-stage")
    @ApiOperation("客户生命周期阶段")
    public Result<List<Map<String, Object>>> getLifecycleStage() {
        return Result.success(comprehensiveAnalysisService.getLifecycleStage());
    }

    @GetMapping("/advanced/customer-status")
    @ApiOperation("客户状态")
    public Result<List<Map<String, Object>>> getCustomerStatus() {
        return Result.success(comprehensiveAnalysisService.getCustomerStatus());
    }

    @GetMapping("/advanced/behavior-cluster")
    @ApiOperation("行为聚类")
    public Result<List<Map<String, Object>>> getBehaviorCluster() {
        return Result.success(comprehensiveAnalysisService.getBehaviorCluster());
    }

    @GetMapping("/advanced/category-association")
    @ApiOperation("品类关联")
    public Result<List<Map<String, Object>>> getCategoryAssociation() {
        return Result.success(comprehensiveAnalysisService.getCategoryAssociation());
    }
}
