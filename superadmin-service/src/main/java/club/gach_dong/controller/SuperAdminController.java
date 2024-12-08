package club.gach_dong.controller;

import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import club.gach_dong.api.SuperAdminApiSpecification;
import club.gach_dong.dto.response.TokenResponse;
import club.gach_dong.entity.SuperAdmin;
import club.gach_dong.service.SuperAdminService;
import club.gach_dong.dto.request.CreateAnnouncementRequest;
import club.gach_dong.dto.request.UpdateAnnouncementRequest;
import club.gach_dong.dto.response.AnnouncementResponse;
import club.gach_dong.entity.Announcement;
import club.gach_dong.util.JwtUtil;

@RestController
@RequiredArgsConstructor
public class SuperAdminController implements SuperAdminApiSpecification {

    private final SuperAdminService superAdminService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<String> logout(@RequestHeader("Authorization") String token, @RequestHeader("Refresh-Token") String refreshToken) {
        try {
            String jwtToken = token.startsWith("Bearer ") ? token.substring(7) : token;

            superAdminService.blacklistSuperAdminToken(jwtToken);
            superAdminService.blacklistSuperAdminRefreshToken(refreshToken);

            return ResponseEntity.ok("로그아웃 되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("로그아웃 실패: " + e.getMessage());
        }
    }

    @Override
    public ResponseEntity<AnnouncementResponse> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @RequestBody CreateAnnouncementRequest request) {
        if (!superAdminService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            Announcement announcement = superAdminService.createAnnouncement(request.title(), request.content());
            return ResponseEntity.status(HttpStatus.CREATED).body(AnnouncementResponse.from(announcement));
        } catch (Exception ex) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(null);
        }
    }

    @Override
    public ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @RequestBody UpdateAnnouncementRequest request) {
        if (!superAdminService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
        }
        try {
            Announcement announcement = superAdminService.updateAnnouncement(id, request.title(), request.content());
            return ResponseEntity.ok(AnnouncementResponse.from(announcement));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }

    @Override
    public ResponseEntity<String> deleteAnnouncement(@RequestHeader("Authorization") String token,
                                                     @PathVariable Long id) {
        if (!superAdminService.validateToken(token)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("유효하지 않은 토큰입니다.");
        }
        try {
            superAdminService.deleteAnnouncement(id);
            return ResponseEntity.ok("공지사항이 성공적으로 삭제되었습니다.");
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body("공지사항 삭제 실패: " + ex.getMessage());
        }
    }

    @Override
    public ResponseEntity<TokenResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        if (!jwtUtil.validateSuperAdminRefreshToken(refreshToken)) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(TokenResponse.withMessage("유효하지 않은 Refresh Token입니다."));
        }

        try {
            String email = jwtUtil.getSuperAdminEmailFromToken(refreshToken);
            SuperAdmin superAdmin = superAdminService.findByEmail(email);

            String newAccessToken = jwtUtil.generateSuperAdminToken(superAdmin);

            String newRefreshToken = jwtUtil.generateSuperAdminRefreshToken(superAdmin);

            jwtUtil.blacklistSuperAdminRefreshToken(refreshToken);

            return ResponseEntity.ok(TokenResponse.of(newAccessToken, newRefreshToken));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                    .body(TokenResponse.withMessage("Access Token 재발급 실패: " + e.getMessage()));
        }
    }
}
