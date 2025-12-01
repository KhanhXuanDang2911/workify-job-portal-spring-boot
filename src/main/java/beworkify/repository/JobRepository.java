package beworkify.repository;

import beworkify.entity.Industry;
import beworkify.entity.Job;
import beworkify.entity.Province;
import beworkify.repository.custom.JobRepositoryCustom;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Job entities. Provides methods for CRUD operations and custom
 * queries related to job postings.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JobRepositoryCustom {

  @Query(
      "SELECT j.id "
          + "FROM Job j "
          + "WHERE (:keyword IS NULL OR :keyword = '' "
          + "       OR lower(j.jobTitle) LIKE %:keyword% "
          + "       OR lower(j.companyName) LIKE %:keyword% "
          + "       OR lower(j.jobDescription) LIKE %:keyword% "
          + "       OR lower(j.requirement) LIKE %:keyword%) "
          + "  AND (:provinceId IS NULL "
          + "       OR EXISTS (SELECT 1 "
          + "                  FROM j.jobLocations loc "
          + "                  WHERE loc.province.id = :provinceId)) "
          + "  AND (:industryId IS NULL "
          + "       OR EXISTS (SELECT 1 "
          + "                  FROM j.jobIndustries ji2 "
          + "                  WHERE ji2.industry.id = :industryId)) "
          + "  AND (:authorEmail IS NULL OR j.author.email = :authorEmail)")
  Page<Long> findIdsMyJobs(
      @Param("provinceId") Long provinceId,
      @Param("industryId") Long industryId,
      @Param("keyword") String keyword,
      @Param("authorEmail") String authorEmail,
      Pageable pageable);

  @EntityGraph(
      attributePaths = {
        "author",
        "jobLocations",
        "jobLocations.province",
        "jobIndustries",
        "jobIndustries.industry"
      })
  @Query("SELECT DISTINCT j FROM Job j " + "WHERE j.id IN :ids")
  List<Job> fetchJobsByIds(@Param("ids") List<Long> ids);

  @Query(
      "SELECT j.id "
          + "FROM Job j "
          + "WHERE (:keyword IS NULL OR :keyword = '' "
          + "       OR lower(j.jobTitle) LIKE %:keyword% "
          + "       OR lower(j.companyName) LIKE %:keyword% "
          + "       OR lower(j.jobDescription) LIKE %:keyword% "
          + "       OR lower(j.requirement) LIKE %:keyword%) "
          + "  AND (:provinceId IS NULL "
          + "       OR EXISTS (SELECT 1 "
          + "                  FROM j.jobLocations loc "
          + "                  WHERE loc.province.id = :provinceId)) "
          + "  AND (:industryId IS NULL "
          + "       OR EXISTS (SELECT 1 "
          + "                  FROM j.jobIndustries ji2 "
          + "                  WHERE ji2.industry.id = :industryId))")
  Page<Long> findIdsAllJobs(
      @Param("provinceId") Long provinceId,
      @Param("industryId") Long industryId,
      @Param("keyword") String keyword,
      Pageable pageable);

  @Query(
      "SELECT DISTINCT jl.province "
          + "FROM Job j "
          + "JOIN j.jobLocations jl "
          + "WHERE j.author.id = :employerId")
  List<Province> findEmployerProvinces(@Param("employerId") Long employerId);

  @Query(
      "SELECT DISTINCT ji.industry "
          + "FROM Job j "
          + "JOIN j.jobIndustries ji "
          + "WHERE j.author.id = :employerId")
  List<Industry> findEmployerIndustries(@Param("employerId") Long employerId);

  @Query(
      "SELECT j.id "
          + "FROM Job j "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "  AND j.author.id = :employerId")
  Page<Long> findIdsHiringJobs(@Param("employerId") Long employerId, Pageable pageable);

  @Query(
      "SELECT COUNT(j) "
          + "FROM Job j "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "    AND j.author.id = :employerId")
  long countHiringJobsByEmployerId(@Param("employerId") Long employerId);

  @Query(
      "SELECT j.author.id AS employerId, COUNT(j) AS cnt "
          + "FROM Job j "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "    AND j.author.id IN :employerIds "
          + "GROUP BY j.author.id")
  List<Object[]> countHiringJobsByEmployerIds(@Param("employerIds") List<Long> employerIds);

  @Query(
      "SELECT j.author.id AS employerId, COUNT(j) AS cnt "
          + "FROM Job j "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "GROUP BY j.author.id "
          + "ORDER BY COUNT(j) DESC")
  List<Object[]> findTopEmployerIdsByHiringJobs(Pageable pageable);

  @Query(
      "SELECT j.id AS jobId, COUNT(a) AS cnt "
          + "FROM Job j "
          + "LEFT JOIN j.applications a "
          + "LEFT JOIN j.jobIndustries ji "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "  AND j.expirationDate >= CURRENT_DATE "
          + "  AND (:industryId IS NULL OR ji.industry.id = :industryId) "
          + "GROUP BY j.id "
          + "ORDER BY COUNT(a) DESC, "
          + "         j.createdAt DESC, "
          + "         CASE WHEN j.maxSalary IS NULL AND j.minSalary IS NULL THEN 1 ELSE 0 END ASC, "
          + "         COALESCE(j.maxSalary, j.minSalary) DESC")
  List<Object[]> findTopAttractiveJobIds(Long industryId, Pageable pageable);

  @Query(
      "SELECT j.id AS jobId, COUNT(a) AS cnt "
          + "FROM Job j "
          + "LEFT JOIN j.applications a "
          + "WHERE j.status = beworkify.enumeration.JobStatus.APPROVED "
          + "  AND j.expirationDate >= CURRENT_DATE "
          + "  AND EXISTS (SELECT 1 "
          + "              FROM j.jobIndustries ji "
          + "              WHERE ji.industry.id = :industryId) "
          + "GROUP BY j.id "
          + "ORDER BY j.createdAt DESC")
  List<Object[]> findPersonalizedJobIdsByIndustry(
      @Param("industryId") Long industryId, Pageable pageable);
}
