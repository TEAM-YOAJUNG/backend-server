package club.gach_dong.dto;

import lombok.Getter;

@Getter
public class AuthResponse {
    private String token;

    private AuthResponse(String token) {
        this.token = token;
    }

    public static AuthResponse of(String token) {
        return new AuthResponse(token);
    }
}
