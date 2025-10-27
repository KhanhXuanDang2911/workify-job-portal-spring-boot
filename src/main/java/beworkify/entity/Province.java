package beworkify.entity;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Entity;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.util.List;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "provinces")
public class Province extends BaseEntity {
    private String code;
    private String name;
    private String engName;
    private String provinceSlug;

    @OneToMany(mappedBy = "province", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<District> district;

    @OneToMany(mappedBy = "province")
    @JsonIgnore
    private List<Location> locations;
}
