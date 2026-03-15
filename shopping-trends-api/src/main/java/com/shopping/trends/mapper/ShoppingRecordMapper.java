package com.shopping.trends.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import com.shopping.trends.dto.UserStatsDTO;
import com.shopping.trends.entity.ShoppingRecord;
import org.apache.ibatis.annotations.Mapper;
import org.apache.ibatis.annotations.Param;

import java.time.LocalDate;
import java.util.List;
import java.util.Map;

@Mapper
public interface ShoppingRecordMapper extends BaseMapper<ShoppingRecord> {

    /**
     * 根据日期范围查询购物记录
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 购物记录列表
     */
    List<ShoppingRecord> selectByDateRange(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    List<ShoppingRecord> selectAll();

    /**
     * 获取用户统计数据
     *
     * @param startDate 开始日期
     * @param endDate   结束日期
     * @return 用户统计数据DTO
     */
    UserStatsDTO selectUserStats(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 查询产品销量趋势数据
     * @param startDate 开始日期
     * @param endDate 结束日期
     * @return 趋势数据列表(包含date和salesCount字段)
     */
    List<Map<String, Object>> selectProductTrendData(
            @Param("startDate") LocalDate startDate,
            @Param("endDate") LocalDate endDate);

    /**
     * 查询产品品类分布数据
     * @return 品类分布列表(包含name和value字段)
     */
    List<Map<String, Object>> selectCategoryDistribution();

    /**
     * 查询热销产品排行
     * @param limit 排名数量
     * @return 热销产品列表(包含name和value字段)
     */
    List<Map<String, Object>> selectTopProducts(@Param("limit") int limit);

    /**
     * 按 color 列分组统计（XML 定义，不修改数据库）
     */
    List<Map<String, Object>> selectColorStats();

    /**
     * 按 size 列分组统计（XML 定义，不修改数据库）
     */
    List<Map<String, Object>> selectSizeStats();
}