package beworkify.repository;

import beworkify.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Industry entities. Provides methods for CRUD operations and
 * custom queries related to industries.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
  boolean existsByName(String name);

  boolean existsByEngName(String engName);

  boolean existsByNameAndIdNot(String name, Long id);

  boolean existsByEngNameAndIdNot(String engName, Long id);

  @Query(
      "SELECT i "
          + "FROM Industry i "
          + "LEFT JOIN i.categoryJob ic "
          + "WHERE (lower(i.name) LIKE %:keyword% "
          + "       OR lower(i.engName) LIKE %:keyword% "
          + "       OR lower(i.description) LIKE %:keyword%) "
          + "  AND (:categoryId IS NULL OR ic.id = :categoryId)")
  Page<Industry> searchIndustries(
      @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
}
