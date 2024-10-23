package club.gach_dong.response.status;

import lombok.AllArgsConstructor;
import lombok.Getter;
import org.springframework.http.HttpStatus;


@Getter
@AllArgsConstructor
public enum ErrorStatus {
    _INTERNAL_SERVER_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "COMMON500", "서버 에러, 관리자에게 문의 바랍니다."),
    _BAD_REQUEST(HttpStatus.BAD_REQUEST, "COMMON400", "잘못된 요청입니다."),
    _UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "COMMON401", "인증이 필요합니다."),
    _FORBIDDEN(HttpStatus.FORBIDDEN, "COMMON403", "금지된 요청입니다."),
    _EMPTY_FIELD(HttpStatus.NO_CONTENT, "COMMON404", "입력 값이 누락되었습니다."),
    
    CLUB_UNAUTHORIZED(HttpStatus.UNAUTHORIZED, "APPLICATION401", "인증이 필요합니다."),

    APPLICATION_FORM_NOT_FOUND(HttpStatus.NOT_FOUND, "APPLICATIONFORM401", "ApplicationForm이 없습니다."),
    APPLICATION_FORM_IN_USE(HttpStatus.CONFLICT, "APPLICATIONFORM402", "이미 사용중인 지원서 양식입니다."),
    ;
    //
    private final HttpStatus httpStatus;
    private final String code;
    private final String message;
}
