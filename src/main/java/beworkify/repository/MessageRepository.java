
package beworkify.repository;

import beworkify.entity.Message;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface MessageRepository extends JpaRepository<Message, Long> {

	@Query("SELECT m FROM Message m " + "WHERE m.conversation.id = :conversationId " + "ORDER BY m.createdAt ASC")
	List<Message> findByConversationIdOrderByCreatedAtAsc(@Param("conversationId") Long conversationId);

	// Đánh dấu tin nhắn đã xem cho User (receiver là User)
	@Modifying
	@Query("UPDATE Message m SET m.seen = true " + "WHERE m.conversation.id = :conversationId "
			+ "AND m.senderId != :userId " + "AND m.senderType != 'USER' " + "AND m.seen = false")
	void markAsSeenForJobSeeker(@Param("conversationId") Long conversationId, @Param("userId") Long userId);

	// Đánh dấu tin nhắn đã xem cho Employer (receiver là Employer)
	@Modifying
	@Query("UPDATE Message m SET m.seen = true " + "WHERE m.conversation.id = :conversationId "
			+ "AND m.senderId != :employerId " + "AND m.senderType != 'EMPLOYER' " + "AND m.seen = false")
	void markAsSeenForEmployer(@Param("conversationId") Long conversationId, @Param("employerId") Long employerId);
}
