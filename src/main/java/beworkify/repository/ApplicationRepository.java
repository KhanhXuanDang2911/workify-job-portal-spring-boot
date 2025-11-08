
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

	@EntityGraph(attributePaths = {"job", "job.author"})
	@Query("""
			SELECT a FROM Application a
			WHERE a.job.id = :jobId
			                    AND (COALESCE(:thresholdDateTime, a.createdAt) <= a.createdAt)
			                    AND (:status IS NULL OR a.status = :status)
			""")
	Page<Application> findByJobId(@Param("jobId") Long jobId, @Param("status") ApplicationStatus status,
			@Param("thresholdDateTime") LocalDateTime thresholdDateTime, Pageable pageable);

	@Query("""
			SELECT a.job.id AS jobId, COUNT(a) AS cnt
			FROM Application a
			WHERE a.job.id IN :jobIds
			GROUP BY a.job.id
			""")
	List<Object[]> countByJobIds(@Param("jobIds") List<Long> jobIds);
}
