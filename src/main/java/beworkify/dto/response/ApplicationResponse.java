package beworkify.dto.response;

import beworkify.enumeration.ApplicationStatus;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO for application response. Contains applicant details, status, and associated job summary.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
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
