package beworkify.dto.response;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

/**
 * DTO for employer response. Contains comprehensive employer details including contact info and
 * social links.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@SuperBuilder
public class EmployerResponse extends BaseResponse {
  private String email;
  private String phoneNumber;
  private String companyName;
  private LevelCompanySize companySize;
  private String contactPerson;
  private String avatarUrl;
  private String backgroundUrl;
  private String employerSlug;
  private String aboutCompany;
  private List<String> websiteUrls;
  private String facebookUrl;
  private String twitterUrl;
  private String linkedinUrl;
  private String googleUrl;
  private String youtubeUrl;
  private StatusUser status;
  private ProvinceResponse province;
  private DistrictResponse district;
  private String detailAddress;
  private Integer numberOfHiringJobs;
}
