
package beworkify.entity;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import jakarta.persistence.*;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;
import org.hibernate.annotations.JdbcTypeCode;
import org.hibernate.type.SqlTypes;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
@Entity
@Table(name = "employers")
public class Employer extends BaseEntity implements UserDetails {
	@Column(nullable = false, unique = true)
	private String email;

	@Column(nullable = false)
	private String phoneNumber;

	@Column(nullable = false)
	private String password;

	private String avatarUrl;

	@Column(nullable = false)
	@Enumerated(EnumType.STRING)
	private StatusUser status;

	@Column(nullable = false)
	private String companyName;

	@Column(nullable = false)
	private LevelCompanySize companySize;

	@Column(nullable = false)
	private String contactPerson;

	private String backgroundUrl;

	@Column(columnDefinition = "TEXT")
	private String aboutCompany;

	@Column(columnDefinition = "jsonb")
	@JdbcTypeCode(SqlTypes.JSON)
	private List<String> websiteUrls;

	private String facebookUrl;
	private String twitterUrl;
	private String linkedinUrl;
	private String googleUrl;
	private String youtubeUrl;
	private String employerSlug;

	@ManyToOne
	@JoinColumn(name = "province_id", nullable = false)
	private Province province;

	@ManyToOne
	@JoinColumn(name = "district_id", nullable = false)
	private District district;

	private String detailAddress;

	@OneToMany(mappedBy = "author", cascade = CascadeType.REMOVE, orphanRemoval = true)
	private List<Job> jobs;

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		Set<GrantedAuthority> authorities = new HashSet<>();
		authorities.add(new SimpleGrantedAuthority("ROLE_EMPLOYER"));
		return authorities;
	}

	@Override
	public String getUsername() {
		return this.email;
	}

	@Override
	public boolean isEnabled() {
		return this.status.equals(StatusUser.ACTIVE) || this.status.equals(StatusUser.PENDING);
	}
}
