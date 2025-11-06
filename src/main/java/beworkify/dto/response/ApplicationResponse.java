package beworkify.dto.response;

import beworkify.enumeration.ApplicationStatus;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApplicationResponse extends BaseResponse {
    private String fullName;
    private String email;
    private String phoneNumber;
    private String coverLetter;
    private String cvUrl;
    private Integer applyCount;
    private ApplicationStatus status;
    private JobSummaryResponse job;
}
