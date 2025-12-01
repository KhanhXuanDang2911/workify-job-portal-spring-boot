package beworkify.entity;

import jakarta.persistence.*;
import java.util.ArrayList;
import java.util.List;
import lombok.*;

/**
 * Entity class representing a conversation between a job seeker and an employer. Tracks messages,
 * unread counts, and the associated job application.
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
@Table(
    name = "conversations",
    uniqueConstraints = {@UniqueConstraint(columnNames = {"job_id", "application_id"})})
public class Conversation extends BaseEntity {

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_id", nullable = false)
  private Job job;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "application_id", nullable = false)
  private Application application;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "job_seeker_id", nullable = false)
  private User jobSeeker;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "employer_id", nullable = false)
  private Employer employer;

  @OneToMany(mappedBy = "conversation", cascade = CascadeType.ALL, orphanRemoval = true)
  @Builder.Default
  private List<Message> messages = new ArrayList<>();

  @Column(nullable = false)
  @Builder.Default
  private Boolean hasEmployerMessage = false;

  @Column(columnDefinition = "TEXT")
  private String lastMessage;

  private Long lastMessageSenderId;

  @Column(length = 20)
  private String lastMessageSenderType;

  @Column(name = "unread_count_job_seeker", nullable = false)
  @Builder.Default
  private Integer unreadCountJobSeeker = 0;

  @Column(name = "unread_count_employer", nullable = false)
  @Builder.Default
  private Integer unreadCountEmployer = 0;
}
