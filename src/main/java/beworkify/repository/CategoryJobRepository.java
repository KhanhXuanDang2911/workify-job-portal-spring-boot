
package beworkify.repository;

import beworkify.entity.CategoryJob;
import beworkify.repository.custom.CategoryJobRepositoryCustom;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryJobRepository extends JpaRepository<CategoryJob, Long>, CategoryJobRepositoryCustom {
	boolean existsByName(String name);

	boolean existsByNameAndIdNot(String name, Long id);

	@Query("select c from CategoryJob c where lower(c.name) like %:keyword% OR lower(c.description) like %:keyword% or lower(c.engName) like %:keyword%")
	Page<CategoryJob> searchJobCategories(@Param("keyword") String keyword, Pageable pageable);

	Optional<CategoryJob> findByName(String name);
}
