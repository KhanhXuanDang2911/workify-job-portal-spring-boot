package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "notifications")
public class Notification extends BaseEntity {

  @Column(nullable = false)
  private String title;

  @Column(columnDefinition = "TEXT")
  private String content;

  private String link;

  @Column(nullable = false)
  private Boolean readFlag = false;

  @ManyToOne
  @JoinColumn(name = "recipient_user_id")
  private User recipientUser;

  @ManyToOne
  @JoinColumn(name = "recipient_employer_id")
  private Employer recipientEmployer;

  private Long jobId;
  private Long applicationId;

  @Column(nullable = false)
  private String type;
}
