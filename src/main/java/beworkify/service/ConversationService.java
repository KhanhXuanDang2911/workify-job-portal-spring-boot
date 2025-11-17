
package beworkify.service;

import beworkify.dto.response.ConversationResponse;
import beworkify.entity.Conversation;
import java.util.List;

public interface ConversationService {

	/**
	 * Tạo mới hoặc lấy conversation hiện có
	 *
	 * @param jobId
	 *            ID của công việc
	 * @param applicationId
	 *            ID của đơn ứng tuyển
	 * @param employerId
	 *            ID của nhà tuyển dụng
	 * @return Conversation entity
	 */
	Conversation createOrGetConversation(Long jobId, Long applicationId, Long employerId);

	/**
	 * Lấy conversation theo ID
	 *
	 * @param conversationId
	 *            ID của conversation
	 * @return Conversation entity
	 */
	Conversation getConversationById(Long conversationId);

	/**
	 * Lấy danh sách conversations của user hiện tại
	 *
	 * @return Danh sách ConversationResponse
	 */
	List<ConversationResponse> getConversationsForCurrentUser();

	/**
	 * Cập nhật tin nhắn cuối cùng của conversation
	 *
	 * @param conversationId
	 *            ID của conversation
	 * @param content
	 *            Nội dung tin nhắn
	 * @param senderId
	 *            ID người gửi
	 * @param senderType
	 *            Loại người gửi (USER hoặc EMPLOYER)
	 */
	void updateLastMessage(Long conversationId, String content, Long senderId, String senderType);

	/**
	 * Đánh dấu conversation đã có tin nhắn từ employer
	 *
	 * @param conversationId
	 *            ID của conversation
	 */
	void markHasEmployerMessage(Long conversationId);

	/**
	 * Kiểm tra user có thuộc conversation không
	 *
	 * @param conversationId
	 *            ID của conversation
	 * @param userId
	 *            ID của user
	 * @param userType
	 *            Loại user (USER hoặc EMPLOYER)
	 * @return true nếu user thuộc conversation
	 */
	boolean isUserInConversation(Long conversationId, Long userId, String userType);

	/**
	 * Get conversation by application ID. Allows both job seeker and employer to
	 * retrieve the conversation associated with a specific job application.
	 *
	 * @param applicationId
	 *            The ID of the application
	 * @return ConversationResponse containing conversation details
	 */
	ConversationResponse getConversationByApplicationId(Long applicationId);
}
