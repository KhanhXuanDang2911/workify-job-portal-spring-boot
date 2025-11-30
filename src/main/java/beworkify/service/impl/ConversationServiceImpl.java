package beworkify.service.impl;

import beworkify.dto.response.ConversationResponse;
import beworkify.entity.*;
import beworkify.enumeration.ErrorCode;
import beworkify.exception.AppException;
import beworkify.exception.ResourceNotFoundException;
import beworkify.repository.*;
import beworkify.service.ConversationService;
import java.util.List;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
@Slf4j
public class ConversationServiceImpl implements ConversationService {

  private final ConversationRepository conversationRepository;
  private final ApplicationRepository applicationRepository;
  private final JobRepository jobRepository;
  private final EmployerRepository employerRepository;

  @Override
  @Transactional
  public Conversation createOrGetConversation(Long jobId, Long applicationId, Long employerId) {
    return conversationRepository
        .findByJobIdAndApplicationId(jobId, applicationId)
        .orElseGet(
            () -> {
              Application application =
                  applicationRepository
                      .findById(applicationId)
                      .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

              Job job =
                  jobRepository
                      .findById(jobId)
                      .orElseThrow(() -> new ResourceNotFoundException("Job not found"));

              if (!application.getJob().getId().equals(jobId)) {
                throw new AppException(ErrorCode.BAD_REQUEST);
              }

              Employer employer =
                  employerRepository
                      .findById(employerId)
                      .orElseThrow(() -> new ResourceNotFoundException("Employer not found"));

              Conversation conversation =
                  Conversation.builder()
                      .job(job)
                      .application(application)
                      .jobSeeker(application.getUser())
                      .employer(employer)
                      .hasEmployerMessage(false)
                      .build();

              log.info(
                  "Creating new conversation for job: {} and application: {}",
                  jobId,
                  applicationId);
              return conversationRepository.save(conversation);
            });
  }

  @Override
  @Transactional(readOnly = true)
  public Conversation getConversationById(Long conversationId) {
    return conversationRepository
        .findById(conversationId)
        .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));
  }

  @Override
  @Transactional(readOnly = true)
  public List<ConversationResponse> getConversationsForCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    List<Conversation> conversations;
    if (principal instanceof User) {
      User user = (User) principal;
      conversations = conversationRepository.findByJobSeekerId(user.getId());
    } else if (principal instanceof Employer) {
      Employer employer = (Employer) principal;
      conversations = conversationRepository.findByEmployerId(employer.getId());
    } else {
      throw new AppException(ErrorCode.BAD_REQUEST);
    }

    return conversations.stream().map(this::mapToResponse).collect(Collectors.toList());
  }

  @Override
  @Transactional
  public void updateLastMessage(
      Long conversationId, String content, Long senderId, String senderType) {
    Conversation conversation = getConversationById(conversationId);
    conversation.setLastMessage(content);
    conversation.setLastMessageSenderId(senderId);
    conversation.setLastMessageSenderType(senderType);
    conversationRepository.save(conversation);
  }

  @Override
  @Transactional
  public void markHasEmployerMessage(Long conversationId) {
    Conversation conversation = getConversationById(conversationId);
    if (!conversation.getHasEmployerMessage()) {
      conversation.setHasEmployerMessage(true);
      conversationRepository.save(conversation);
    }
  }

  @Override
  public boolean isUserInConversation(Long conversationId, Long userId, String userType) {
    Conversation conversation = getConversationById(conversationId);

    if ("USER".equals(userType)) {
      return conversation.getJobSeeker().getId().equals(userId);
    } else if ("EMPLOYER".equals(userType)) {
      return conversation.getEmployer().getId().equals(userId);
    }

    return false;
  }

  @Override
  @Transactional(readOnly = true)
  public ConversationResponse getConversationByApplicationId(Long applicationId) {
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

    Application application =
        applicationRepository
            .findById(applicationId)
            .orElseThrow(() -> new ResourceNotFoundException("Application not found"));

    if ("USER".equals(userType)) {
      if (!application.getUser().getId().equals(userId)) {
        throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
      }
    } else if ("EMPLOYER".equals(userType)) {
      if (!application.getJob().getAuthor().getId().equals(userId)) {
        throw new AppException(ErrorCode.NOT_CONVERSATION_PARTICIPANT);
      }
    }

    Conversation conversation =
        conversationRepository
            .findByJobIdAndApplicationId(application.getJob().getId(), applicationId)
            .orElseThrow(() -> new AppException(ErrorCode.CONVERSATION_NOT_FOUND));

    return mapToResponse(conversation);
  }

  @Override
  @Transactional(readOnly = true)
  public Integer countConversationsWithUnreadForCurrentUser() {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();

    if (principal instanceof User) {
      Long userId = ((User) principal).getId();
      Long cnt = conversationRepository.countConversationsWithUnreadForJobSeeker(userId);
      return cnt == null ? 0 : cnt.intValue();
    } else if (principal instanceof Employer) {
      Long employerId = ((Employer) principal).getId();
      Long cnt = conversationRepository.countConversationsWithUnreadForEmployer(employerId);
      return cnt == null ? 0 : cnt.intValue();
    } else {
      throw new AppException(ErrorCode.BAD_REQUEST);
    }
  }

  private ConversationResponse mapToResponse(Conversation conv) {
    Object principal = SecurityContextHolder.getContext().getAuthentication().getPrincipal();
    Boolean hasUnread = Boolean.FALSE;

    if (principal instanceof User) {
      hasUnread = conv.getUnreadCountJobSeeker() != null && conv.getUnreadCountJobSeeker() > 0;
    } else if (principal instanceof Employer) {
      hasUnread = conv.getUnreadCountEmployer() != null && conv.getUnreadCountEmployer() > 0;
    }

    return ConversationResponse.builder()
        .id(conv.getId())
        .jobId(conv.getJob().getId())
        .jobTitle(conv.getJob().getJobTitle())
        .applicationId(conv.getApplication().getId())
        .jobSeekerId(conv.getJobSeeker().getId())
        .jobSeekerName(conv.getJobSeeker().getFullName())
        .jobSeekerAvatar(conv.getJobSeeker().getAvatarUrl())
        .employerId(conv.getEmployer().getId())
        .employerName(conv.getEmployer().getCompanyName())
        .employerAvatar(conv.getEmployer().getAvatarUrl())
        .lastMessage(conv.getLastMessage())
        .lastMessageSenderId(conv.getLastMessageSenderId())
        .lastMessageSenderType(conv.getLastMessageSenderType())
        .hasEmployerMessage(conv.getHasEmployerMessage())
        .hasUnread(hasUnread)
        .createdAt(conv.getCreatedAt())
        .updatedAt(conv.getUpdatedAt())
        .build();
  }
}
