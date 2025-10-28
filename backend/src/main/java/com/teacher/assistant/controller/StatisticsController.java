package com.teacher.assistant.controller;

import com.teacher.assistant.config.JwtConfig;
import com.teacher.assistant.config.ResultVO;
import com.teacher.assistant.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.format.annotation.DateTimeFormat;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;
import java.util.Map;

/**
 * 统计数据控制器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@RestController
@RequestMapping("/statistics")
@CrossOrigin(origins = "*", maxAge = 3600)
public class StatisticsController {

    @Autowired
    private StatisticsService statisticsService;

    @Autowired
    private JwtConfig jwtConfig;

    /**
     * 获取概览统计
     */
    @GetMapping("/overview")
    public ResultVO<Map<String, Object>> getOverview(@RequestHeader("Authorization") String authHeader) {
        try {
            log.debug("收到获取概览统计请求, Authorization: {}", authHeader != null ? "Bearer [已隐藏]" : "null");

            Map<String, Object> overview = statisticsService.getOverviewStatistics();
            return ResultVO.success(overview);

        } catch (Exception e) {
            log.error("获取概览统计失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取用户数量统计
     */
    @GetMapping("/users/count")
    public ResultVO<Map<String, Object>> getUserCountStatistics(@RequestHeader("Authorization") String authHeader) {
        try {
            log.debug("收到获取用户数量统计请求, Authorization: {}", authHeader != null ? "Bearer [已隐藏]" : "null");

            Map<String, Object> stats = statisticsService.getUserCountStatistics();
            return ResultVO.success(stats);

        } catch (Exception e) {
            log.error("获取用户数量统计失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取用户类型分布
     */
    @GetMapping("/users/distribution")
    public ResultVO<Map<String, Object>> getUserTypeDistribution(@RequestHeader("Authorization") String authHeader) {
        try {
            log.debug("收到获取用户类型分布请求, Authorization: {}", authHeader != null ? "Bearer [已隐藏]" : "null");

            Map<String, Object> stats = statisticsService.getUserTypeDistribution();
            return ResultVO.success(stats);

        } catch (Exception e) {
            log.error("获取用户类型分布失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取院系分布
     */
    @GetMapping("/department/distribution")
    public ResultVO<Map<String, Object>> getDepartmentDistribution(@RequestHeader("Authorization") String authHeader) {
        try {
            log.debug("收到获取院系分布请求, Authorization: {}", authHeader != null ? "Bearer [已隐藏]" : "null");

            Map<String, Object> stats = statisticsService.getDepartmentDistribution();
            return ResultVO.success(stats);

        } catch (Exception e) {
            log.error("获取院系分布失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取活跃用户趋势
     */
    @GetMapping("/users/trend")
    public ResultVO<Map<String, Object>> getActiveUserTrend(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "2025-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startTime,
            @RequestParam(defaultValue = "2025-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endTime) {

        try {
            log.debug("收到获取活跃用户趋势请求, Authorization: {}, startTime: {}, endTime: {}",
                authHeader != null ? "Bearer [已隐藏]" : "null", startTime, endTime);

            Map<String, Object> stats = statisticsService.getActiveUserTrend(startTime, endTime);
            return ResultVO.success(stats);

        } catch (Exception e) {
            log.error("获取活跃用户趋势失败", e);
            return ResultVO.error(e.getMessage());
        }
    }

    /**
     * 获取对话量统计
     */
    @GetMapping("/chat/count")
    public ResultVO<Map<String, Object>> getChatCountStatistics(
            @RequestHeader("Authorization") String authHeader,
            @RequestParam(defaultValue = "2025-01-01") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime startTime,
            @RequestParam(defaultValue = "2025-12-31") @DateTimeFormat(iso = DateTimeFormat.ISO.DATE) LocalDateTime endTime) {

        try {
            log.debug("收到获取对话量统计请求, Authorization: {}, startTime: {}, endTime: {}",
                authHeader != null ? "Bearer [已隐藏]" : "null", startTime, endTime);

            Map<String, Object> stats = statisticsService.getChatCountStatistics(startTime, endTime);
            return ResultVO.success(stats);

        } catch (Exception e) {
            log.error("获取对话量统计失败", e);
            return ResultVO.error(e.getMessage());
        }
    }
}
