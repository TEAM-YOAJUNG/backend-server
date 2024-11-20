package club.gach_dong.util;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Component;
import club.gach_dong.entity.SuperAdmin;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Date;
import java.util.concurrent.TimeUnit;

@Component
public class JwtUtil {

    private final Key superAdminJwtKey;
    private final RedisTemplate<String, String> redisTemplate;

    public JwtUtil(
            @Value("${jwt.superadmin.secret}") String superAdminJwtSecret,
            RedisTemplate<String, String> redisTemplate) {
        this.superAdminJwtKey = Keys.hmacShaKeyFor(superAdminJwtSecret.getBytes(StandardCharsets.UTF_8));
        this.redisTemplate = redisTemplate;
    }

    public String generateSuperAdminToken(SuperAdmin superAdmin) {
        Date expirationDate = Date.from(Instant.now().plus(1, ChronoUnit.DAYS));
        return Jwts.builder()
                .setSubject(superAdmin.getEmail())
                .claim("user_reference_id", superAdmin.getUserReferenceId())
                .setExpiration(expirationDate)
                .signWith(superAdminJwtKey, SignatureAlgorithm.HS512)
                .compact();
    }

    public String generateSuperAdminRefreshToken(SuperAdmin superAdmin) {
        Date expirationDate = Date.from(Instant.now().plus(7, ChronoUnit.DAYS));
        String refreshToken = Jwts.builder()
                .setSubject(superAdmin.getEmail())
                .claim("user_reference_id", superAdmin.getUserReferenceId())
                .setExpiration(expirationDate)
                .signWith(superAdminJwtKey, SignatureAlgorithm.HS512)
                .compact();

        redisTemplate.opsForValue().set(refreshToken, superAdmin.getEmail(), 7, TimeUnit.DAYS);
        return refreshToken;
    }

    public String getSuperAdminEmailFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(superAdminJwtKey)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 토큰입니다.");
        }
    }

    public String getSuperAdminReferenceIdFromToken(String token) {
        try {
            Claims claims = Jwts.parser()
                    .setSigningKey(superAdminJwtKey)
                    .parseClaimsJws(token.replace("Bearer ", ""))
                    .getBody();
            return claims.get("user_reference_id", String.class);
        } catch (Exception e) {
            throw new RuntimeException("유효하지 않은 관리자 토큰입니다.");
        }
    }

    public boolean validateSuperAdminToken(String token) {
        if (isTokenBlacklisted(token.replace("Bearer ", ""))) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(superAdminJwtKey).parseClaimsJws(token.replace("Bearer ", ""));
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public boolean validateSuperAdminRefreshToken(String adminRefreshToken) {
        String token = adminRefreshToken.replace("Bearer ", "");

        if (isTokenBlacklisted(token)) {
            return false;
        }

        try {
            Jwts.parser().setSigningKey(superAdminJwtKey).parseClaimsJws(token);
            return true;
        } catch (Exception e) {
            return false;
        }
    }

    public void blacklistSuperAdminToken(String token) {
        Claims claims = Jwts.parser()
                .setSigningKey(superAdminJwtKey)
                .parseClaimsJws(token.replace("Bearer ", ""))
                .getBody();

        Date expirationDate = claims.getExpiration();
        Date currentDate = new Date();

        long remainingValidity = expirationDate.getTime() - currentDate.getTime();

        if (remainingValidity > 0) {
            redisTemplate.opsForValue().set("blacklist:" + token, "blacklisted", remainingValidity, TimeUnit.MILLISECONDS);
        }
    }

    public void blacklistSuperAdminRefreshToken(String adminRefreshToken) {
        String token = adminRefreshToken.replace("Bearer ", "");

        Claims claims = Jwts.parser()
                .setSigningKey(superAdminJwtKey)
                .parseClaimsJws(token)
                .getBody();

        Date expirationDate = claims.getExpiration();
        Date currentDate = new Date();

        long remainingValidity = expirationDate.getTime() - currentDate.getTime();

        if (remainingValidity > 0) {
            redisTemplate.opsForValue().set("blacklist:" + token, "blacklisted", remainingValidity, TimeUnit.MILLISECONDS);
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return redisTemplate.hasKey("blacklist:" + token);
    }
}