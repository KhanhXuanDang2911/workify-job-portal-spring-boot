package beworkify.service;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.MessageResponse;
import java.util.List;

public interface MessageService {

  MessageResponse sendMessage(SendMessageRequest request);

  List<MessageResponse> getMessagesByConversationId(Long conversationId);

  void markMessagesAsSeen(Long conversationId);
}
