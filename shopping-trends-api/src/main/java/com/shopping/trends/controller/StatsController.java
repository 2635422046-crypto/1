package com.shopping.trends.controller;

import com.shopping.trends.common.Result;
import com.shopping.trends.dto.FrequencyStatsDTO;
import com.shopping.trends.service.UserStatsService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

/**
 * 用户统计数据控制器
 */
@CrossOrigin(origins = "*", allowedHeaders = "*", methods = {RequestMethod.GET, RequestMethod.POST, RequestMethod.PUT, RequestMethod.DELETE, RequestMethod.OPTIONS})
@RestController
@RequestMapping("/api/shopping-records/stats")
@RequiredArgsConstructor
@Api(tags = "购物记录统计接口")
public class StatsController {

    private final UserStatsService userStatsService;

    /**
     * 获取用户统计摘要数据（活跃用户数、平均购买频率、年龄/性别/地区分布）
     * @return 包含各维度统计数据的 Map
     */
    @GetMapping("/user-summary")
    @ApiOperation("获取用户统计摘要数据")
    public Result<Map<String, Object>> getUserSummaryStats() {
        Map<String, Object> stats = userStatsService.getUserSummaryStats();
        return Result.success(stats);
    }

    /**
     * 获取购买频率统计数据（按日/周/月汇总）
     * @return 含 dailyFrequency、weeklyFrequency、monthlyFrequency 三个 Map 的 DTO
     */
    @GetMapping("/frequency")
    @ApiOperation("获取购买频率统计数据")
    public Result<FrequencyStatsDTO> getPurchaseFrequencyStats() {
        FrequencyStatsDTO stats = userStatsService.getPurchaseFrequencyStats();
        return Result.success(stats);
    }
}