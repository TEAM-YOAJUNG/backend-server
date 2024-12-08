package club.gach_dong.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.Parameter;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import club.gach_dong.dto.request.CreateAnnouncementRequest;
import club.gach_dong.dto.request.UpdateAnnouncementRequest;
import club.gach_dong.dto.response.AnnouncementResponse;
import club.gach_dong.dto.response.TokenResponse;

@Tag(name = "슈퍼어드민 API", description = "슈퍼어드민 관련 API")
@RestController
@RequestMapping("/api/v1")
public interface SuperAdminApiSpecification {

    @Operation(summary = "로그아웃", description = "서비스 관리자가 로그아웃합니다.",
            security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/logout")
    ResponseEntity<String> logout(
            @Parameter(description = "JWT 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "Refresh Token") @RequestHeader("Refresh-Token") String refreshToken);

    @Operation(summary = "공지사항 생성", description = "서비스 관리자가 새로운 공지사항을 생성합니다.",
            security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/create-announcements")
    ResponseEntity<AnnouncementResponse> createAnnouncement(
            @Parameter(description = "JWT 토큰") @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateAnnouncementRequest request);

    @Operation(summary = "공지사항 수정", description = "서비스 관리자가 기존 공지사항을 수정합니다.",
            security = @SecurityRequirement(name = "Authorization"))
    @PutMapping("/update-announcements/{id}")
    ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @Parameter(description = "JWT 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "공지사항 ID") @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request);

    @Operation(summary = "공지사항 삭제", description = "서비스 관리자가 공지사항을 삭제합니다.",
            security = @SecurityRequirement(name = "Authorization"))
    @DeleteMapping("/delete-announcements/{id}")
    ResponseEntity<String> deleteAnnouncement(
            @Parameter(description = "JWT 토큰") @RequestHeader("Authorization") String token,
            @Parameter(description = "공지사항 ID") @PathVariable Long id);

    @Operation(summary = "Refresh Token 재발급", description = "유효한 Refresh Token을 사용하여 새로운 Refresh Token과 Access Token을 발급받습니다.",
            security = @SecurityRequirement(name = "Authorization"))
    @PostMapping("/refresh-token")
    ResponseEntity<TokenResponse> refreshToken(
            @Parameter(description = "Refresh Token") @RequestHeader("Authorization") String refreshToken);
}
