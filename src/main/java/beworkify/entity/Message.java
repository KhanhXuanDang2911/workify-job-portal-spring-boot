package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

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

  // Sender có thể là User (JOB_SEEKER) hoặc Employer
  // Chỉ lưu ID và type, không dùng FK để linh hoạt
  @Column(nullable = false)
  private Long senderId;

  // "USER" hoặc "EMPLOYER"
  @Column(nullable = false, length = 20)
  private String senderType;

  @Column(nullable = false, columnDefinition = "TEXT")
  private String content;

  @Column(nullable = false)
  @Builder.Default
  private Boolean seen = false;
}
