
package beworkify.entity;

import jakarta.persistence.*;
import java.util.List;
import lombok.*;

@Entity
@Table(name = "industries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Industry extends BaseEntity {
	@Column(nullable = false)
	private String name;

	@Column(nullable = false)
	private String engName;

	private String description;

	@OneToMany(mappedBy = "industry", orphanRemoval = true, cascade = CascadeType.REMOVE)
	private List<JobIndustry> jobIndustries;

	@ManyToOne
	@JoinColumn(name = "category_job_id", nullable = false)
	private CategoryJob categoryJob;
}
