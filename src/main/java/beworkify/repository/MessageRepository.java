package beworkify.repository;

import beworkify.entity.Message;
import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Message entities. Provides methods for CRUD operations and
 * custom queries related to chat messages.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

  @Query(
      "SELECT m FROM Message m "
          + "WHERE m.conversation.id = :conversationId "
          + "ORDER BY m.createdAt ASC")
  List<Message> findByConversationIdOrderByCreatedAtAsc(
      @Param("conversationId") Long conversationId);

  @Modifying
  @Query(
      "UPDATE Message m "
          + "SET m.seen = true "
          + "WHERE m.conversation.id = :conversationId "
          + "  AND m.senderType = 'EMPLOYER' "
          + "  AND m.seen = false")
  int markAsSeenForJobSeeker(@Param("conversationId") Long conversationId);

  @Modifying
  @Query(
      "UPDATE Message m "
          + "SET m.seen = true "
          + "WHERE m.conversation.id = :conversationId "
          + "  AND m.senderType = 'USER' "
          + "  AND m.seen = false")
  int markAsSeenForEmployer(@Param("conversationId") Long conversationId);
}
