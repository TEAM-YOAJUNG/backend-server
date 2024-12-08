package club.gach_dong.repository;

import org.springframework.data.jpa.repository.JpaRepository;
import club.gach_dong.entity.Announcement;

public interface AnnouncementRepository extends JpaRepository<Announcement, Long> {
}
