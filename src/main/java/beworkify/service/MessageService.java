package beworkify.service;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.MessageResponse;
import java.util.List;

/**
 * Service interface for managing chat messages. Handles sending and retrieving messages in
 * conversations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
public interface MessageService {

  MessageResponse sendMessage(SendMessageRequest request);

  List<MessageResponse> getMessagesByConversationId(Long conversationId);

  void markMessagesAsSeen(Long conversationId);
}
