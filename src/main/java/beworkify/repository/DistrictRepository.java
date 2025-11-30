package beworkify.repository;

import beworkify.entity.District;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
  boolean existsByCode(String code);

  boolean existsByCodeAndIdNot(String code, Long id);

  java.util.List<District> findAllByOrderByNameAsc();

  java.util.List<District> findAllByProvinceIdOrderByNameAsc(Long provinceId);
}
