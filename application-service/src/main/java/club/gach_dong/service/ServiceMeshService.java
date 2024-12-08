package club.gach_dong.service;

import club.gach_dong.dto.response.AuthResponseDTO;
import club.gach_dong.dto.response.ClubResponseDTO;
import club.gach_dong.exception.ClubException;
import club.gach_dong.exception.UserException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;
import org.springframework.web.util.UriComponentsBuilder;

@Service
@RequiredArgsConstructor
public class ServiceMeshService {
    @Value("${msa.url.userDetail}")
    private String userDetailUrl;

    @Value("${msa.url.clubPublic}")
    private String clubPublicUrl;

    public final RestClient restClient;

    private static final Logger logger = LogManager.getLogger(ServiceMeshService.class);

    public List<AuthResponseDTO.getUserProfile> getUserProfiles(List<String> userIds) {
        String uri = UriComponentsBuilder.fromHttpUrl(userDetailUrl)
                .path("/profiles")
                .toUriString();

        Map<String, List<String>> requestBody = new HashMap<>();
        requestBody.put("userReferenceId", userIds);

        try {
            return restClient.post()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .header("Authorization", "Bearer ")
                    .body(requestBody)
                    .retrieve()
                    .body(new ParameterizedTypeReference<List<AuthResponseDTO.getUserProfile>>() {
                    });

        } catch (RestClientException e) {
            logger.error("REST 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new UserException.UserNotFound();
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new UserException.UserNotFound();
        }
    }

    public String getFirstStatus(Long clubId, Long recruitmentId) {

        String uri = UriComponentsBuilder.fromHttpUrl(clubPublicUrl)
                .path("/inner-service/{clubId}/recruitments/{recruitmentId}")
                .buildAndExpand(clubId, recruitmentId)
                .toUriString();
        try {
            ResponseEntity<ClubResponseDTO.RecruitmentResponseDto> responseEntity = restClient.get()
                    .uri(uri)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .toEntity(ClubResponseDTO.RecruitmentResponseDto.class);

            ClubResponseDTO.RecruitmentResponseDto responseBody = responseEntity.getBody();
            if (responseBody != null && responseBody.getProcessData() != null) {
                return (String) responseBody.getProcessData().get("process1");
            }

            throw new ClubException.ClubCommunicateFailedException();

        } catch (RestClientException e) {
            logger.error("REST 클라이언트 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        } catch (Exception e) {
            logger.error("예상치 못한 오류 발생: {}", e.getMessage(), e);
            throw new ClubException.ClubAdminCommunicateFailedException();
        }
    }
}
