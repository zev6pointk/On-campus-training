package com.teacher.assistant.service;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

/**
 * 统计数据服务接口
 *
 * @author teacher
 * @since 2025-10-28
 */
public interface StatisticsService {

    /**
     * 获取用户数量统计
     *
     * @return 用户数量统计
     */
    Map<String, Object> getUserCountStatistics();

    /**
     * 获取用户类型分布
     *
     * @return 用户类型分布
     */
    Map<String, Object> getUserTypeDistribution();

    /**
     * 获取院系分布
     *
     * @return 院系分布
     */
    Map<String, Object> getDepartmentDistribution();

    /**
     * 获取活跃用户趋势
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 活跃用户趋势
     */
    Map<String, Object> getActiveUserTrend(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取对话量统计
     *
     * @param startTime 开始时间
     * @param endTime 结束时间
     * @return 对话量统计
     */
    Map<String, Object> getChatCountStatistics(LocalDateTime startTime, LocalDateTime endTime);

    /**
     * 获取整体统计概览
     *
     * @return 统计概览
     */
    Map<String, Object> getOverviewStatistics();
}
