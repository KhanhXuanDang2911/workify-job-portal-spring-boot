
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

@Repository
public interface EmployerRepository extends JpaRepository<Employer, Long> {
	Optional<Employer> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	@Query("select e from Employer e where (:keyword is null or lower(e.companyName) like %:keyword% OR lower(e.email) like %:keyword%)"
			+ " and (:companySize is null or e.companySize = :companySize)"
			+ " and (:provinceId is null or e.province.id = :provinceId)"
			+ " and (:isAdmin = true or e.status = beworkify.enumeration.StatusUser.ACTIVE)")
	Page<Employer> searchEmployers(@Param("keyword") String keyword, @Param("companySize") LevelCompanySize companySize,
			@Param("provinceId") Long provinceId, @Param("isAdmin") boolean isAdmin, Pageable pageable);
}
