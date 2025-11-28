package com.ljyh.tabletalk.service;

import com.ljyh.tabletalk.entity.Merchant;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.Date;
import java.util.HashMap;
import java.util.Map;
import java.util.function.Function;

/**
 * 商家JWT服务类
 */
@Slf4j
@Service
public class JwtMerchantService {
    
    @Value("${app.jwt.secret}")
    private String secret;
    
    @Value("${jwt.expiration:86400}") // 默认24小时
    private Long expiration;
    
    /**
     * 获取签名密钥
     */
    private Key getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(secret);
        return Keys.hmacShaKeyFor(keyBytes);
    }
    
    /**
     * 从token中获取用户名
     */
    public String extractUsername(String token) {
        return extractClaim(token, Claims::getSubject);
    }
    
    /**
     * 从token中获取过期时间
     */
    public Date extractExpiration(String token) {
        return extractClaim(token, Claims::getExpiration);
    }
    
    /**
     * 从token中提取指定声明
     */
    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver) {
        final Claims claims = extractAllClaims(token);
        return claimsResolver.apply(claims);
    }
    
    /**
     * 从token中提取所有声明
     */
    private Claims extractAllClaims(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(getSigningKey())
                .build()
                .parseClaimsJws(token)
                .getBody();
    }
    
    /**
     * 检查token是否过期
     */
    private Boolean isTokenExpired(String token) {
        return extractExpiration(token).before(new Date());
    }
    
    /**
     * 生成token
     */
    public String generateToken(Merchant merchant) {
        Map<String, Object> claims = new HashMap<>();
        claims.put("merchantId", merchant.getId());
        claims.put("restaurantId", merchant.getRestaurantId());
        claims.put("role", merchant.getRole().name());
        claims.put("name", merchant.getName());
        return createToken(claims, merchant.getUsername());
    }
    
    /**
     * 创建token
     */
    private String createToken(Map<String, Object> claims, String subject) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + expiration * 1000);
        
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(now)
                .setExpiration(expiryDate)
                .signWith(getSigningKey(), SignatureAlgorithm.HS256)
                .compact();
    }
    
    /**
     * 验证token
     */
    public Boolean validateToken(String token, UserDetails userDetails) {
        try {
            final String username = extractUsername(token);
            return (username.equals(userDetails.getUsername()) && !isTokenExpired(token));
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 验证token是否有效（验证签名和过期时间）
     */
    public Boolean validateToken(String token) {
        try {
            // 尝试解析token，这会验证签名
            extractAllClaims(token);
            // 检查是否过期
            return !isTokenExpired(token);
        } catch (io.jsonwebtoken.SignatureException e) {
            log.debug("JWT签名无效: {}", e.getMessage());
            return false;
        } catch (io.jsonwebtoken.ExpiredJwtException e) {
            log.debug("JWT token已过期: {}", e.getMessage());
            return false;
        } catch (Exception e) {
            log.debug("Token验证失败: {}", e.getMessage());
            return false;
        }
    }
    
    /**
     * 从token中获取商家ID
     */
    public Long extractMerchantId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("merchantId", Long.class);
    }
    
    /**
     * 从token中获取餐厅ID
     */
    public Long extractRestaurantId(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("restaurantId", Long.class);
    }
    
    /**
     * 从token中获取角色
     */
    public String extractRole(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("role", String.class);
    }
    
    /**
     * 从token中获取姓名
     */
    public String extractName(String token) {
        Claims claims = extractAllClaims(token);
        return claims.get("name", String.class);
    }
}