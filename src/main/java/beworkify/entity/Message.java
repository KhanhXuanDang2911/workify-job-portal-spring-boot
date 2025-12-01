package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

/**
 * Entity class representing a chat message. Contains details about the sender, content, and seen
 * status.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "messages")
public class Message extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "conversation_id", nullable = false)
  private Conversation conversation;

  @Column(nullable = false)
  private Long senderId;

  @Column(nullable = false, length = 20)
  private String senderType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  @Builder.Default
  private Boolean seen = false;
}
