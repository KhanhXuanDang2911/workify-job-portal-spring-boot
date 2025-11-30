package beworkify.repository;

import beworkify.entity.Application;
import beworkify.entity.Job;
import beworkify.entity.User;
import beworkify.enumeration.ApplicationStatus;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ApplicationRepository extends JpaRepository<Application, Long> {
  Optional<Application> findByUserAndJob(User user, Job job);

  long countByUserIdAndJobId(Long userId, Long jobId);

  @EntityGraph(attributePaths = {"job", "job.author"})
  Optional<Application> findTopByUserIdAndJobIdOrderByCreatedAtDesc(Long userId, Long jobId);

  @EntityGraph(attributePaths = {"job", "job.author"})
  Page<Application> findAllByUser(User user, Pageable pageable);

  @Query(
      value =
          """
			SELECT a.* FROM applications a
			INNER JOIN jobs j ON j.id = a.job_id
			LEFT JOIN employers e ON e.id = j.employer_id
			WHERE a.job_id = CAST(:jobId AS bigint)
			                    AND a.created_at >= COALESCE(CAST(:thresholdDateTime AS timestamp), '1970-01-01 00:00:00'::timestamp)
			                    AND (CAST(:status AS varchar) IS NULL OR a.status = CAST(:status AS varchar))
			""",
      nativeQuery = true)
  Page<Application> findByJobId(
      @Param("jobId") Long jobId,
      @Param("status") ApplicationStatus status,
      @Param("thresholdDateTime") LocalDateTime thresholdDateTime,
      Pageable pageable);

  long countByJobId(Long jobId);

  @Query(
      """
			SELECT a.job.id AS jobId, COUNT(a) AS cnt
			FROM Application a
			WHERE a.job.id IN :jobIds
			GROUP BY a.job.id
			""")
  List<Object[]> countByJobIds(@Param("jobIds") List<Long> jobIds);
}
