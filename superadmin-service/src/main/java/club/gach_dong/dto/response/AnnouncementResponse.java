package club.gach_dong.dto.response;

import io.swagger.v3.oas.annotations.media.Schema;
import club.gach_dong.domain.Announcement;

import java.util.Date;

public record AnnouncementResponse(
        @Schema(description = "공지사항 ID", example = "1")
        Long id,

        @Schema(description = "공지사항 제목", example = "공지사항 제목")
        String title,

        @Schema(description = "공지사항 내용", example = "공지사항 내용입니다.")
        String content,

        @Schema(description = "작성일자", example = "2024-01-01T12:00:00Z")
        Date createdDate
) {
    public static AnnouncementResponse from(Announcement announcement) {
        return new AnnouncementResponse(
                announcement.getId(),
                announcement.getTitle(),
                announcement.getContent(),
                announcement.getCreatedDate()
        );
    }
}
