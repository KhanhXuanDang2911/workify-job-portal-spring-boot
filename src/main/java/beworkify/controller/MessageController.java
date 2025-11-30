package beworkify.controller;

import beworkify.dto.request.SendMessageRequest;
import beworkify.dto.response.ConversationResponse;
import beworkify.dto.response.MessageResponse;
import beworkify.dto.response.ResponseData;
import beworkify.service.ConversationService;
import beworkify.service.MessageService;
import beworkify.util.ResponseBuilder;
import io.swagger.v3.oas.annotations.Operation;
import io.swagger.v3.oas.annotations.security.SecurityRequirement;
import io.swagger.v3.oas.annotations.tags.Tag;
import jakarta.validation.Valid;
import java.util.List;
import java.util.Map;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.Payload;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@Controller
@RequiredArgsConstructor
@Slf4j
@Tag(name = "Message", description = "Chat and messaging APIs")
@RequestMapping("/api/v1")
public class MessageController {

  private final MessageService messageService;
  private final ConversationService conversationService;

  @MessageMapping("/chat.sendMessage")
  public void sendMessageViaWebSocket(@Payload SendMessageRequest request) {
    log.info("Received WebSocket message");
    messageService.sendMessage(request);
  }

  @PostMapping("/messages")
  @Operation(summary = "Send a message", security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<MessageResponse>> sendMessage(
      @Valid @RequestBody SendMessageRequest request) {

    MessageResponse message = messageService.sendMessage(request);
    String msg = "Message sent successfully";

    return ResponseBuilder.withData(HttpStatus.OK, msg, message);
  }

  @GetMapping("/conversations")
  @Operation(
      summary = "Get user's conversations",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<List<ConversationResponse>>> getConversations() {

    List<ConversationResponse> conversations = conversationService.getConversationsForCurrentUser();
    String msg = "Conversations retrieved successfully";

    return ResponseBuilder.withData(HttpStatus.OK, msg, conversations);
  }

  @GetMapping("/messages/{conversationId}")
  @Operation(
      summary = "Get messages by conversation",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<List<MessageResponse>>> getMessages(
      @PathVariable Long conversationId) {

    List<MessageResponse> messages = messageService.getMessagesByConversationId(conversationId);
    String msg = "Messages retrieved successfully";

    return ResponseBuilder.withData(HttpStatus.OK, msg, messages);
  }

  @PutMapping("/messages/{conversationId}/seen")
  @Operation(
      summary = "Mark messages as seen",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<Void>> markMessagesAsSeen(@PathVariable Long conversationId) {

    messageService.markMessagesAsSeen(conversationId);
    String msg = "Messages marked as seen";

    return ResponseBuilder.noData(HttpStatus.OK, msg);
  }

  @GetMapping("/messages/unread-conversations")
  @Operation(
      summary = "Get number of conversations with unread messages",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<Map<String, Integer>>> getUnreadConversationsCount() {

    Integer count = conversationService.countConversationsWithUnreadForCurrentUser();
    String msg = "Unread conversations count retrieved";

    return ResponseBuilder.withData(HttpStatus.OK, msg, Map.of("unreadConversations", count));
  }

  @GetMapping("/conversations/application/{applicationId}")
  @Operation(
      summary = "Get conversation by application ID",
      security = @SecurityRequirement(name = "bearerAuth"))
  public ResponseEntity<ResponseData<ConversationResponse>> getConversationByApplicationId(
      @PathVariable Long applicationId) {

    ConversationResponse conversation =
        conversationService.getConversationByApplicationId(applicationId);
    String msg = "Conversation retrieved successfully";

    return ResponseBuilder.withData(HttpStatus.OK, msg, conversation);
  }
}
