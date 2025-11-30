package beworkify.repository;

import beworkify.entity.Industry;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface IndustryRepository extends JpaRepository<Industry, Long> {
  boolean existsByName(String name);

  boolean existsByEngName(String engName);

  boolean existsByNameAndIdNot(String name, Long id);

  boolean existsByEngNameAndIdNot(String engName, Long id);

  @Query(
      "select i from Industry i left join i.categoryJob ic "
          + "where (lower(i.name) like %:keyword% "
          + "OR lower(i.engName) like %:keyword% "
          + "OR lower(i.description) like %:keyword%) "
          + "AND (:categoryId is null or ic.id = :categoryId)")
  Page<Industry> searchIndustries(
      @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);
}
