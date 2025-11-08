
package beworkify.service;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.MessageResponse;
import java.util.List;

public interface MessageService {

	/**
	 * Gửi tin nhắn mới
	 *
	 * @param request
	 *            Request chứa thông tin tin nhắn
	 * @return MessageResponse
	 */
	MessageResponse sendMessage(SendMessageRequest request);

	/**
	 * Lấy danh sách tin nhắn theo conversation ID
	 *
	 * @param conversationId
	 *            ID của conversation
	 * @return Danh sách MessageResponse
	 */
	List<MessageResponse> getMessagesByConversationId(Long conversationId);

	/**
	 * Đánh dấu tin nhắn đã xem
	 *
	 * @param conversationId
	 *            ID của conversation
	 */
	void markMessagesAsSeen(Long conversationId);
}
