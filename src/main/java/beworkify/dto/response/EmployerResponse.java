package beworkify.dto.response;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
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
public class EmployerResponse extends BaseResponse {
    private String email;
    private String phoneNumber;
    private String companyName;
    private LevelCompanySize companySize;
    private String contactPerson;
    private ProvinceResponse province;
    private DistrictResponse district;
    private String detailAddress;
    private String avatarUrl;
    private String backgroundUrl;
    private String employerSlug;
    private String aboutCompany;
    private StatusUser status;
}
