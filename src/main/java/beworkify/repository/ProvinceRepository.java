package beworkify.repository;

import beworkify.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Province entities. Provides methods for CRUD operations and
 * custom queries related to provinces.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  @Query(
      "SELECT p "
          + "FROM Province p "
          + "WHERE lower(p.name) LIKE %:keyword% "
          + "   OR lower(p.engName) LIKE %:keyword% "
          + "   OR lower(p.code) LIKE %:keyword%")
  Page<Province> searchProvinces(@Param("keyword") String keyword, Pageable pageable);

  java.util.List<Province> findAllByOrderByNameAsc();
}
