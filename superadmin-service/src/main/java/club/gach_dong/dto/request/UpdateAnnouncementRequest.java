package club.gach_dong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record UpdateAnnouncementRequest(
        @Schema(description = "수정할 공지사항 제목", example = "수정된 공지사항 제목")
        String title,

        @Schema(description = "수정할 공지사항 내용", example = "수정된 공지사항 내용")
        String content
) {}
