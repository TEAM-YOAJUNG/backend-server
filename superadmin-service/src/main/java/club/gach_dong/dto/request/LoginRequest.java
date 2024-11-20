package club.gach_dong.dto.request;

import io.swagger.v3.oas.annotations.media.Schema;

public record LoginRequest(
        @Schema(description = "서비스 관리자 이메일", example = "superadmin1234")
        String email,

        @Schema(description = "서비스 관리자 비밀번호", example = "superadmin1234")
        String password
) {}
