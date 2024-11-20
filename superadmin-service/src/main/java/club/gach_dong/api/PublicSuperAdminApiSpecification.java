package club.gach_dong.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import club.gach_dong.dto.request.LoginRequest;
import club.gach_dong.dto.response.AnnouncementResponse;
import club.gach_dong.dto.response.AuthResponse;

import java.util.List;

@Tag(name = "Public 슈퍼어드민 API", description = "Public한 슈퍼어드민 관련 API")
@RestController
@RequestMapping("/public/api/v1")
public interface PublicSuperAdminApiSpecification {

    @Operation(summary = "로그인", description = "서비스 관리자가 로그인합니다.")
    @PostMapping("/login")
    ResponseEntity<AuthResponse> login(
            @Parameter(description = "로그인 정보") @Valid @RequestBody LoginRequest loginRequest);

    @Operation(summary = "모든 공지사항 조회", description = "모든 공지사항을 조회합니다.")
    @GetMapping("/announcements")
    ResponseEntity<List<AnnouncementResponse>> getAllAnnouncements();

    @Operation(summary = "특정 공지사항 조회", description = "ID로 특정 공지사항을 조회합니다.")
    @GetMapping("/announcements/{id}")
    ResponseEntity<AnnouncementResponse> getAnnouncementById(
            @Parameter(description = "공지사항 ID") @PathVariable Long id);
}
