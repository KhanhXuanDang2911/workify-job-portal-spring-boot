package beworkify.entity;

import jakarta.persistence.Entity;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "locations")
public class Location extends BaseEntity {
    @ManyToOne
    @JoinColumn(name = "province_id", nullable = false)
    private Province province;
    @ManyToOne
    @JoinColumn(name = "district_id", nullable = false)
    private District district;
    private String detailAddress;
    @ManyToOne
    @JoinColumn(name = "job_id", nullable = false)
    private Job job;
}
