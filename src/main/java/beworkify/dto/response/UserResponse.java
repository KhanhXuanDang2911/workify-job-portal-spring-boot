
package beworkify.dto.response;

import beworkify.enumeration.Gender;
import beworkify.enumeration.StatusUser;
import beworkify.enumeration.UserRole;
import java.time.LocalDate;
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
public class UserResponse extends BaseResponse {
	private String fullName;
	private String email;
	private String phoneNumber;
	private LocalDate birthDate;
	private Gender gender;
	private ProvinceResponse province;
	private DistrictResponse district;
	private String detailAddress;
	private String avatarUrl;
	private Boolean noPassword;
	private UserRole role;
	private StatusUser status;
}
