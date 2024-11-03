package club.gach_dong.domain;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "superadmin")
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    private SuperAdmin(String email, String password) {
        this.email = email;
        this.password = password;
    }

    public static SuperAdmin of(String email, String password) {
        return new SuperAdmin(email, password);
    }
}
