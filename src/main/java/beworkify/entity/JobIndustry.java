package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "job_industries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobIndustry extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
    @ManyToOne
    @JoinColumn(name = "industry_id", nullable = false)
    private Industry industry;
}
