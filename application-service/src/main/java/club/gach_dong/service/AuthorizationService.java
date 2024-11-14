package club.gach_dong.service;

import club.gach_dong.exception.ApplicationException.ApplicationUnauthorizedException;
import club.gach_dong.exception.ClubException;
import club.gach_dong.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;


@Service
@RequiredArgsConstructor
public class AuthorizationService {

    @Value("${msa.url.club}")
    private String clubUrl;

    private final static String REFERENCE_ID_HEADER_KEY = "X-USER-REFERENCE-ID";

    public final RestClient restClient;

    private static final Logger logger = LoggerFactory.getLogger(AuthorizationService.class);

    @Deprecated
    public String getUserId(HttpServletRequest httpServletRequest) {
        String header = httpServletRequest.getHeader(REFERENCE_ID_HEADER_KEY);

        if (header != null) {
            return header;
        }

        throw new UserException.UserNotFound();
//        return null;
    }

    public void getAuthByUserIdAndApplyId(String userId, Long recruitmentId) {

        String uri = UriComponentsBuilder.fromHttpUrl(clubUrl)
                .path("/{recruitmentId}/has-authority")
                .buildAndExpand(recruitmentId)
                .toUriString();

        try {
            Boolean result = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(REFERENCE_ID_HEADER_KEY, userId)
                    .retrieve()
                    .body(Boolean.class);

            if (Boolean.FALSE.equals(result)) {
                logger.info("유효하지 않은 권한");
                throw new ClubException.ClubAdminUnauthorizedException();
            }
//            return result != null ? result : false;

        } catch (RestClientException e) {
            System.err.println("REST 클라이언트 오류 발생: " + e.getMessage());
            throw new ClubException.ClubAdminCommunicateFailedException();
//            return false;
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            throw new ClubException.ClubAdminCommunicateFailedException();
//            return false;
        }
    }

    public void getApplyIsValid(Long recruitmentId) {

        String uri = UriComponentsBuilder.fromHttpUrl(clubUrl)
                .path("/recruitment/{recruitmentId}/is-valid")
                .buildAndExpand(recruitmentId)
                .toUriString();

        try {
            Boolean result = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .body(Boolean.class);

            if (Boolean.FALSE.equals(result)) {
                throw new ApplicationUnauthorizedException();
            }
//            return result != null ? result : false;

        } catch (RestClientException e) {
            System.err.println("REST 클라이언트 오류 발생: " + e.getMessage());
            throw new ClubException.ClubAdminCommunicateFailedException();
//            return false;
        } catch (Exception e) {
            System.err.println("예상치 못한 오류 발생: " + e.getMessage());
            throw new ClubException.ClubAdminCommunicateFailedException();
//            return false;
        }
    }

    @Deprecated
    public String getToken(HttpServletRequest request) {
        String bearerToken = request.getHeader("Authorization");
        if (StringUtils.hasText(bearerToken) && bearerToken.startsWith("Bearer")) {
            return bearerToken.substring(7);
        }
        throw new UserException.UserNotFound();
    }


}
