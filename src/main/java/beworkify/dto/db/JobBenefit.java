package beworkify.dto.db;

import beworkify.enumeration.BenefitType;
import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class JobBenefit {
    private BenefitType type;
    private String description;
}