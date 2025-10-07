package beworkify.entity;

import beworkify.enumeration.LevelCompanySize;
import beworkify.enumeration.StatusUser;
import jakarta.persistence.*;
import lombok.*;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

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
    private String phoneNumber;
    @Column(nullable = false)
    private String password;
    private String avatarUrl;
    @Enumerated(EnumType.STRING)
    private StatusUser status;
    private String companyName;
    @Enumerated(EnumType.STRING)
    private LevelCompanySize companySize;
    private String contactPerson;
    private String backgroundUrl;
    private String aboutCompany;
    private String employerSlug;
    @ManyToOne
    @JoinColumn(name = "province_id")
    private Province province;
    @ManyToOne
    @JoinColumn(name = "district_id")
    private District district;
    private String detailAddress;

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
