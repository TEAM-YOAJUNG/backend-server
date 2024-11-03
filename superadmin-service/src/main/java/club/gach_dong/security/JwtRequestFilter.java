package club.gach_dong.security;

import io.jsonwebtoken.Jwts;
import jakarta.annotation.PostConstruct;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import club.gach_dong.domain.SuperAdmin;
import club.gach_dong.service.SuperAdminService;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.Key;
import io.jsonwebtoken.security.Keys;

@Component
public class JwtRequestFilter extends OncePerRequestFilter {

    @Autowired
    private SuperAdminService superAdminService;

    @Value("${jwt.secret}")
    private String jwtSecret;

    private Key jwtKey;

    @PostConstruct
    public void init() {
        jwtKey = Keys.hmacShaKeyFor(jwtSecret.getBytes(StandardCharsets.UTF_8));
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain chain)
            throws ServletException, IOException {
        final String authorizationHeader = request.getHeader("Authorization");

        String email = null;
        String jwt = null;

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            email = Jwts.parser()
                    .setSigningKey(jwtKey)
                    .parseClaimsJws(jwt)
                    .getBody()
                    .getSubject();
        }

        if (email != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            SuperAdmin superAdmin = superAdminService.findByEmail(email);
            if (superAdmin != null) {
                UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(
                        superAdmin, null, null);
                SecurityContextHolder.getContext().setAuthentication(authentication);
            } else {
                throw new UsernameNotFoundException("사용자를 찾을 수 없습니다: " + email);
            }
        }
        chain.doFilter(request, response);
    }
}
