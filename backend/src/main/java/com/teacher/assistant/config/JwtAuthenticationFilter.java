package com.teacher.assistant.config;

import com.teacher.assistant.entity.User;
import com.teacher.assistant.service.UserService;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.util.StringUtils;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;

/**
 * JWT认证过滤器
 *
 * @author teacher
 * @since 2025-10-28
 */
@Slf4j
@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private UserService userService;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain filterChain) throws ServletException, IOException {
        try {
            String authHeader = request.getHeader(jwtConfig.getHeader());

            log.debug("JWT过滤器处理请求: {}, Authorization头存在: {}, 长度: {}",
                request.getRequestURI(),
                authHeader != null,
                authHeader != null ? authHeader.length() : 0);

            if (StringUtils.hasText(authHeader) && authHeader.startsWith(jwtConfig.getPrefix())) {
                String token = authHeader.substring(jwtConfig.getPrefix().length());
                log.debug("提取Token: {}", token.substring(0, Math.min(20, token.length())) + "...");

                if (jwtConfig.validateToken(token)) {
                    Long userId = jwtConfig.getUserIdFromToken(token);
                    String username = jwtConfig.getUsernameFromToken(token);

                    log.debug("JWT验证成功, userId: {}, username: {}", userId, username);

                    if (userId != null && username != null) {
                        User user = userService.getById(userId);
                        log.debug("查询到用户: {}, 用户状态: {}, 用户类型: {}",
                            user != null ? user.getUsername() : "null",
                            user != null ? user.getStatus() : "null",
                            user != null ? user.getUserType() : "null");

                        if (user != null) {
                            if (user.getStatus() == 1) {
                                // 创建认证对象
                                String role = "ROLE_" + getUserRole(user.getUserType());
                                log.debug("设置用户角色: {}", role);

                                UserDetails userDetails = org.springframework.security.core.userdetails.User.builder()
                                        .username(user.getUsername())
                                        .password(user.getPassword())
                                        .authorities(role)
                                        .build();

                                UsernamePasswordAuthenticationToken authentication =
                                        new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());
                                authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

                                SecurityContextHolder.getContext().setAuthentication(authentication);
                                log.debug("Spring Security认证上下文设置成功");
                            } else {
                                log.warn("用户状态无效: status={} (需要为1)", user.getStatus());
                            }
                        } else {
                            log.warn("未找到用户ID: {}", userId);
                        }
                    }
                } else {
                    log.debug("JWT验证失败");
                }
            } else {
                log.debug("未提供认证信息或格式不正确");
            }
        } catch (Exception e) {
            log.error("JWT认证失败: {}", e.getMessage(), e);
        }

        filterChain.doFilter(request, response);
    }

    /**
     * 根据用户类型获取角色
     */
    private String getUserRole(Integer userType) {
        return switch (userType) {
            case 1 -> "ADMIN";
            case 2 -> "TEACHER";
            case 3 -> "STUDENT";
            default -> "USER";
        };
    }
}
