package club.gach_dong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import test.superadmin.domain.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
