package beworkify.repository;

import beworkify.entity.Province;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface ProvinceRepository extends JpaRepository<Province, Long> {
  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  @Query(
      "select p from Province p where lower(p.name) like %:keyword% OR lower(p.engName) like %:keyword% OR lower(p.code) like %:keyword%")
  Page<Province> searchProvinces(@Param("keyword") String keyword, Pageable pageable);

  java.util.List<Province> findAllByOrderByNameAsc();
}
