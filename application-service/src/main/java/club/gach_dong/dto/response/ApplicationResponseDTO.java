package club.gach_dong.dto.response;

import lombok.Builder;
import lombok.Getter;

import java.util.List;

public class ApplicationResponseDTO {
    @Getter
    @Builder
    public static class ToGetFormInfoAdminDTO {
        private Long formId;
        private String formName;
        private String formBody;
        private String formStatus;
        private String formSettings;
    }

    @Getter
    @Builder
    public static class ToGetFormInfoUserDTO {
        private Long formId;
        private String formName;
        private String formBody;
    }

    @Getter
    @Builder
    public static class ToGetApplicationHistoryListDTO {
        private List<ToGetApplicationHistoryDTO> toGetApplicationHistoryDTO;
    }

    @Getter
    @Builder
    public static class ToGetApplicationHistoryDTO {
        private Long applicationId;
        private String clubName;
        private String status;
    }

    @Getter
    @Builder
    public static class ToCreateApplicationFormDTO {
        private Long applicationFormId;
    }

    @Getter
    @Builder
    public static class ToCreateApplicationDTO {
        private Long applyId;
    }

}
