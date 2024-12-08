package club.gach_dong.service;

import club.gach_dong.domain.Application;
import club.gach_dong.domain.ApplicationDocs;
import club.gach_dong.domain.ApplicationForm;
import club.gach_dong.domain.ApplicationFormStatus;
import club.gach_dong.dto.request.ApplicationRequestDTO;
import club.gach_dong.dto.response.ApplicationResponseDTO;
import club.gach_dong.dto.response.AuthResponseDTO;
import club.gach_dong.dto.response.AuthResponseDTO.getUserProfile;
import club.gach_dong.exception.ApplicationException;
import club.gach_dong.exception.ApplicationException.ApplicationDuplicatedException;
import club.gach_dong.exception.ApplicationException.ApplicationFormInUseException;
import club.gach_dong.exception.ApplicationException.ApplicationNotChangeableException;
import club.gach_dong.exception.ApplicationException.ApplicationUnauthorizedException;
import club.gach_dong.exception.FileException.FileTooManyException;
import club.gach_dong.exception.UserException;
import club.gach_dong.gcp.ObjectStorageService;
import club.gach_dong.gcp.ObjectStorageServiceConfig;
import club.gach_dong.repository.ApplicationDocsRepository;
import club.gach_dong.repository.ApplicationFormRepository;
import club.gach_dong.repository.ApplicationRepository;
import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Objects;
import java.util.Optional;
import java.util.UUID;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.multipart.MultipartFile;

@Service
@RequiredArgsConstructor
public class ApplicationService {

    private final ApplicationFormRepository applicationFormRepository;
    private final ApplicationRepository applicationRepository;
    private final FileService fileService;
    private final ObjectStorageService objectStorageService;
    private final ObjectStorageServiceConfig objectStorageServiceConfig;
    private final ApplicationDocsRepository applicationDocsRepository;
    private final AuthorizationService authorizationService;
    private final ServiceMeshService serviceMeshService;
    private final club.gach_dong.Application application;

