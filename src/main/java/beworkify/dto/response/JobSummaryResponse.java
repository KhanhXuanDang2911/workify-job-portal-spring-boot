package beworkify.dto.response;

import beworkify.enumeration.*;
import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.*;

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
