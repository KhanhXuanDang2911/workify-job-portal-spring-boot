package beworkify.dto.response;

import beworkify.dto.db.JobBenefit;
import beworkify.enumeration.*;
import java.time.LocalDate;
import java.util.List;
import java.util.Set;
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
public class JobResponse extends BaseResponse {
  private String companyName;
  private LevelCompanySize companySize;
  private String companyWebsite;
  private String aboutCompany;
  private String jobTitle;
  private Set<LocationResponse> jobLocations;
  private SalaryType salaryType;
  private Double minSalary;
  private Double maxSalary;
  private SalaryUnit salaryUnit;
  private String jobDescription;
  private String requirement;
  private List<JobBenefit> jobBenefits;
  private EducationLevel educationLevel;
  private ExperienceLevel experienceLevel;
  private JobLevel jobLevel;
  private JobType jobType;
  private JobGender gender;
  private String jobCode;
  private Set<IndustryResponse> industries;
  private AgeType ageType;
  private Long minAge;
  private Long maxAge;
  private String contactPerson;
  private String phoneNumber;
  private LocationResponse contactLocation;
  private String description;
  private LocalDate expirationDate;
  private JobStatus status;
  private EmployerSummaryResponse author;
  private Integer numberOfApplications;
}
