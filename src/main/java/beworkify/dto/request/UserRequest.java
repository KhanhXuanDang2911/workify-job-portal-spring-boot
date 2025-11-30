package beworkify.dto.request;

import beworkify.enumeration.Gender;
import beworkify.enumeration.StatusUser;
import beworkify.enumeration.UserRole;
import beworkify.validation.annotation.ValueOfEnum;
import beworkify.validation.group.OnAdmin;
import beworkify.validation.group.OnCreate;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.constraints.*;
import java.time.LocalDate;
import lombok.*;
import org.springframework.format.annotation.DateTimeFormat;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class UserRequest {
  @NotBlank(message = "{validation.fullname.not.blank}")
  @Size(min = 3, max = 160, message = "{validation.fullname.size}")
  private String fullName;

  @NotBlank(
      message = "{validation.email.not.blank}",
      groups = {OnCreate.class})
  @Pattern(
      regexp =
          "^[a-zA-Z0-9](?:[a-zA-Z0-9._%+-]{0,63}[a-zA-Z0-9])?@[a-zA-Z0-9](?:[a-zA-Z0-9.-]{0,253}[a-zA-Z0-9])?\\.[a-zA-Z]{2,}$",
      message = "{validation.email.invalid}",
      groups = {OnCreate.class})
  private String email;

  @NotBlank(
      message = "{validation.password.not.blank}",
      groups = {OnCreate.class})
  @Pattern(
      regexp = "^(?=.*[A-Z])(?=.*[a-z])(?=.*[^A-Za-z0-9]).{8,160}$",
      message = "{validation.password.invalid}",
      groups = {OnCreate.class})
  private String password;

  @Pattern(regexp = "^(?:\\+84|0)[35789][0-9]{8}$", message = "{validation.phone.invalid}")
  private String phoneNumber;

  @DateTimeFormat(iso = DateTimeFormat.ISO.DATE)
  @JsonFormat(pattern = "dd/MM/yyyy")
  @PastOrPresent(message = "{validation.birthdate.past.present}")
  private LocalDate birthDate;

  @ValueOfEnum(enumClass = Gender.class, message = "{validation.gender.invalid}", required = false)
  private String gender;

  @Min(value = 1, message = "{validation.province.invalid}")
  private Long provinceId;

  @Min(value = 1, message = "{validation.district.invalid}")
  private Long districtId;

  @Min(value = 1, message = "{validation.id.min}")
  private Long industryId;

  private String detailAddress;

  @NotNull(
      message = "{validation.status.user.not.null}",
      groups = {OnAdmin.class})
  @ValueOfEnum(
      enumClass = StatusUser.class,
      message = "{validation.status.user.invalid}",
      groups = {OnAdmin.class})
  private String status;

  @NotNull(
      message = "{validation.role.not.null}",
      groups = {OnAdmin.class})
  @ValueOfEnum(
      enumClass = UserRole.class,
      message = "{validation.role.invalid}",
      groups = {OnAdmin.class})
  private String role;
}
