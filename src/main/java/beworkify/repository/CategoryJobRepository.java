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

/**
 * Repository interface for managing CategoryJob entities. Provides methods for CRUD operations and
 * custom queries related to job categories.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface CategoryJobRepository
    extends JpaRepository<CategoryJob, Long>, CategoryJobRepositoryCustom {
  boolean existsByName(String name);

  boolean existsByNameAndIdNot(String name, Long id);

  @Query(
      "SELECT c "
          + "FROM CategoryJob c "
          + "WHERE lower(c.name) LIKE %:keyword% "
          + "   OR lower(c.description) LIKE %:keyword% "
          + "   OR lower(c.engName) LIKE %:keyword%")
  Page<CategoryJob> searchJobCategories(@Param("keyword") String keyword, Pageable pageable);

  Optional<CategoryJob> findByName(String name);
}
