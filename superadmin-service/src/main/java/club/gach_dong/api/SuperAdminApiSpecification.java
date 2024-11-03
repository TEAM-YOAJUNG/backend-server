package club.gach_dong.api;

import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import club.gach_dong.dto.request.CreateAnnouncementRequest;
import club.gach_dong.dto.request.UpdateAnnouncementRequest;
import club.gach_dong.dto.response.AnnouncementResponse;

@Tag(name = "슈퍼어드민 API", description = "슈퍼어드민 관련 API")
@RestController
@RequestMapping("/api/v1")
public interface SuperAdminApiSpecification {

    @Operation(summary = "로그아웃", description = "서비스 관리자가 로그아웃합니다.")
    @PostMapping("/logout")
    ResponseEntity<Void> logout(
            @RequestHeader("Authorization") String token);

    @Operation(summary = "공지사항 생성", description = "서비스 관리자가 새로운 공지사항을 생성합니다.")
    @PostMapping("/announcements")
    ResponseEntity<AnnouncementResponse> createAnnouncement(
            @RequestHeader("Authorization") String token,
            @Valid @RequestBody CreateAnnouncementRequest request);

    @Operation(summary = "공지사항 수정", description = "서비스 관리자가 기존 공지사항을 수정합니다.")
    @PutMapping("/announcements/{id}")
    ResponseEntity<AnnouncementResponse> updateAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id,
            @Valid @RequestBody UpdateAnnouncementRequest request);

    @Operation(summary = "공지사항 삭제", description = "서비스 관리자가 공지사항을 삭제합니다.")
    @DeleteMapping("/announcements/{id}")
    ResponseEntity<String> deleteAnnouncement(
            @RequestHeader("Authorization") String token,
            @PathVariable Long id);
}
