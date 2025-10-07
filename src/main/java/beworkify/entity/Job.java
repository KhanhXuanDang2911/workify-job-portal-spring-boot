package beworkify.entity;

import beworkify.enumeration.*;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "jobs")
public class Job extends BaseEntity {
    @Column(nullable = false)
    private String companyName;
    @Column(nullable = false)
    private LevelCompanySize companySize;
    private String companyWebsite;
    @Column(nullable = false)
    private String aboutCompany;
    @Column(nullable = false)
    private String jobTitle;
    @OneToMany(mappedBy = "job", cascade = CascadeType.ALL, orphanRemoval = true)
    private List<Location> jobLocations;
    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private SalaryType salaryType;
    private Double minSalary;
    private Double maxSalary;
    @Enumerated(EnumType.STRING)
    private SalaryUnit salaryUnit;
    @Column(nullable = false)
    private String jobDescription;
    @Column(nullable = false)
    private String requirement;
    @Column(nullable = false)
    private EducationLevel educationLevel;
    @Column(nullable = false)
    private ExperienceLevel experienceLevel;
    @Column(nullable = false)
    private JobLevel jobLevel;
    @Column(nullable = false)
    private JobType jobType;
    @Column(nullable = false)
    private Gender gender;
    private String jobCode;
    @OneToMany(mappedBy = "job", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<JobIndustry> jobIndustries;
    @Enumerated(EnumType.STRING)
    private AgeType ageType;
    private Long minAge;
    private Long maxAge;
    @Column(nullable = false)
    private String contactPerson;
    private String phoneNumber;
    @OneToOne
    private Location contactLocation;
    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
    private LocalDate expirationDate;
    private JobStatus status;
}
