
package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.ArrayList;
import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "conversations", uniqueConstraints = {@UniqueConstraint(columnNames = {"job_id", "application_id"})})
public class Conversation extends BaseEntity {

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_id", nullable = false)
	private Job job;

	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "application_id", nullable = false)
	private Application application;

	// Job Seeker (User entity)
	@ManyToOne(fetch = FetchType.LAZY)
	@JoinColumn(name = "job_seeker_id", nullable = false)
	private User jobSeeker;

	// Employer (Employer entity)
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

	// Can be jobSeeker.id or employer.id
	private Long lastMessageSenderId;

	// "USER" or "EMPLOYER" để biết sender type
	@Column(length = 20)
	private String lastMessageSenderType;
}
