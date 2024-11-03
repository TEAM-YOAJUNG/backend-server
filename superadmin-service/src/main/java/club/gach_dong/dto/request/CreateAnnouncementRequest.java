package club.gach_dong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record CreateAnnouncementRequest(
        @Schema(description = "공지사항 제목", example = "공지사항 제목")
        String title,

        @Schema(description = "공지사항 내용", example = "공지사항 내용")
        String content
) {}
