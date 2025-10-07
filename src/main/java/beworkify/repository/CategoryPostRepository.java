package beworkify.repository;

import beworkify.entity.CategoryPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface CategoryPostRepository extends JpaRepository<CategoryPost, Long> {
    boolean existsByTitle(String title);

    boolean existsByTitleAndIdNot(String title, Long id);

    boolean existsBySlug(String slug);

    @Query("select c from CategoryPost c where lower(c.title) like %:keyword% OR lower(c.description) like %:keyword%")
    Page<CategoryPost> searchCategories(@Param("keyword") String keyword, Pageable pageable);
}
