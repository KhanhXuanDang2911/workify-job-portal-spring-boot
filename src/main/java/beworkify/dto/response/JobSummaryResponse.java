package beworkify.dto.response;

import beworkify.enumeration.*;
import lombok.*;

/**
 * DTO for job summary response. Contains essential job details for list views or summaries.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSummaryResponse {
  private Long id;
  private String jobTitle;
  private JobStatus status;
  private EmployerSummaryResponse employer;
}
