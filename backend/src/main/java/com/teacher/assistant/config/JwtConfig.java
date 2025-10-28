package com.teacher.assistant.config;

import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import lombok.Data;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;

/**
 * JWT配置与工具类
 *
 * @author teacher
 * @since 2025-10-28
 */
@Data
@Component
public class JwtConfig {

    @Value("${jwt.secret}")
    private String secret;

    @Value("${jwt.expiration}")
    private Long expiration;

    @Value("${jwt.header}")
    private String header;

    @Value("${jwt.prefix}")
    private String prefix;

    /**
     * 生成JWT Token
     */
    public String generateToken(String username, Long userId, Integer userType) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("userId", userId);
        claims.put("username", username);
        claims.put("userType", userType);
        return generateToken(claims);
    }

    /**
     * 从数据声明生成令牌
     */
    private String generateToken(Map<String, Object> claims) {
        Date expirationDate = new Date(System.currentTimeMillis() + expiration * 1000);
        return Jwts.builder()
                .setClaims(claims)
                .setExpiration(expirationDate)
                .signWith(getSigningKey())
                .compact();
    }

    /**
     * 从令牌中获取数据声明
     */
    private Claims getClaimsFromToken(String token) {
        Claims claims;
        try {
            claims = Jwts.parser()
                    .verifyWith(getSigningKey())
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (Exception e) {
            claims = null;
        }
        return claims;
    }

    /**
     * 从令牌中获取用户名
     */
    public String getUsernameFromToken(String token) {
        String username;
        try {
            final Claims claims = getClaimsFromToken(token);
            username = claims.get("username").toString();
        } catch (Exception e) {
            username = null;
        }
        return username;
    }

    /**
     * 从令牌中获取用户ID
     */
    public Long getUserIdFromToken(String token) {
        Long userId;
        try {
            final Claims claims = getClaimsFromToken(token);
            userId = Long.valueOf(claims.get("userId").toString());
        } catch (Exception e) {
            userId = null;
        }
        return userId;
    }

    /**
     * 从令牌中获取用户类型
     */
    public Integer getUserTypeFromToken(String token) {
        Integer userType;
        try {
            final Claims claims = getClaimsFromToken(token);
            userType = Integer.valueOf(claims.get("userType").toString());
        } catch (Exception e) {
            userType = null;
        }
        return userType;
    }

    /**
     * 获取令牌的过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        Date expiration;
        try {
            final Claims claims = getClaimsFromToken(token);
            expiration = claims.getExpiration();
        } catch (Exception e) {
            expiration = null;
        }
        return expiration;
    }

    /**
     * 判断令牌是否过期
     */
    public Boolean isTokenExpired(String token) {
        final Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 验证令牌
     */
    public Boolean validateToken(String token) {
        try {
            getClaimsFromToken(token);
            return true;
        } catch (JwtException | IllegalArgumentException e) {
            return false;
        }
    }

    /**
     * 获取签名密钥
     */
    private SecretKey getSigningKey() {
        byte[] keyBytes = secret.getBytes();
        return Keys.hmacShaKeyFor(keyBytes);
    }
}
