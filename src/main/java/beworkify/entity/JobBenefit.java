package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "job_benefits")
public class JobBenefit extends BaseEntity {
    @Column(nullable = false)
    private String icon;
    @Column(nullable = false)
    private String description;
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
