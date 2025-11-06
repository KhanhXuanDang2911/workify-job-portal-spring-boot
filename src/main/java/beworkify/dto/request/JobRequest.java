package beworkify.dto.request;

import beworkify.enumeration.*;
import beworkify.validation.annotation.ValidAge;
import beworkify.validation.annotation.ValidSalary;
import beworkify.validation.annotation.ValueOfEnum;
import com.fasterxml.jackson.annotation.JsonFormat;
import jakarta.validation.Valid;
import jakarta.validation.constraints.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@ValidSalary
@ValidAge
public class JobRequest {

    @NotBlank(message = "{validation.job.companyName.notBlank}")
    @Size(max = 1000, message = "{validation.job.companyName.size}")
    private String companyName;

    @ValueOfEnum(enumClass = LevelCompanySize.class, message = "{error.invalid.level.company.size.enum}")
    private String companySize;

    @Size(max = 1000, message = "{validation.job.companyWebsite.size}")
    private String companyWebsite;

    @NotBlank(message = "{validation.job.aboutCompany.notBlank}")
    private String aboutCompany;

    @NotBlank(message = "{validation.job.jobTitle.notBlank}")
    @Size(max = 1000, message = "{validation.job.jobTitle.size}")
    private String jobTitle;

    @Valid
    @NotEmpty(message = "{validation.job.jobLocations.notEmpty}")
    private List<LocationRequest> jobLocations;

    @ValueOfEnum(enumClass = SalaryType.class, message = "{error.invalid.salary.type.enum}")
    private String salaryType;

    @DecimalMin(value = "0.0", message = "{validation.job.minSalary.min}")
    private Double minSalary;

    @DecimalMin(value = "0.0", message = "{validation.job.maxSalary.min}")
    private Double maxSalary;

    @ValueOfEnum(enumClass = SalaryUnit.class, message = "{error.invalid.salary.unit.enum}", required = false)
    private String salaryUnit;

    @NotBlank(message = "{validation.job.jobDescription.notBlank}")
    private String jobDescription;

    @NotBlank(message = "{validation.job.requirement.notBlank}")
    private String requirement;

    @Valid
    @NotEmpty(message = "{validation.job.benefits.notEmpty}")
    private List<JobBenefitRequest> jobBenefits;

    @ValueOfEnum(enumClass = EducationLevel.class, message = "{error.invalid.education.level.enum}")
    private String educationLevel;

    @ValueOfEnum(enumClass = ExperienceLevel.class, message = "{error.invalid.experience.level.enum}")
    private String experienceLevel;

    @ValueOfEnum(enumClass = JobLevel.class, message = "{error.invalid.job.level.enum}")
    private String jobLevel;

    @ValueOfEnum(enumClass = JobType.class, message = "{error.invalid.job.type.enum}")
    private String jobType;

    @ValueOfEnum(enumClass = JobGender.class, message = "{error.invalid.job.gender.enum}")
    private String gender;

    private String jobCode;

    @NotEmpty(message = "{validation.job.industryIds.notEmpty}")
    private List<@NotNull Long> industryIds;

    @ValueOfEnum(enumClass = AgeType.class, message = "{error.invalid.age.type.enum}")
    private String ageType;

    @Min(value = 15, message = "{validation.job.minAge.min}")
    @Max(value = 100, message = "{validation.job.minAge.max}")
    private Long minAge;

    @Min(value = 15, message = "{validation.job.maxAge.min}")
    @Max(value = 100, message = "{validation.job.maxAge.max}")
    private Long maxAge;

    @NotBlank(message = "{validation.job.contactPerson.notBlank}")
    private String contactPerson;

    @Pattern(regexp = "^(?:\\+84|0)[35789][0-9]{8}$", message = "{validation.phone.invalid}")
    private String phoneNumber;

    @Valid
    @NotNull(message = "{validation.job.contactLocation.notNull}")
    private LocationRequest contactLocation;

    private String description;

    @NotNull(message = "{validation.job.expirationDate.notNull}")
    @Future(message = "{validation.job.expirationDate.future}")
    @JsonFormat(pattern = "dd/MM/yyyy")
    private LocalDate expirationDate;

}