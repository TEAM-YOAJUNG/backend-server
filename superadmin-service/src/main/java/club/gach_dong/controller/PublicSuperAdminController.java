package club.gach_dong.controller;

import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import club.gach_dong.api.PublicSuperAdminApiSpecification;
import club.gach_dong.dto.request.LoginRequest;
import club.gach_dong.dto.response.AuthResponse;
import club.gach_dong.entity.SuperAdmin;
import club.gach_dong.service.SuperAdminService;
import club.gach_dong.dto.response.AnnouncementResponse;
import club.gach_dong.entity.Announcement;
import club.gach_dong.util.JwtUtil;

import java.util.List;
import java.util.stream.Collectors;

@RestController
@RequiredArgsConstructor
public class PublicSuperAdminController implements PublicSuperAdminApiSpecification {

    private final SuperAdminService superAdminService;
    private final JwtUtil jwtUtil;

    @Override
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginRequest loginRequest) {
        SuperAdmin superAdmin = superAdminService.findByEmail(loginRequest.email());

        if (superAdmin == null || !superAdminService.checkPassword(superAdmin, loginRequest.password())) {
            return ResponseEntity.status(HttpStatus.UNAUTHORIZED)
                    .body(AuthResponse.withMessage("이메일 또는 비밀번호가 올바르지 않습니다."));
        }

        String accessToken = jwtUtil.generateSuperAdminToken(superAdmin);
        String refreshToken = jwtUtil.generateSuperAdminRefreshToken(superAdmin);

        return ResponseEntity.ok(AuthResponse.of(accessToken, refreshToken));
    }

    @Override
    public ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements() {
        List<AnnouncementResponse> announcements = superAdminService.getAllAnnouncements()
                .stream()
                .map(AnnouncementResponse::from)
                .collect(Collectors.toList());
        return ResponseEntity.ok(announcements);
    }

    @Override
    public ResponseEntity<AnnouncementResponse> getAnnouncementById(@PathVariable Long id) {
        try {
            Announcement announcement = superAdminService.getAnnouncementById(id);
            return ResponseEntity.ok(AnnouncementResponse.from(announcement));
        } catch (RuntimeException ex) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
}
