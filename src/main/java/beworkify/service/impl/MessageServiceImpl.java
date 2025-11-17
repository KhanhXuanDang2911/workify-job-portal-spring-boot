
package beworkify.service.impl;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.MessageResponse;
import beworkify.entity.*;
import beworkify.enumeration.ErrorCode;
import beworkify.exception.AppException;
import beworkify.repository.MessageRepository;
import beworkify.service.ConversationService;
import beworkify.service.MessageService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

	private final MessageRepository messageRepository;
	private final ConversationService conversationService;
	private final SimpMessagingTemplate messagingTemplate;

	@Override
	@Transactional
	public MessageResponse sendMessage(SendMessageRequest request) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Long senderId;
		String senderType;
		String senderName;
		String senderAvatar;

		if (principal instanceof User) {
			User user = (User) principal;
			senderId = user.getId();
			senderType = "USER";
			senderName = user.getFullName();
			senderAvatar = user.getAvatarUrl();
		} else if (principal instanceof Employer) {
			Employer employer = (Employer) principal;
			senderId = employer.getId();
			senderType = "EMPLOYER";
			senderName = employer.getCompanyName();
			senderAvatar = employer.getAvatarUrl();
		} else {
			throw new AppException(ErrorCode.BAD_REQUEST);
		}

		Conversation conversation = conversationService.getConversationById(request.getConversationId());

		if (!conversationService.isUserInConversation(conversation.getId(), senderId, senderType)) {
			throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
		}

		// USER can only send message after EMPLOYER has sent the first message
		if ("USER".equals(senderType) && !conversation.getHasEmployerMessage()) {
			throw new AppException(ErrorCode.APPLICANT_MUST_WAIT_RECRUITER);
		}

		Message message = Message.builder().conversation(conversation).senderId(senderId).senderType(senderType)
				.content(request.getContent()).seen(false).build();

		message = messageRepository.save(message);
		conversationService.updateLastMessage(conversation.getId(), request.getContent(), senderId, senderType);

		// Mark conversation to allow USER to send messages after employer initiates
		if ("EMPLOYER".equals(senderType)) {
			conversationService.markHasEmployerMessage(conversation.getId());
		}

		MessageResponse messageResponse = MessageResponse.builder().id(message.getId())
				.conversationId(conversation.getId()).senderId(senderId).senderType(senderType).senderName(senderName)
				.senderAvatar(senderAvatar).content(message.getContent()).seen(message.getSeen())
				.createdAt(message.getCreatedAt()).build();

		// Determine receiver and sender principal (format: "USER:email" or
		// "EMPLOYER:email")
		String receiverPrincipal;
		String senderPrincipal;

		if ("USER".equals(senderType)) {
			senderPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
			receiverPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();
		} else {
			senderPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();
			receiverPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
		}

		// Broadcast message via WebSocket to both receiver and sender (for multi-device
		// sync)
		messagingTemplate.convertAndSendToUser(receiverPrincipal, "/queue/messages", messageResponse);
		messagingTemplate.convertAndSendToUser(senderPrincipal, "/queue/messages", messageResponse);

		log.info("Message sent from {} (type: {}) in conversation {} - Broadcasted to {} and {}", senderId, senderType,
				conversation.getId(), receiverPrincipal, senderPrincipal);

		return messageResponse;
	}

	@Override
	@Transactional(readOnly = true)
	public List<MessageResponse> getMessagesByConversationId(Long conversationId) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Long userId;
		String userType;

		if (principal instanceof User) {
			userId = ((User) principal).getId();
			userType = "USER";
		} else if (principal instanceof Employer) {
			userId = ((Employer) principal).getId();
			userType = "EMPLOYER";
		} else {
			throw new AppException(ErrorCode.BAD_REQUEST);
		}

		if (!conversationService.isUserInConversation(conversationId, userId, userType)) {
			throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
		}

		List<Message> messages = messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

		Conversation conversation = conversationService.getConversationById(conversationId);

		return messages.stream().map(msg -> mapToResponse(msg, conversation)).collect(Collectors.toList());
	}

	@Override
	@Transactional
	public void markMessagesAsSeen(Long conversationId) {
		Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

		Long userId;
		String userType;

		if (principal instanceof User) {
			userId = ((User) principal).getId();
			userType = "USER";
			messageRepository.markAsSeenForJobSeeker(conversationId, userId);
		} else if (principal instanceof Employer) {
			userId = ((Employer) principal).getId();
			userType = "EMPLOYER";
			messageRepository.markAsSeenForEmployer(conversationId, userId);
		} else {
			throw new AppException(ErrorCode.BAD_REQUEST);
		}

		if (!conversationService.isUserInConversation(conversationId, userId, userType)) {
			throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
		}

		log.info("Marked messages as seen in conversation {} for user {} (type: {})", conversationId, userId, userType);
	}

	private MessageResponse mapToResponse(Message message, Conversation conversation) {
		String senderName;
		String senderAvatar;

		if ("USER".equals(message.getSenderType())) {
			senderName = conversation.getJobSeeker().getFullName();
			senderAvatar = conversation.getJobSeeker().getAvatarUrl();
		} else {
			senderName = conversation.getEmployer().getCompanyName();
			senderAvatar = conversation.getEmployer().getAvatarUrl();
		}

		return MessageResponse.builder().id(message.getId()).conversationId(message.getConversation().getId())
				.senderId(message.getSenderId()).senderType(message.getSenderType()).senderName(senderName)
				.senderAvatar(senderAvatar).content(message.getContent()).seen(message.getSeen())
				.createdAt(message.getCreatedAt()).build();
	}
}
