package club.gach_dong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import club.gach_dong.entity.SuperAdmin;

import java.util.Optional;

public interface SuperAdminRepository extends JpaRepository<SuperAdmin, Long> {
    Optional<SuperAdmin> findByEmail(String email);
    Optional<SuperAdmin> findByUserReferenceId(String userReferenceId);
}
