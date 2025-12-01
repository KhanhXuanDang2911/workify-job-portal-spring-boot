package beworkify.repository;

import beworkify.entity.CategoryPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing CategoryPost entities. Provides methods for CRUD operations and
 * custom queries related to post categories.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface CategoryPostRepository extends JpaRepository<CategoryPost, Long> {
  boolean existsByTitle(String title);

  boolean existsByTitleAndIdNot(String title, Long id);

  boolean existsBySlug(String slug);

  @Query(
      "SELECT c "
          + "FROM CategoryPost c "
          + "WHERE lower(c.title) LIKE %:keyword% "
          + "   OR lower(c.description) LIKE %:keyword%")
  Page<CategoryPost> searchCategories(@Param("keyword") String keyword, Pageable pageable);
}
