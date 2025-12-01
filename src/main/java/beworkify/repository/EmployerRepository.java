package beworkify.repository;

import beworkify.entity.Employer;
import beworkify.enumeration.LevelCompanySize;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Employer entities. Provides methods for CRUD operations and
 * custom queries related to employers.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
  Optional<Employer> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  @Query(
      "SELECT e "
          + "FROM Employer e "
          + "WHERE (:keyword IS NULL "
          + "       OR lower(e.companyName) LIKE %:keyword% "
          + "       OR lower(e.email) LIKE %:keyword%) "
          + "  AND (:companySize IS NULL OR e.companySize = :companySize) "
          + "  AND (:provinceId IS NULL OR e.province.id = :provinceId) "
          + "  AND (:isAdmin = true OR e.status = beworkify.enumeration.StatusUser.ACTIVE)")
  Page<Employer> searchEmployers(
      @Param("keyword") String keyword,
      @Param("companySize") LevelCompanySize companySize,
      @Param("provinceId") Long provinceId,
      @Param("isAdmin") boolean isAdmin,
      Pageable pageable);
}
