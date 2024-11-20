package club.gach_dong.entity;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.UUID;

@Entity
@Data
@NoArgsConstructor
@Table(name = "superadmin")
public class SuperAdmin {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(length = 255, nullable = false, unique = true)
    private String userReferenceId;

    @Column(length = 255, nullable = false, unique = true)
    private String email;

    @Column(length = 255, nullable = false)
    private String password;

    @Column(length = 100, nullable = false)
    private String name;

    public SuperAdmin(String userReferenceId, String email, String password, String name) {
        this.userReferenceId = userReferenceId;
        this.email = email;
        this.password = password;
        this.name = name;
    }

    public static SuperAdmin of(String email, String password, String name) {
        return new SuperAdmin(UUID.randomUUID().toString(), email, password, name);
    }
}