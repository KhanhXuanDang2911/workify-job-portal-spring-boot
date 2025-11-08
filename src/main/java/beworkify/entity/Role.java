
package beworkify.entity;

import beworkify.enumeration.UserRole;
import jakarta.persistence.*;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "roles")
public class Role extends BaseEntity {
	@Column(nullable = false, unique = true)
	@Enumerated(EnumType.STRING)
	private UserRole role;

	private String description;

	@OneToMany(mappedBy = "role")
	private List<User> users;
}
