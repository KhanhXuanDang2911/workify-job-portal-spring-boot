package beworkify.entity;

import beworkify.enumeration.Gender;
import beworkify.enumeration.StatusUser;
import jakarta.persistence.*;
import java.time.LocalDate;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;
import java.util.Set;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

/**
 * Entity class representing a user (job seeker or admin). Contains details about the user's
 * profile, credentials, and role.
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
@Table(name = "users")
public class User extends BaseEntity implements UserDetails {
  @Column(nullable = false)
  private String fullName;

  @Column(nullable = false, unique = true)
  private String email;

  private String phoneNumber;
  private String password;
  private String avatarUrl;

  @Column(nullable = false)
  @Enumerated(EnumType.STRING)
  private StatusUser status;

  private LocalDate birthDate;
  private Boolean noPassword;

  @Enumerated(EnumType.STRING)
  private Gender gender;

  @ManyToOne
  @JoinColumn(name = "province_id")
  private Province province;

  @ManyToOne
  @JoinColumn(name = "district_id")
  private District district;

  private String detailAddress;

  @ManyToOne
  @JoinColumn(name = "role_id", nullable = false)
  private Role role;

  @OneToMany(mappedBy = "userAuthor", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Post> posts;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private List<Application> applications;

  @OneToMany(mappedBy = "user", cascade = CascadeType.ALL, orphanRemoval = true)
  private Set<SavedJob> savedJobs;

  @ManyToOne
  @JoinColumn(name = "industry_id")
  private Industry industry;

  @Override
  public Collection<? extends GrantedAuthority> getAuthorities() {
    Set<GrantedAuthority> authorities = new HashSet<>();
    authorities.add(
        new SimpleGrantedAuthority(String.format("ROLE_%s", this.role.getRole().getName())));
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
