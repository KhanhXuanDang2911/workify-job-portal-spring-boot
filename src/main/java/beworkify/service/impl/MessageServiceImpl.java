package beworkify.service.impl;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.MessageResponse;
import beworkify.entity.*;
import beworkify.enumeration.ErrorCode;
import beworkify.exception.AppException;
import beworkify.repository.ConversationRepository;
import beworkify.repository.MessageRepository;
import beworkify.service.ConversationService;
import beworkify.service.MessageService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.messaging.simp.SimpMessagingTemplate;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class MessageServiceImpl implements MessageService {

  private final MessageRepository messageRepository;
  private final ConversationService conversationService;
  private final ConversationRepository conversationRepository;
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

    Conversation conversation =
        conversationRepository
            .findByIdForUpdate(request.getConversationId())
            .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

    if (!conversationService.isUserInConversation(conversation.getId(), senderId, senderType)) {
      throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
    }

    if ("USER".equals(senderType) && !conversation.getHasEmployerMessage()) {
      throw new AppException(ErrorCode.APPLICANT_MUST_WAIT_RECRUITER);
    }

    Message message =
        Message.builder()
            .conversation(conversation)
            .senderId(senderId)
            .senderType(senderType)
            .content(request.getContent())
            .seen(false)
            .build();

    message = messageRepository.save(message);
    conversation.setLastMessage(request.getContent());
    conversation.setLastMessageSenderId(senderId);
    conversation.setLastMessageSenderType(senderType);

    if ("EMPLOYER".equals(senderType)) {
      if (!conversation.getHasEmployerMessage()) {
        conversation.setHasEmployerMessage(true);
      }
      conversation.setUnreadCountJobSeeker(conversation.getUnreadCountJobSeeker() + 1);
    } else {
      conversation.setUnreadCountEmployer(conversation.getUnreadCountEmployer() + 1);
    }

    conversation = conversationRepository.save(conversation);

    MessageResponse messageResponse =
        MessageResponse.builder()
            .id(message.getId())
            .conversationId(conversation.getId())
            .senderId(senderId)
            .senderType(senderType)
            .senderName(senderName)
            .senderAvatar(senderAvatar)
            .content(message.getContent())
            .seen(message.getSeen())
            .createdAt(message.getCreatedAt())
            .build();

    String receiverPrincipal;
    String senderPrincipal;

    if ("USER".equals(senderType)) {
      senderPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
      receiverPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();
    } else {
      senderPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();
      receiverPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
    }

    Integer unreadForRecipient;
    Integer totalUnreadConversations;

    if ("USER".equals(senderType)) {
      unreadForRecipient = conversation.getUnreadCountEmployer();
      Long cnt =
          conversationRepository.countConversationsWithUnreadForEmployer(
              conversation.getEmployer().getId());
      totalUnreadConversations = cnt == null ? 0 : cnt.intValue();
    } else {
      unreadForRecipient = conversation.getUnreadCountJobSeeker();
      Long cnt =
          conversationRepository.countConversationsWithUnreadForJobSeeker(
              conversation.getJobSeeker().getId());
      totalUnreadConversations = cnt == null ? 0 : cnt.intValue();
    }

    java.util.Map<String, Object> payload = new java.util.HashMap<>();
    payload.put("type", "MESSAGE");
    payload.put("message", messageResponse);
    java.util.Map<String, Object> unread = new java.util.HashMap<>();
    unread.put("conversationId", conversation.getId());
    unread.put("unreadForRecipient", unreadForRecipient);
    unread.put("totalUnreadConversations", totalUnreadConversations);
    payload.put("unread", unread);

    messagingTemplate.convertAndSendToUser(receiverPrincipal, "/queue/messages", payload);
    messagingTemplate.convertAndSendToUser(senderPrincipal, "/queue/messages", payload);

    log.info(
        "Message sent from {} (type: {}) in conversation {} - Broadcasted to {} and {}",
        senderId,
        senderType,
        conversation.getId(),
        receiverPrincipal,
        senderPrincipal);

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

    List<Message> messages =
        messageRepository.findByConversationIdOrderByCreatedAtAsc(conversationId);

    Conversation conversation = conversationService.getConversationById(conversationId);

    return messages.stream()
        .map(msg -> mapToResponse(msg, conversation))
        .collect(Collectors.toList());
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

      Conversation conversation =
          conversationRepository
              .findByIdForUpdate(conversationId)
              .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

      if (!conversationService.isUserInConversation(conversationId, userId, userType)) {
        throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
      }

      int updated = messageRepository.markAsSeenForJobSeeker(conversationId);
      if (updated > 0) {
        int newCount = Math.max(0, conversation.getUnreadCountJobSeeker() - updated);
        conversation.setUnreadCountJobSeeker(newCount);
        conversation = conversationRepository.save(conversation);
      }

      // compute totals (number of conversations that have unread messages)
      Long cntJS =
          conversationRepository.countConversationsWithUnreadForJobSeeker(
              conversation.getJobSeeker().getId());
      Long cntEmp =
          conversationRepository.countConversationsWithUnreadForEmployer(
              conversation.getEmployer().getId());
      Integer totalJobSeeker = cntJS == null ? 0 : cntJS.intValue();
      Integer totalEmployer = cntEmp == null ? 0 : cntEmp.intValue();

      java.util.Map<String, Object> payload = new java.util.HashMap<>();
      payload.put("type", "SEEN_UPDATE");
      payload.put("conversationId", conversation.getId());
      payload.put("updatedByUserId", userId);
      java.util.Map<String, Object> unread = new java.util.HashMap<>();
      unread.put("conversationId", conversation.getId());
      unread.put("unreadForJobSeeker", conversation.getUnreadCountJobSeeker());
      unread.put("unreadForEmployer", conversation.getUnreadCountEmployer());
      payload.put("unread", unread);
      java.util.Map<String, Object> total = new java.util.HashMap<>();
      total.put("jobSeeker", totalJobSeeker);
      total.put("employer", totalEmployer);
      payload.put("totalUnreadConversations", total);

      String jobSeekerPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
      String employerPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();

      messagingTemplate.convertAndSendToUser(jobSeekerPrincipal, "/queue/unread", payload);
      messagingTemplate.convertAndSendToUser(employerPrincipal, "/queue/unread", payload);

      log.info(
          "Marked messages as seen in conversation {} for user {} (type: {})",
          conversationId,
          userId,
          userType);

    } else if (principal instanceof Employer) {
      userId = ((Employer) principal).getId();
      userType = "EMPLOYER";

      Conversation conversation =
          conversationRepository
              .findByIdForUpdate(conversationId)
              .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

      if (!conversationService.isUserInConversation(conversationId, userId, userType)) {
        throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
      }

      int updated = messageRepository.markAsSeenForEmployer(conversationId);
      if (updated > 0) {
        int newCount = Math.max(0, conversation.getUnreadCountEmployer() - updated);
        conversation.setUnreadCountEmployer(newCount);
        conversation = conversationRepository.save(conversation);
      }

      // compute totals (number of conversations that have unread messages)
      Long cntJS =
          conversationRepository.countConversationsWithUnreadForJobSeeker(
              conversation.getJobSeeker().getId());
      Long cntEmp =
          conversationRepository.countConversationsWithUnreadForEmployer(
              conversation.getEmployer().getId());
      Integer totalJobSeeker = cntJS == null ? 0 : cntJS.intValue();
      Integer totalEmployer = cntEmp == null ? 0 : cntEmp.intValue();

      java.util.Map<String, Object> payload = new java.util.HashMap<>();
      payload.put("type", "SEEN_UPDATE");
      payload.put("conversationId", conversation.getId());
      payload.put("updatedByUserId", userId);
      java.util.Map<String, Object> unread = new java.util.HashMap<>();
      unread.put("conversationId", conversation.getId());
      unread.put("unreadForJobSeeker", conversation.getUnreadCountJobSeeker());
      unread.put("unreadForEmployer", conversation.getUnreadCountEmployer());
      payload.put("unread", unread);
      java.util.Map<String, Object> total = new java.util.HashMap<>();
      total.put("jobSeeker", totalJobSeeker);
      total.put("employer", totalEmployer);
      payload.put("totalUnreadConversations", total);

      String jobSeekerPrincipal = "USER:" + conversation.getJobSeeker().getEmail();
      String employerPrincipal = "EMPLOYER:" + conversation.getEmployer().getEmail();

      messagingTemplate.convertAndSendToUser(jobSeekerPrincipal, "/queue/unread", payload);
      messagingTemplate.convertAndSendToUser(employerPrincipal, "/queue/unread", payload);

      log.info(
          "Marked messages as seen in conversation {} for employer {}", conversationId, userId);

    } else {
      throw new AppException(ErrorCode.BAD_REQUEST);
    }
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

    return MessageResponse.builder()
        .id(message.getId())
        .conversationId(message.getConversation().getId())
        .senderId(message.getSenderId())
        .senderType(message.getSenderType())
        .senderName(senderName)
        .senderAvatar(senderAvatar)
        .content(message.getContent())
        .seen(message.getSeen())
        .createdAt(message.getCreatedAt())
        .build();
  }
}
