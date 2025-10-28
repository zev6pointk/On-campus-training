package com.teacher.assistant.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.teacher.assistant.entity.User;
import com.teacher.assistant.entity.ChatRecord;
import com.teacher.assistant.service.UserService;
import com.teacher.assistant.service.ChatRecordService;
import com.teacher.assistant.service.StatisticsService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.*;
import java.util.stream.Collectors;

/**
 * 统计数据服务实现
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@Service
public class StatisticsServiceImpl implements StatisticsService {

    private final UserService userService;
    private final ChatRecordService chatRecordService;

    public StatisticsServiceImpl(UserService userService, ChatRecordService chatRecordService) {
        this.userService = userService;
        this.chatRecordService = chatRecordService;
    }

    @Override
    public Map<String, Object> getUserCountStatistics() {
        List<User> allUsers = userService.list();

        Map<String, Object> result = new HashMap<>();
        result.put("total", allUsers.size());

        // 按用户类型统计
        Map<Integer, Long> typeCount = allUsers.stream()
                .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

        result.put("byType", Map.of(
                "admin", typeCount.getOrDefault(1, 0L),
                "teacher", typeCount.getOrDefault(2, 0L),
                "student", typeCount.getOrDefault(3, 0L)
        ));

        return result;
    }

    @Override
    public Map<String, Object> getUserTypeDistribution() {
        List<User> allUsers = userService.list();

        Map<String, Object> result = new HashMap<>();
        Map<Integer, Long> typeCount = allUsers.stream()
                .filter(u -> u.getUserType() != null)
                .collect(Collectors.groupingBy(User::getUserType, Collectors.counting()));

        List<Map<String, Object>> data = Arrays.asList(
                Map.of("name", "系统管理员", "value", typeCount.getOrDefault(1, 0L)),
                Map.of("name", "教师", "value", typeCount.getOrDefault(2, 0L)),
                Map.of("name", "学生", "value", typeCount.getOrDefault(3, 0L))
        );

        result.put("data", data);
        return result;
    }

    @Override
    public Map<String, Object> getDepartmentDistribution() {
        List<User> allUsers = userService.list();

        Map<String, Long> deptCount = allUsers.stream()
                .filter(u -> u.getDepartment() != null && !u.getDepartment().isEmpty())
                .collect(Collectors.groupingBy(User::getDepartment, Collectors.counting()));

        List<Map<String, Object>> data = deptCount.entrySet().stream()
                .map(entry -> {
                    Map<String, Object> map = new HashMap<>();
                    map.put("name", entry.getKey());
                    map.put("value", entry.getValue());
                    return map;
                })
                .sorted((a, b) -> Long.compare((Long) b.get("value"), (Long) a.get("value")))
                .collect(Collectors.toList());

        Map<String, Object> result = new HashMap<>();
        result.put("data", data);
        return result;
    }

    @Override
    public Map<String, Object> getActiveUserTrend(LocalDateTime startTime, LocalDateTime endTime) {
        // 这里简化处理，返回模拟数据
        List<Map<String, Object>> trendData = new ArrayList<>();
        LocalDateTime current = startTime;

        while (current.isBefore(endTime) || current.equals(endTime)) {
            Map<String, Object> dayData = new HashMap<>();
            dayData.put("date", current.toLocalDate().toString());
            dayData.put("count", (long) (Math.random() * 100 + 50)); // 随机数据
            trendData.add(dayData);
            current = current.plusDays(1);
        }

        Map<String, Object> result = new HashMap<>();
        result.put("data", trendData);
        return result;
    }

    @Override
    public Map<String, Object> getChatCountStatistics(LocalDateTime startTime, LocalDateTime endTime) {
        LambdaQueryWrapper<ChatRecord> wrapper = new LambdaQueryWrapper<>();
        wrapper.between(ChatRecord::getCreateTime, startTime, endTime);

        long total = chatRecordService.count(wrapper);

        // 按类型统计
        Map<String, Long> typeCount = chatRecordService.list(wrapper).stream()
                .collect(Collectors.groupingBy(
                        record -> record.getMessageType() == 1 ? "用户消息" : "AI回复",
                        Collectors.counting()
                ));

        Map<String, Object> result = new HashMap<>();
        result.put("total", total);
        result.put("byType", typeCount);
        return result;
    }

    @Override
    public Map<String, Object> getOverviewStatistics() {
        Map<String, Object> overview = new HashMap<>();

        // 获取各种统计
        Map<String, Object> userStats = getUserCountStatistics();
        Map<String, Object> chatStats = getChatCountStatistics(
                LocalDateTime.now().minusDays(30),
                LocalDateTime.now()
        );

        overview.put("totalUsers", userStats.get("total"));
        overview.put("totalChats", chatStats.get("total"));
        overview.put("activeUsers", userStats.get("byType"));

        // 模拟一些其他指标
        overview.put("onlineUsers", (long) ((Integer) userStats.get("total")) / 3);
        overview.put("todayMessages", (long) (Math.random() * 500 + 200));

        return overview;
    }
}
