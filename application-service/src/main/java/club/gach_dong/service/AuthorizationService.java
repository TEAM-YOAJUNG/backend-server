package club.gach_dong.service;

import club.gach_dong.exception.ApplicationException.ApplicationUnauthorizedException;
import club.gach_dong.exception.ClubException;
import club.gach_dong.exception.UserException;
import jakarta.servlet.http.HttpServletRequest;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;


@Slf4j
@Service
@RequiredArgsConstructor
public class AuthorizationService {

    @Value("${msa.url.club}")
    private String clubUrl;

    private final static String REFERENCE_ID_HEADER_KEY = "X-USER-REFERENCE-ID";

    public final RestClient restClient;

    @Deprecated
    public String getUserId(HttpServletRequest httpServletRequest) {
        String header = httpServletRequest.getHeader(REFERENCE_ID_HEADER_KEY);

        if (header != null) {
            return header;
        }

        throw new UserException.UserNotFound();
    }

    public void getAuthByUserIdAndApplyId(String userId, Long recruitmentId) {

        String uri = UriComponentsBuilder.fromHttpUrl(clubUrl)
                .path("/{recruitmentId}/has-authority")
                .queryParam("recruitmentId", recruitmentId)
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
                throw new ClubException.ClubAdminUnauthorizedException();
            }

        } catch (RestClientException e) {
            log.error("REST 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
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

        } catch (RestClientException e) {
            log.error("REST 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        }
    }

    public void getAuthByUserIdAndClubId(String userId, Long clubId) {

        String uri = UriComponentsBuilder.fromHttpUrl(clubUrl)
                .path("/has-authority/{clubId}")
                .buildAndExpand(clubId)
                .toUriString();

        try {
            Boolean result = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .header(REFERENCE_ID_HEADER_KEY, userId)
                    .retrieve()
                    .body(Boolean.class);

            if (Boolean.FALSE.equals(result)) {
                throw new ClubException.ClubAdminUnauthorizedException();
            }

        } catch (RestClientException e) {
            log.error("REST 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        } catch (Exception e) {
            log.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
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
