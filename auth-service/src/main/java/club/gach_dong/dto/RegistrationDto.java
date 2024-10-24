package club.gach_dong.dto;

import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Getter;

@Getter
@Table(name = "users")
public class RegistrationDto {
    @NotBlank(message = "이메일은 필수값입니다.")
    @Pattern(regexp = "^[a-zA-Z0-9._%+-]+@gachon\\.ac\\.kr$", message = "이메일은 gachon.ac.kr 도메인이어야 합니다.")
    @Size(max = 100, message = "최대 허용 길이를 초과하였습니다.")
    private String email;

    @NotBlank(message = "인증코드는 필수값입니다.")
    @Size(min = 6, max = 6, message = "인증코드는 6자리여야 합니다.")
    private String code;

    @NotBlank(message = "비밀번호는 필수값입니다.")
    @Pattern(regexp = "(?=.*[0-9])(?=.*[a-zA-Z])(?=.*\\W)(?=\\S+$).{8,16}", message = "비밀번호는 8~16자 영문, 숫자, 특수문자를 사용하세요.")
    private String password;

    @NotBlank(message = "이름은 필수값입니다.")
    @Size(max = 100, message = "최대 허용 길이를 초과하였습니다.")
    private String name;

    @NotBlank(message = "권한은 필수값입니다.")
    private String role;
}