    @Transactional
    public ApplicationResponseDTO.ToCreateApplicationFormDTO createApplicationForm(
            ApplicationRequestDTO.ToCreateApplicationFormDTO toCreateApplicationFormDTO, String userId) {

        //Verify Club Admin Auth with Club_Id, User_Id
        authorizationService.getAuthByUserIdAndClubId(userId, toCreateApplicationFormDTO.getClubId());

        ApplicationForm applicationForm = ApplicationForm.builder()
                .applicationFormStatus(ApplicationFormStatus.valueOf(toCreateApplicationFormDTO.getStatus()))
                .formName(toCreateApplicationFormDTO.getFormName())
                .clubId(toCreateApplicationFormDTO.getClubId())
                .body(toCreateApplicationFormDTO.getFormBody())
                .build();

        ApplicationForm createdApplicationForm = applicationFormRepository.save(applicationForm);

        return ApplicationResponseDTO.ToCreateApplicationFormDTO.builder()
                .applicationFormId(createdApplicationForm.getId())
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetFormInfoAdminDTO getFormInfoAdmin(Long formId, String userId) {

        ApplicationForm applicationForm = applicationFormRepository.findById(formId)
                .orElseThrow(ApplicationException.ApplicationFormNotFoundException::new);

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndClubId(userId, applicationForm.getClubId());

        return ApplicationResponseDTO.ToGetFormInfoAdminDTO.builder()
                .formId(applicationForm.getId())
                .formName(applicationForm.getFormName())
                .formBody(applicationForm.getBody())
                .formStatus(String.valueOf(applicationForm.getApplicationFormStatus()))
                .clubId(applicationForm.getClubId())
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetFormInfoUserDTO getFormInfoUser(Long formId, String userId) {

        ApplicationForm applicationForm = applicationFormRepository.findById(formId)
                .orElseThrow(ApplicationException.ApplicationFormNotFoundException::new);

        return ApplicationResponseDTO.ToGetFormInfoUserDTO.builder()
                .formId(applicationForm.getId())
                .formName(applicationForm.getFormName())
                .formBody(applicationForm.getBody())
                .build();
    }

    @Transactional
    public void deleteApplicationForm(Long formId, String userId) {

        ApplicationForm applicationForm = applicationFormRepository.findById(formId)
                .orElseThrow(ApplicationException.ApplicationFormNotFoundException::new);

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndClubId(userId, applicationForm.getClubId());

        if (applicationForm.getApplicationFormStatus() == ApplicationFormStatus.IN_USE) {
            throw new ApplicationFormInUseException();
        }

        applicationFormRepository.deleteById(formId);
    }


    @Transactional
    public ApplicationResponseDTO.ToCreateApplicationFormDTO changeApplicationForm(Long formId,
                                                                                   ApplicationRequestDTO.ToCreateApplicationFormDTO toCreateApplicationFormDTO,
                                                                                   String userId) {

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndClubId(userId, toCreateApplicationFormDTO.getClubId());

        ApplicationForm applicationForm = applicationFormRepository.findById(formId)
                .orElseThrow(ApplicationException.ApplicationFormNotFoundException::new);

        applicationFormRepository.deleteById(formId);

        return createApplicationForm(toCreateApplicationFormDTO, userId);
    }

    @Transactional
    public ApplicationResponseDTO.ToCreateApplicationDTO createApplication(Long recruitmentId,
                                                                           List<MultipartFile> files,
                                                                           ApplicationRequestDTO.ToApplyClubDTO toApplyClub,
                                                                           String userId) {

        //Verify it is valid apply
        authorizationService.getApplyIsValid(recruitmentId);

        //Check it is duplicated
        Optional<Application> applicationOptional = applicationRepository.findByUserIdAndRecruitmentId(userId,
                recruitmentId);
        if (applicationOptional.isPresent()) {
            Application application = applicationOptional.get();

            if (!Objects.equals(application.getApplicationStatus(), "TEMPORARY_SAVED")) {
                throw new ApplicationDuplicatedException();
            }

            deleteApplication(recruitmentId, userId);

        }

        ApplicationForm applicationForm = applicationFormRepository.findById(toApplyClub.getApplicationFormId())
                .orElseThrow(ApplicationException.ApplicationFormNotFoundException::new);

        if (files != null) {
            //업로드 할 파일 검증 (길이, 확장자 등)
            fileService.validateFiles(files);

            if (files.size() > 5) {
                throw new FileTooManyException();
            }
        }

        Application application = Application.builder()
                .userId(userId)
                .recruitmentId(recruitmentId)
                .applicationFormId(toApplyClub.getApplicationFormId())
                .applicationBody(toApplyClub.getFormBody())
                .applicationStatus(toApplyClub.getStatus())
                .clubId(toApplyClub.getClubId())
                .submitDate(LocalDateTime.now())
                .build();

        Application applicationId = applicationRepository.save(application);

        if (files != null) {
            //uploadFiles
            for (MultipartFile file : files) {
                String fileName = file.getOriginalFilename();
                String uuid = UUID.randomUUID().toString();
                String fileUrl = objectStorageService.uploadObject(objectStorageServiceConfig.getApplicationDocsDir(),
                        uuid, file);

                ApplicationDocs applicationDocs = ApplicationDocs.builder()
                        .applicationId(applicationId.getId())
                        .fileName(fileName)
                        .fileUrl(fileUrl)
                        .build();
                applicationDocsRepository.save(applicationDocs);
            }
        }

        return ApplicationResponseDTO.ToCreateApplicationDTO.builder()
                .applicationId(applicationId.getId())
                .build();
    }

    @Transactional
    public void deleteApplication(Long recruitmentId, String userId) {

        Application application = applicationRepository.findByUserIdAndRecruitmentId(userId, recruitmentId)
                .orElseThrow(ApplicationException.ApplicationNotFoundException::new);

        //If application is not owner of user
        if (!userId.equals(application.getUserId())) {
            throw new ApplicationUnauthorizedException();
        }

        //If application couldn't be deleted.
        if (!Objects.equals(application.getApplicationStatus(), "TEMPORARY_SAVED")) {
            throw new ApplicationNotChangeableException();
        }

        List<ApplicationDocs> applicationDocsList = applicationDocsRepository.findAllByApplicationId(recruitmentId);

        if (!applicationDocsList.isEmpty()) {
            for (ApplicationDocs applicationDocs : applicationDocsList) {
                objectStorageService.deleteObject(applicationDocs.getFileUrl());
                applicationDocsRepository.deleteByApplicationId(applicationDocs.getApplicationId());
            }
        }

        applicationRepository.delete(application);
    }

    @Transactional
    public ApplicationResponseDTO.ToCreateApplicationDTO changeApplication(Long recruitmentId,
                                                                           List<MultipartFile> files,
                                                                           ApplicationRequestDTO.ToApplyClubDTO toApplyClub,
                                                                           String userId) {
        deleteApplication(recruitmentId, userId);
        return createApplication(recruitmentId, files, toApplyClub, userId);
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetApplicationHistoryListDTO getApplicationHistoryList(String userId) {

        List<Application> applicationList = applicationRepository.findAllByUserId(userId);

        List<ApplicationResponseDTO.ToGetApplicationHistoryDTO> applicationHistoryDTOs = applicationList.stream()
                .map(application -> ApplicationResponseDTO.ToGetApplicationHistoryDTO.builder()
                        .applicationId(application.getId())
                        .clubId(application.getClubId())
                        .status(application.getApplicationStatus())
                        .submitDate(application.getSubmitDate())
                        .build())
                .collect(Collectors.toList());

        return ApplicationResponseDTO.ToGetApplicationHistoryListDTO.builder()
                .toGetApplicationHistoryDTO(applicationHistoryDTOs)
                .build();

    }

    @Transactional
    public void changeApplicationStatus(String userId,
                                        ApplicationRequestDTO.ToChangeApplicationStatus toChangeApplicationStatus) {

        Application application = applicationRepository.findById(toChangeApplicationStatus.getApplicationId())
                .orElseThrow(ApplicationException.ApplicationNotFoundException::new);

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndApplyId(userId, application.getRecruitmentId());

        applicationRepository.updateApplicationStatus(toChangeApplicationStatus.getStatus(), application);
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetApplicationListAdminDTO getApplicationListAdmin(String userId,
                                                                                       Long recruitmentId) {

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndApplyId(userId, recruitmentId);

        List<Application> applicationList = applicationRepository.findAllByRecruitmentIdAndApplicationStatus(
                recruitmentId);

        if (applicationList.isEmpty()) {
            return ApplicationResponseDTO.ToGetApplicationListAdminDTO.builder()
                    .toGetApplicationDTO(null)
                    .build();
        }

        List<String> userIds = applicationList.stream()
                .map(Application::getUserId)
                .collect(Collectors.toList());

        List<AuthResponseDTO.getUserProfile> userProfiles = serviceMeshService.getUserProfiles(userIds);

        Map<String, getUserProfile> userProfileMap = userProfiles.stream()
                .collect(Collectors.toMap(getUserProfile::getUserReferenceId, profile -> profile));

        List<ApplicationResponseDTO.ToGetApplicationDTO> toGetApplicationDTOs = applicationList.stream()
                .map(application -> {
                    AuthResponseDTO.getUserProfile userProfile = userProfileMap.get(application.getUserId());
                    return ApplicationResponseDTO.ToGetApplicationDTO.builder()
                            .applicationId(application.getId())
                            .userReferenceId(application.getUserId())
                            .userName(userProfile != null ? userProfile.getName() : null)
                            .userEmail(userProfile != null ? userProfile.getEmail() : null)
                            .userProfileUrl(userProfile != null ? userProfile.getProfileImageUrl() : null)
                            .status(application.getApplicationStatus())
                            .submitDate(application.getSubmitDate())
                            .applicationBody(application.getApplicationBody())
                            .build();
                })
                .collect(Collectors.toList());

        return ApplicationResponseDTO.ToGetApplicationListAdminDTO.builder()
                .toGetApplicationDTO(toGetApplicationDTOs)
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetApplicationTempDTO getApplicationTemp(Long recruitmentId, String userId) {

        Application application = applicationRepository.findByRecruitmentIdAndApplicationStatusAndUserId(recruitmentId,
                "TEMPORARY_SAVED", userId).orElse(null);

        if (application == null) {
            return null;
        }

        return ApplicationResponseDTO.ToGetApplicationTempDTO.builder()
                .applicationId(application.getId())
                .applicationBody(application.getApplicationBody())
                .build();
    }

    @Transactional(readOnly = true)
    public ApplicationResponseDTO.ToGetApplicationDTO getApplicationAdmin(String userId, Long applicationId) {

        Application application = applicationRepository.findById(applicationId)
                .orElseThrow(ApplicationException.ApplicationNotFoundException::new);

        //Verify Club Admin Auth with Apply_Id, User_Id
        authorizationService.getAuthByUserIdAndApplyId(userId, application.getRecruitmentId());

        List<AuthResponseDTO.getUserProfile> userProfiles = serviceMeshService.getUserProfiles(
                Collections.singletonList(userId));

        AuthResponseDTO.getUserProfile userProfile = userProfiles.stream()
                .findFirst()
                .orElseThrow(UserException.UserNotFound::new);

        return ApplicationResponseDTO.ToGetApplicationDTO.builder()
                .applicationId(application.getId())
                .userReferenceId(userId)
                .userProfileUrl(userProfile.getProfileImageUrl())
                .userEmail(userProfile.getEmail())
                .userName(userProfile.getName())
                .applicationBody(application.getApplicationBody())
                .status(application.getApplicationStatus())
                .submitDate(application.getSubmitDate())
                .build();
    }

    @Transactional(readOnly = true)
    public List<ApplicationResponseDTO.ToGetFormInfoAdminDTO> toGetFormInfoListAdmin(String userId, Long clubId) {
        List<ApplicationForm> applicationFormList = applicationFormRepository.findAllByClubId(clubId);

        if (applicationFormList.isEmpty()) {
            return Collections.emptyList();
        }

        ApplicationForm firstForm = applicationFormList.get(0);
        authorizationService.getAuthByUserIdAndClubId(userId, firstForm.getClubId());

        return applicationFormList.stream()
                .map(applicationForm -> ApplicationResponseDTO.ToGetFormInfoAdminDTO.builder()
                        .formId(applicationForm.getId())
                        .formName(applicationForm.getFormName())
                        .formBody(applicationForm.getBody())
                        .formStatus(String.valueOf(applicationForm.getApplicationFormStatus()))
                        .clubId(applicationForm.getClubId())
                        .build())
                .collect(Collectors.toList());
    }

}
