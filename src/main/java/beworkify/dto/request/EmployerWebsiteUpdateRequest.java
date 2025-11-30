package beworkify.dto.request;

import java.util.List;
import lombok.Getter;
import lombok.Setter;

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
