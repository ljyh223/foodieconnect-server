package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.entity.User;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * JWT服务
 */
@Slf4j
@Service
public class JwtService {
    
    @Value("${app.jwt.secret}")
    private String secretKey;
    
    @Value("${app.jwt.expiration}")
    private long jwtExpiration;
    
    @Value("${app.jwt.refresh-expiration}")
    private long refreshExpiration;
    
    @Value("${app.jwt.allowed-clock-skew:30000}")
    private long allowedClockSkew;
    
    /**
     * 从令牌中提取用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * 从令牌中提取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 从令牌中提取声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 生成访问令牌
     */
    public String generateToken(User user) {
        return generateToken(new HashMap<>(), user);
    }
    
    /**
     * 生成刷新令牌
     */
    public String generateRefreshToken(User user) {
        return buildToken(new HashMap<>(), user, refreshExpiration);
    }
    
    /**
     * 生成令牌
     */
    private String generateToken(Map<String, Object> extraClaims, User user) {
        return buildToken(extraClaims, user, jwtExpiration);
    }
    
    /**
     * 构建令牌
     */
    private String buildToken(Map<String, Object> extraClaims, User user, long expiration) {
        return Jwts
                .builder()
                .setClaims(extraClaims)
                .setSubject(user.getEmail())
                .claim("userId", user.getId())
                .claim("displayName", user.getDisplayName())
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(getSignInKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证令牌是否有效
     */
    public boolean isTokenValid(String token) {
        try {
            return !isTokenExpired(token);
        } catch (Exception e) {
            log.warn("令牌验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 检查令牌是否过期
     */
    private boolean isTokenExpired(String token) {
        try {
            Date expiration = extractExpiration(token);
            return expiration.before(new Date(System.currentTimeMillis() - allowedClockSkew));
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            return true;
        } catch (Exception e) {
            log.warn("检查令牌过期状态失败: {}", e.getMessage());
            return true;
        }
    }
    
    /**
     * 提取所有声明
     */
    private Claims extractAllClaims(String token) {
        try {
            return Jwts
                    .parserBuilder()
                    .setSigningKey(getSignInKey())
                    .setAllowedClockSkewSeconds(allowedClockSkew / 1000)
                    .build()
                    .parseClaimsJws(token)
                    .getBody();
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.warn("JWT令牌已过期: {}", e.getMessage());
            throw e;
        } catch (Exception e) {
            log.warn("JWT令牌解析失败: {}", e.getMessage());
            throw new RuntimeException("无效的JWT令牌", e);
        }
    }
    
    /**
     * 获取签名密钥
     */
    private Key getSignInKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 获取令牌过期时间
     */
    public long getExpirationTime() {
        return jwtExpiration;
    }
    
    /**
     * 获取刷新令牌过期时间
     */
    public long getRefreshExpirationTime() {
        return refreshExpiration;
    }
}