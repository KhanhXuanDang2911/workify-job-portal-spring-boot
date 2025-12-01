package beworkify.repository;

import beworkify.entity.Post;
import beworkify.enumeration.StatusPost;
import java.util.List;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Post entities. Provides methods for CRUD operations and custom
 * queries related to blog posts.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  @Query(
      "SELECT p "
          + "FROM Post p "
          + "JOIN p.category c "
          + "WHERE (:categoryId IS NULL OR c.id = :categoryId) "
          + "  AND (lower(p.title) LIKE concat('%', :keyword, '%') "
          + "       OR lower(p.content) LIKE concat('%', :keyword, '%'))")
  Page<Post> searchPosts(
      @Param("keyword") String keyword, @Param("categoryId") Long categoryId, Pageable pageable);

  @Query(
      "SELECT p "
          + "FROM Post p "
          + "JOIN p.category c "
          + "WHERE (:categoryId IS NULL OR c.id = :categoryId) "
          + "  AND (lower(p.title) LIKE concat('%', :keyword, '%') "
          + "       OR lower(p.content) LIKE concat('%', :keyword, '%')) "
          + "  AND p.status = :publicStatus")
  Page<Post> searchPublicPosts(
      @Param("keyword") String keyword,
      @Param("categoryId") Long categoryId,
      @Param("publicStatus") StatusPost publicStatus,
      Pageable pageable);

  @Query(
      "SELECT p "
          + "FROM Post p "
          + "JOIN p.category c "
          + "WHERE (:categoryId IS NULL OR c.id = :categoryId) "
          + "  AND (lower(p.title) LIKE concat('%', :keyword, '%') "
          + "       OR lower(p.content) LIKE concat('%', :keyword, '%')) "
          + "  AND p.employerAuthor.id = :employerId")
  Page<Post> searchPostsByEmployer(
      @Param("keyword") String keyword,
      @Param("categoryId") Long categoryId,
      @Param("employerId") Long employerId,
      Pageable pageable);

  @Query(
      "SELECT p "
          + "FROM Post p "
          + "JOIN p.category c "
          + "WHERE (:categoryId IS NULL OR c.id = :categoryId) "
          + "  AND (lower(p.title) LIKE concat('%', :keyword, '%') "
          + "       OR lower(p.content) LIKE concat('%', :keyword, '%')) "
          + "  AND p.employerAuthor.id = :employerId "
          + "  AND p.status = :status")
  Page<Post> searchPostsByEmployerAndStatus(
      @Param("keyword") String keyword,
      @Param("categoryId") Long categoryId,
      @Param("employerId") Long employerId,
      @Param("status") StatusPost status,
      Pageable pageable);

  @Query(
      value =
          "SELECT p.* "
              + "FROM posts p "
              + "WHERE p.status = 'PUBLIC' "
              + "  AND p.id <> :excludeId "
              + "  AND (to_tsvector('english', COALESCE(p.title, '') || ' ' || COALESCE(p.content_text, '')) "
              + "       @@ plainto_tsquery('english', :searchText)) "
              + "ORDER BY ts_rank(to_tsvector('english', COALESCE(p.title, '') || ' ' || COALESCE(p.content_text, '')), "
              + "                 plainto_tsquery('english', :searchText)) DESC, "
              + "         p.updated_at DESC "
              + "LIMIT :limit",
      nativeQuery = true)
  List<Post> findRelatedByFullTextSearch(
      @Param("excludeId") Long excludeId,
      @Param("searchText") String searchText,
      @Param("limit") int limit);

  @Query(
      value =
          "SELECT p.* "
              + "FROM posts p "
              + "WHERE p.status = 'PUBLIC' "
              + "  AND p.id <> :excludeId "
              + "  AND (similarity(p.title, :title) > 0.1 "
              + "       OR similarity(p.content_text, :contentText) > 0.05) "
              + "ORDER BY GREATEST(similarity(p.title, :title), "
              + "                  similarity(p.content_text, :contentText)) DESC, "
              + "         p.updated_at DESC "
              + "LIMIT :limit",
      nativeQuery = true)
  List<Post> findRelatedBySimilarity(
      @Param("excludeId") Long excludeId,
      @Param("title") String title,
      @Param("contentText") String contentText,
      @Param("limit") int limit);

  @Query(
      "SELECT p FROM Post p "
          + "WHERE p.status = :status "
          + "  AND p.category.id = :categoryId "
          + "  AND p.id <> :excludeId "
          + "ORDER BY p.updatedAt DESC")
  List<Post> findRelatedByCategory(
      @Param("status") StatusPost status,
      @Param("categoryId") Long categoryId,
      @Param("excludeId") Long excludeId,
      Pageable pageable);

  @Query(
      "SELECT p FROM Post p "
          + "WHERE p.status = :status "
          + "  AND p.id <> :excludeId "
          + "ORDER BY p.updatedAt DESC")
  List<Post> findLatestPosts(
      @Param("status") StatusPost status, @Param("excludeId") Long excludeId, Pageable pageable);

  @Query("SELECT p " + "FROM Post p " + "WHERE p.status = :status " + "ORDER BY p.updatedAt DESC")
  List<Post> findLatestPublicPosts(@Param("status") StatusPost status, Pageable pageable);
}
