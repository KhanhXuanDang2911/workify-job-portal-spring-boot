package beworkify.dto.response;

import lombok.*;

@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class PopularIndustryResponse {
    private Long id;
    private String name;
    private String engName;
    private String description;
    private Long jobCount;
}
