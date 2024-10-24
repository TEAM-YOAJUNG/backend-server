package club.gach_dong.controller;

import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import club.gach_dong.api.AuthApiSpecification;
import club.gach_dong.dto.AuthResponse;
import club.gach_dong.dto.ChangePasswordDto;
import club.gach_dong.dto.LoginDto;
import club.gach_dong.dto.RegistrationDto;
import club.gach_dong.entity.User;
import club.gach_dong.service.UserService;
import club.gach_dong.util.JwtUtil;

@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController implements AuthApiSpecification {

    private final UserService userService;
    private final JavaMailSender mailSender;
    private final JwtUtil jwtUtil;

    @Override
    @PostMapping("/send_verification_code")
    public ResponseEntity<String> sendVerificationCode(@RequestParam String email) {
        try {
            userService.sendVerificationCode(email);
            return ResponseEntity.ok("인증 코드가 이메일로 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("인증 코드 발송 실패: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/signup")
    public ResponseEntity<String> completeRegistration(@Valid @RequestBody RegistrationDto registrationDto) {
        try {
            userService.verifyCode(registrationDto.getEmail(), registrationDto.getCode());

            userService.completeRegistration(
                    registrationDto.getEmail(),
                    registrationDto.getPassword(),
                    registrationDto.getName(),
                    registrationDto.getRole()
            );

            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("회원가입 실패: " + e.getMessage());
        }
    }


    @Override
    @PostMapping("/login")
    public ResponseEntity<AuthResponse> login(@Valid @RequestBody LoginDto loginDto) {
        User user = userService.findByEmail(loginDto.getEmail());

        if (user != null && userService.checkPassword(user, loginDto.getPassword())) {
            if (!user.isEnabled()) {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
            }

            String token = jwtUtil.generateToken(user);
            return ResponseEntity.ok(AuthResponse.of(token));
        }
        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(null);
    }

    @Override
    @PostMapping("/reset_password")
    public ResponseEntity<String> resetPassword(@RequestHeader("Authorization") String token) {
        try {
            String email = jwtUtil.getEmailFromToken(token);
            userService.resetPassword(email);
            return ResponseEntity.ok("임시 비밀번호가 이메일로 발송되었습니다.");
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 재발급 실패: " + e.getMessage());
        }
    }

    @Override
    @PostMapping("/change_password")
    public ResponseEntity<String> changePassword(@RequestHeader("Authorization") String token, @Valid @RequestBody ChangePasswordDto changePasswordDto) {
        try {
            String email = jwtUtil.getEmailFromToken(token);
            User user = userService.findByEmail(email);

            if (userService.checkPassword(user, changePasswordDto.getCurrentPassword())) {
                user.setPassword(userService.encodePassword(changePasswordDto.getNewPassword()));
                userService.updateUser(user);
                return ResponseEntity.ok("비밀번호가 성공적으로 변경되었습니다.");
            } else {
                return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body("현재 비밀번호가 올바르지 않습니다.");
            }
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body("비밀번호 변경 실패: " + e.getMessage());
        }
    }
}
