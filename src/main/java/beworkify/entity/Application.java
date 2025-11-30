package beworkify.entity;

import beworkify.enumeration.ApplicationStatus;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "applications")
public class Application extends BaseEntity {
  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false)
  private String email;

  @Column(nullable = false)
  private String phoneNumber;

  @Column(nullable = false)
  private String coverLetter;

  @Column(nullable = false)
  private String cvUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private ApplicationStatus status;

  @ManyToOne
  @JoinColumn(nullable = false, name = "user_id")
  private User user;

  @ManyToOne
  @JoinColumn(nullable = false, name = "job_id")
  private Job job;

  @Column(nullable = false)
  private Integer applyCount;
}
