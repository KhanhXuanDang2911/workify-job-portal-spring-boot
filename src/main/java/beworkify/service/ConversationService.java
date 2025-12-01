package beworkify.service;

import beworkify.dto.response.ConversationResponse;
import beworkify.entity.Conversation;
import java.util.List;

/**
 * Service interface for managing conversations. Handles business logic for chat conversations
 * between job seekers and employers.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface ConversationService {

  Conversation createOrGetConversation(Long jobId, Long applicationId, Long employerId);

  Conversation getConversationById(Long conversationId);

  List<ConversationResponse> getConversationsForCurrentUser();

  void updateLastMessage(Long conversationId, String content, Long senderId, String senderType);

  void markHasEmployerMessage(Long conversationId);

  boolean isUserInConversation(Long conversationId, Long userId, String userType);

  ConversationResponse getConversationByApplicationId(Long applicationId);

  Integer countConversationsWithUnreadForCurrentUser();
}
