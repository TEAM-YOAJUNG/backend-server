package club.gach_dong.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import club.gach_dong.domain.Announcement;
import club.gach_dong.domain.SuperAdmin;
import club.gach_dong.repository.AnnouncementRepository;
import club.gach_dong.repository.SuperAdminRepository;
import club.gach_dong.util.JwtUtil;

import java.util.List;

@Service
public class SuperAdminService {

    @Autowired
    private SuperAdminRepository superAdminRepository;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private AnnouncementRepository announcementRepository;

    public String login(String email, String password) {
        SuperAdmin superAdmin = superAdminRepository.findByEmail(email)
                .orElseThrow(() -> new RuntimeException("사용자를 찾을 수 없습니다."));

        if (password.equals(superAdmin.getPassword())) {
            return jwtUtil.generateToken(email);
        }
        throw new RuntimeException("유효하지 않은 토큰입니다.");
    }

    public boolean validateToken(String token) {
        return jwtUtil.validateToken(token);
    }

    public void logout(String token) {
        jwtUtil.blacklistToken(token);
    }

    public Announcement createAnnouncement(String title, String content) {
        Announcement announcement = Announcement.of(title, content);
        return announcementRepository.save(announcement);
    }

    public List<Announcement> getAllAnnouncements() {
        return announcementRepository.findAll();
    }

    public Announcement getAnnouncementById(Long id) {
        return announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));
    }

    public Announcement updateAnnouncement(Long id, String title, String content) {
        Announcement announcement = announcementRepository.findById(id)
                .orElseThrow(() -> new RuntimeException("공지사항이 존재하지 않습니다."));
        announcement.setTitle(title);
        announcement.setContent(content);
        return announcementRepository.save(announcement);
    }

    public void deleteAnnouncement(Long id) {
        announcementRepository.deleteById(id);
    }

    public SuperAdmin findByEmail(String email) {
        return superAdminRepository.findByEmail(email).orElse(null);
    }
}
