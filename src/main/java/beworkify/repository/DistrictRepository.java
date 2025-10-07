package beworkify.repository;

import beworkify.entity.District;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface DistrictRepository extends JpaRepository<District, Long> {
    boolean existsByCode(String code);

    boolean existsByCodeAndIdNot(String code, Long id);

    @Query("select d from District d where lower(d.name) like %:keyword% OR lower(d.code) like %:keyword%")
    Page<District> searchDistricts(@Param("keyword") String keyword, Pageable pageable);

    java.util.List<District> findAllByOrderByNameAsc();

    java.util.List<District> findAllByProvinceIdOrderByNameAsc(Long provinceId);
}
