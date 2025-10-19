package beworkify.dto.response;

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
public class EmployerSummaryResponse extends BaseResponse {
    private String email;
    private String companyName;
    private String avatarUrl;
    private String backgroundUrl;
    private String employerSlug;
}
