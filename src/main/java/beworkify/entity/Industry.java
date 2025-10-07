package beworkify.entity;

import jakarta.persistence.*;
import lombok.*;

import java.util.List;

@Entity
@Table(name = "industries")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Industry extends BaseEntity {
    @Column(nullable = false, unique = true, length = 150)
    private String name;

    @Column(length = 255)
    private String description;

    @OneToMany(mappedBy = "industry", orphanRemoval = true, cascade = CascadeType.REMOVE)
    private List<JobIndustry> jobIndustries;
}
