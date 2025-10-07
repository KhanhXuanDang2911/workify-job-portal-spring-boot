package beworkify.dto.request;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
public class EmployerWebsiteUpdateRequest {
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String googleUrl;
    private String youtubeUrl;
    private List<String> websiteUrls;
}
