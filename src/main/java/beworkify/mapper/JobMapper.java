package beworkify.mapper;

import beworkify.dto.db.JobBenefit;
import beworkify.dto.request.JobRequest;
import beworkify.dto.response.EmployerSummaryResponse;
import beworkify.dto.response.IndustryResponse;
import beworkify.dto.response.JobResponse;
import beworkify.entity.Job;
import beworkify.enumeration.BenefitType;
import beworkify.enumeration.SalaryUnit;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import java.util.stream.Collectors;
import org.mapstruct.*;

/**
 * Mapper for converting between Job entity and DTOs. Handles complex mappings for industries,
 * locations, and benefits.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Mapper(
    componentModel = "spring",
    uses = {
      IndustryMapper.class,
      ProvinceMapper.class,
      DistrictMapper.class,
      LocationMapper.class,
      EmployerMapper.class
    },
    imports = {SalaryUnit.class},
    unmappedTargetPolicy = ReportingPolicy.IGNORE)
public interface JobMapper {

  @Mapping(target = "jobLocations", ignore = true)
  @Mapping(target = "contactLocation", ignore = true)
  @Mapping(
      target = "salaryUnit",
      expression =
          "java(request.getSalaryUnit() != null && !request.getSalaryUnit().trim().isEmpty() ? SalaryUnit.valueOf(request.getSalaryUnit()) : null)")
  Job toEntity(JobRequest request);

  @Mapping(target = "author", ignore = true)
  JobResponse toDTO(Job entity);

  List<JobResponse> toDTOs(List<Job> entities);

  @Mapping(target = "jobLocations", ignore = true)
  @Mapping(target = "contactLocation", ignore = true)
  @Mapping(
      target = "salaryUnit",
      expression =
          "java(request.getSalaryUnit() != null && !request.getSalaryUnit().trim().isEmpty() ? SalaryUnit.valueOf(request.getSalaryUnit()) : null)")
  @BeanMapping(nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
  void updateEntityFromRequest(JobRequest request, @MappingTarget Job entity);

  @AfterMapping
  default void mapIndustries(@MappingTarget JobResponse jobResponse, Job job) {
    if (job.getJobIndustries() != null && !job.getJobIndustries().isEmpty()) {
      Set<IndustryResponse> industries =
          job.getJobIndustries().stream()
              .map(jobIndustry -> jobIndustry != null ? jobIndustry.getIndustry() : null)
              .filter(Objects::nonNull)
              .map(
                  industry ->
                      IndustryResponse.builder()
                          .id(industry.getId())
                          .createdAt(industry.getCreatedAt())
                          .updatedAt(industry.getUpdatedAt())
                          .name(industry.getName())
                          .engName(industry.getEngName())
                          .description(industry.getDescription())
                          .build())
              .collect(Collectors.toSet());
      jobResponse.setIndustries(industries);
    }
  }

  @AfterMapping
  default void mapAuthor(@MappingTarget JobResponse jobResponse, Job job) {
    jobResponse.setAuthor(
        EmployerSummaryResponse.builder()
            .email(job.getAuthor().getEmail())
            .backgroundUrl(job.getAuthor().getBackgroundUrl())
            .avatarUrl(job.getAuthor().getAvatarUrl())
            .companyName(job.getAuthor().getCompanyName())
            .employerSlug(job.getAuthor().getEmployerSlug())
            .id(job.getAuthor().getId())
            .createdAt(job.getAuthor().getCreatedAt())
            .updatedAt(job.getAuthor().getUpdatedAt())
            .build());
  }

  @AfterMapping
  default void mapJobBenefits(@MappingTarget Job job, JobRequest request) {
    if (request.getJobBenefits() != null && !request.getJobBenefits().isEmpty()) {
      job.setJobBenefits(
          request.getJobBenefits().stream()
              .map(
                  benefitRequest ->
                      JobBenefit.builder()
                          .type(BenefitType.fromValue(benefitRequest.getType()))
                          .description(benefitRequest.getDescription())
                          .build())
              .collect(Collectors.toList()));
    }
  }
}
