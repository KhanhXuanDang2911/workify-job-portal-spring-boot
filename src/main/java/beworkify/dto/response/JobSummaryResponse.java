package beworkify.dto.response;

import beworkify.enumeration.*;
import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class JobSummaryResponse {
    private Long id;
    private String companyName;
    private LevelCompanySize companySize;
    private String jobTitle;
    private JobStatus status;
}
