package beworkify.repository;

import beworkify.entity.SavedJob;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing SavedJob entities. Provides methods for CRUD operations and
 * custom queries related to saved jobs.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface SavedJobRepository extends JpaRepository<SavedJob, Long> {

  boolean existsByUser_IdAndJob_Id(Long userId, Long jobId);

  Optional<SavedJob> findByUser_IdAndJob_Id(Long userId, Long jobId);

  @Query(
      "SELECT j.id FROM SavedJob sj "
          + "JOIN sj.job j "
          + "WHERE sj.user.id = :userId "
          + "ORDER BY sj.createdAt DESC")
  Page<Long> findJobIdsByUserId(@Param("userId") Long userId, Pageable pageable);
}
