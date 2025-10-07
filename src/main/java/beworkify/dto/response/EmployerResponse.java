package beworkify.dto.response;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
import lombok.experimental.SuperBuilder;

import java.util.List;

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
}
