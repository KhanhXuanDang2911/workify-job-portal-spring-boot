package beworkify.repository;

import beworkify.entity.Conversation;
import jakarta.persistence.LockModeType;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Lock;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Conversation entities. Provides methods for CRUD operations and
 * custom queries related to conversations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface ConversationRepository extends JpaRepository<Conversation, Long> {

  @Query(
      "SELECT c FROM Conversation c "
          + "WHERE c.job.id = :jobId AND c.application.id = :applicationId")
  Optional<Conversation> findByJobIdAndApplicationId(
      @Param("jobId") Long jobId, @Param("applicationId") Long applicationId);

  @Query(
      "SELECT c FROM Conversation c "
          + "WHERE c.jobSeeker.id = :userId "
          + "ORDER BY c.updatedAt DESC")
  List<Conversation> findByJobSeekerId(@Param("userId") Long userId);

  @Query(
      "SELECT c FROM Conversation c "
          + "WHERE c.employer.id = :employerId "
          + "ORDER BY c.updatedAt DESC")
  List<Conversation> findByEmployerId(@Param("employerId") Long employerId);

  @Lock(LockModeType.PESSIMISTIC_WRITE)
  @Query("SELECT c FROM Conversation c WHERE c.id = :id")
  Optional<Conversation> findByIdForUpdate(@Param("id") Long id);

  @Query(
      "SELECT COALESCE(COUNT(c), 0) "
          + "FROM Conversation c "
          + "WHERE c.jobSeeker.id = :userId "
          + "  AND c.unreadCountJobSeeker > 0")
  Long countConversationsWithUnreadForJobSeeker(@Param("userId") Long userId);

  @Query(
      "SELECT COALESCE(COUNT(c), 0) "
          + "FROM Conversation c "
          + "WHERE c.employer.id = :employerId "
          + "  AND c.unreadCountEmployer > 0")
  Long countConversationsWithUnreadForEmployer(@Param("employerId") Long employerId);
}
