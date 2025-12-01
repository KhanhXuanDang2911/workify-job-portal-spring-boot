package beworkify.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

/**
 * Entity class representing a job category. Contains details about the category name, description,
 * and associated industries.
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
@Table(name = "categories_job")
public class CategoryJob extends BaseEntity {
  @Column(nullable = false)
  private String name;

  private String description;

  @Column(nullable = false)
  private String engName;

  @Builder.Default
  @OneToMany(mappedBy = "categoryJob", orphanRemoval = true, cascade = CascadeType.REMOVE)
  private Set<Industry> industries = new HashSet<>();
}
