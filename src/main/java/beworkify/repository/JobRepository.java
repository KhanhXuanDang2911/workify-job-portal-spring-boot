package beworkify.repository;

import beworkify.entity.Industry;
import beworkify.entity.Job;
import beworkify.entity.Province;
import beworkify.enumeration.JobStatus;
import beworkify.repository.custom.JobRepositoryCustom;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface JobRepository extends JpaRepository<Job, Long>, JobRepositoryCustom {

        @Query("SELECT DISTINCT j FROM Job j " +
                        "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                        "lower(j.jobTitle) LIKE %:keyword% OR " +
                        "lower(j.companyName) LIKE %:keyword% OR " +
                        "lower(j.jobDescription) LIKE %:keyword% OR " +
                        "lower(j.requirement) LIKE %:keyword%) " +
                        "AND (:provinceId IS NULL OR EXISTS (SELECT loc FROM j.jobLocations loc WHERE loc.province.id = :provinceId)) "
                        +
                        "AND (:industryId IS NULL OR EXISTS (SELECT ji2 FROM j.jobIndustries ji2 WHERE ji2.industry.id = :industryId)) "
                        +
                        "AND (:authorEmail IS NULL OR j.author.email = :authorEmail)")
        Page<Job> findMyJobs(
                        @Param("provinceId") Long provinceId,
                        @Param("industryId") Long industryId,
                        @Param("keyword") String keyword,
                        @Param("authorEmail") String authorEmail,
                        Pageable pageable);

        @Query("SELECT DISTINCT j FROM Job j " +
                        "WHERE (:keyword IS NULL OR :keyword = '' OR " +
                        "lower(j.jobTitle) LIKE %:keyword% OR " +
                        "lower(j.companyName) LIKE %:keyword% OR " +
                        "lower(j.jobDescription) LIKE %:keyword% OR " +
                        "lower(j.requirement) LIKE %:keyword%) " +
                        "AND (:provinceId IS NULL OR EXISTS (SELECT loc FROM j.jobLocations loc WHERE loc.province.id = :provinceId)) "
                        +
                        "AND (:industryId IS NULL OR EXISTS (SELECT ji2 FROM j.jobIndustries ji2 WHERE ji2.industry.id = :industryId))")
        Page<Job> findAllJobs(
                        @Param("provinceId") Long provinceId,
                        @Param("industryId") Long industryId,
                        @Param("keyword") String keyword,
                        Pageable pageable);

        @Query("select distinct jl.province from Job j join j.jobLocations jl where j.author.id = :employerId")
        List<Province> findEmployerProvinces(@Param("employerId") Long employerId);

        @Query("select distinct ji.industry from Job j join j.jobIndustries ji where j.author.id = :employerId")
        List<Industry> findEmployerIndustries(@Param("employerId") Long employerId);

        @Query("select j from Job j where j.status = beworkify.enumeration.JobStatus.APPROVED and j.author.id = :employerId")
        Page<Job> findHiringJobs(@Param("employerId") Long employerId, Pageable pageable);

}