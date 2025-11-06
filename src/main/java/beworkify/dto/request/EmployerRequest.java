package beworkify.dto.request;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import beworkify.validation.group.OnAdmin;
import beworkify.validation.group.OnCreate;
import beworkify.validation.annotation.ValueOfEnum;
import jakarta.validation.constraints.*;
import lombok.*;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class EmployerRequest {
    @NotBlank(message = "{validation.email.not.blank}", groups = { OnCreate.class })
    @Pattern(regexp = "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$", message = "{validation.email.invalid}", groups = {
            OnCreate.class })
    private String email;

    @NotBlank(message = "{validation.password.not.blank}", groups = { OnCreate.class })
    @Pattern(regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$", message = "{validation.password.invalid}", groups = {
            OnCreate.class })
    private String password;

    @NotBlank(message = "{validation.companyName.not.blank}")
    private String companyName;

    @NotBlank(message = "{validation.companySize.not.null}")
    @ValueOfEnum(enumClass = LevelCompanySize.class, message = "{error.invalid.level.company.size.enum}")
    private String companySize;

    @NotBlank(message = "{validation.contactPerson.not.blank}")
    private String contactPerson;

    @Pattern(regexp = "^(?:\\+84|0)[35789][0-9]{8}$", message = "{validation.phone.invalid}")
    @NotBlank(message = "{validation.phoneNumber.not.blank}")
    private String phoneNumber;

    @NotNull(message = "{validation.province.not.null}")
    @Min(value = 1, message = "{validation.province.invalid}")
    private Long provinceId;

    @NotNull(message = "{validation.district.not.null}")
    @Min(value = 1, message = "{validation.district.invalid}")
    private Long districtId;

    @NotBlank(message = "{validation.detailAddress.not.blank}")
    private String detailAddress;

    private String aboutCompany;
    private String facebookUrl;
    private String twitterUrl;
    private String linkedinUrl;
    private String googleUrl;
    private String youtubeUrl;
    private List<String> websiteUrls;

    @NotNull(message = "{validation.status.user.not.null}", groups = { OnAdmin.class })
    @ValueOfEnum(enumClass = StatusUser.class, message = "{validation.status.user.invalid}", groups = { OnAdmin.class })
    private String status;

}
