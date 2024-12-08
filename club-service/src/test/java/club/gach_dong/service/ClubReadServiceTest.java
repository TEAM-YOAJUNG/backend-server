//package club.gach_dong.service;
//
//import static org.junit.jupiter.api.Assertions.*;
//import static org.mockito.Mockito.when;
//
//import club.gach_dong.domain.Club;
//import club.gach_dong.domain.ClubCategory;
//import club.gach_dong.repository.ClubRepository;
//import jakarta.servlet.http.Cookie;
//import jakarta.servlet.http.HttpServletRequest;
//import jakarta.servlet.http.HttpServletResponse;
//import java.time.LocalDateTime;
//import org.junit.jupiter.api.Test;
//import org.springframework.beans.factory.annotation.Autowired;
//import org.springframework.boot.test.context.SpringBootTest;
//import org.springframework.boot.test.mock.mockito.MockBean;
//import org.springframework.test.context.ActiveProfiles;
//import org.springframework.transaction.annotation.Transactional;
//
//@SpringBootTest
//@ActiveProfiles("test")
//class ClubReadServiceTest {
//
//    @Autowired
//    private ClubReadService clubService;
//
//    @Autowired
//    private ClubRepository clubRepository;
//
//    @MockBean
//    private HttpServletRequest request;
//
//    @MockBean
//    private HttpServletResponse response;
//
//    @Test
//    @Transactional
//    void testViewCountIncrement() {
//        Club club = clubRepository.save(Club.of("GDG Gachon", ClubCategory.SPORTS, "한줄 설명", "소개", null, "admin", LocalDateTime.now()));
//
//        // Mock 쿠키 없는 요청
//        when(request.getCookies()).thenReturn(null);
//        clubService.getClub(club.getId(), request, response);
//
//        assertEquals(1, club.getViewCount());
//
//        // Mock 쿠키 있는 요청
//        Cookie cookie = new Cookie("clubView", "[" + club.getId() + "]");
//        when(request.getCookies()).thenReturn(new Cookie[]{cookie});
//        clubService.getClub(club.getId(), request, response);
//
//        assertEquals(1, club.getViewCount()); // 조회수는 증가하지 않아야 함
//    }
//
//}