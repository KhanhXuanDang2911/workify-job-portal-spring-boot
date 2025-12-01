package beworkify.entity;

import beworkify.dto.db.JobBenefit;
import beworkify.enumeration.*;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;

/**
 * Entity class representing a job posting. Contains comprehensive details about the job, including
 * title, description, requirements, salary, and location.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {
  @Column(nullable = false, length = 1000)
  private String companyName;

  @Column(nullable = false)
  private LevelCompanySize companySize;

  @Column(length = 1000)
  private String companyWebsite;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String aboutCompany;

  @Column(nullable = false, length = 1000)
  private String jobTitle;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private Set<Location> jobLocations = new HashSet<>();

  @Enumerated(EnumType.STRING)
  @Column(nullable = false)
  private SalaryType salaryType;

  private Double minSalary;
  private Double maxSalary;

  @Enumerated(EnumType.STRING)
  private SalaryUnit salaryUnit;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String jobDescription;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String requirement;

  @Column(columnDefinition = "jsonb")
  @JdbcTypeCode(SqlTypes.JSON)
  private List<JobBenefit> jobBenefits;

  @Column(nullable = false)
  private EducationLevel educationLevel;

  @Column(nullable = false)
  private ExperienceLevel experienceLevel;

  @Column(nullable = false)
  private JobLevel jobLevel;

  @Column(nullable = false)
  private JobType jobType;

  @Column(nullable = false)
  private JobGender gender;

  private String jobCode;

  @OneToMany(mappedBy = "job", orphanRemoval = true, cascade = CascadeType.ALL)
  @Builder.Default
  private Set<JobIndustry> jobIndustries = new HashSet<>();

  @Enumerated(EnumType.STRING)
  private AgeType ageType;

  private Long minAge;
  private Long maxAge;

  @Column(nullable = false)
  private String contactPerson;

  private String phoneNumber;

  @OneToOne(cascade = CascadeType.ALL, orphanRemoval = true)
  @JoinColumn(name = "contact_location_id", nullable = false)
  private Location contactLocation;

  @Column(columnDefinition = "TEXT")
  private String description;

  @Column(nullable = false)
  private LocalDate expirationDate;

  private JobStatus status;

  @ManyToOne
  @JoinColumn(name = "employer_id", nullable = false)
  private Employer author;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<Application> applications;

  @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SavedJob> savedJobs;
}
