package beworkify.entity;

import jakarta.persistence.*;
import java.util.HashSet;
import java.util.Set;
import lombok.*;

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
