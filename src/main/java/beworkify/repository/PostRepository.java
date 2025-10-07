package beworkify.repository;

import beworkify.entity.Post;
import beworkify.enumeration.StatusPost;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface PostRepository extends JpaRepository<Post, Long> {
  @Query("select p from Post p join p.category c" +
      " where (:categoryId is null or c.id = :categoryId)" +
      " and (:authorId is null or p.author.id = :authorId)" +
      " and (lower(p.title) like concat('%', :keyword, '%')" +
      " OR lower(p.content) like concat('%', :keyword, '%')" +
      " OR lower(p.author.fullName) like concat('%', :keyword, '%'))")
  Page<Post> searchPosts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId, Long authorId,
      Pageable pageable);

  @Query("select p from Post p join p.category c" +
      " where (:categoryId is null or c.id = :categoryId)" +
      " and (lower(p.title) like concat('%', :keyword, '%')" +
      " OR lower(p.content) like concat('%', :keyword, '%')" +
      " OR lower(p.author.fullName) like concat('%', :keyword, '%'))" +
      " and p.status = :publicStatus")
  Page<Post> searchPublicPosts(@Param("keyword") String keyword, @Param("categoryId") Long categoryId,
      @Param("publicStatus") StatusPost publicStatus, Pageable pageable);

  @Query(value = """
      SELECT p.* FROM posts p
      WHERE p.status = 'PUBLIC'
        AND p.id <> :excludeId
        AND (
          to_tsvector('english', COALESCE(p.title, '') || ' ' || COALESCE(p.content_text, ''))
          @@ plainto_tsquery('english', :searchText)
        )
      ORDER BY ts_rank(
        to_tsvector('english', COALESCE(p.title, '') || ' ' || COALESCE(p.content_text, '')),
        plainto_tsquery('english', :searchText)
      ) DESC, p.updated_at DESC
      LIMIT :limit
      """, nativeQuery = true)
  List<Post> findRelatedByFullTextSearch(@Param("excludeId") Long excludeId,
      @Param("searchText") String searchText,
      @Param("limit") int limit);

  @Query(value = """
      SELECT p.* FROM posts p
      WHERE p.status = 'PUBLIC'
        AND p.id <> :excludeId
        AND (
          similarity(p.title, :title) > 0.1
          OR similarity(p.content_text, :contentText) > 0.05
        )
      ORDER BY GREATEST(
        similarity(p.title, :title),
        similarity(p.content_text, :contentText)
      ) DESC, p.updated_at DESC
      LIMIT :limit
      """, nativeQuery = true)
  List<Post> findRelatedBySimilarity(@Param("excludeId") Long excludeId,
      @Param("title") String title,
      @Param("contentText") String contentText,
      @Param("limit") int limit);

  @Query("""
      SELECT p FROM Post p
      WHERE p.status = :status
        AND p.category.id = :categoryId
        AND p.id <> :excludeId
      ORDER BY p.updatedAt DESC
      """)
  List<Post> findRelatedByCategory(@Param("status") StatusPost status,
      @Param("categoryId") Long categoryId,
      @Param("excludeId") Long excludeId,
      Pageable pageable);

  @Query("""
      SELECT p FROM Post p
      WHERE p.status = :status
        AND p.id <> :excludeId
      ORDER BY p.updatedAt DESC
      """)
  List<Post> findLatestPosts(@Param("status") StatusPost status,
      @Param("excludeId") Long excludeId,
      Pageable pageable);

  @Query("""
      SELECT p FROM Post p
      WHERE p.status = :status
      ORDER BY p.updatedAt DESC
      """)
  List<Post> findLatestPublicPosts(@Param("status") StatusPost status, Pageable pageable);

}