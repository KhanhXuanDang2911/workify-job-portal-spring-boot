package beworkify.repository;

import beworkify.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing User entities. Provides methods for CRUD operations and custom
 * queries related to users.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface UserRepository extends JpaRepository<User, Long> {
  @Query("SELECT DISTINCT u " + "FROM User u " + "JOIN FETCH u.role r " + "WHERE u.email = :email")
  Optional<User> findByEmailWithRole(@Param("email") String email);

  Optional<User> findByEmail(String email);

  boolean existsByEmail(String email);

  boolean existsByEmailAndIdNot(String email, Long id);

  @Query(
      "SELECT u "
          + "FROM User u "
          + "WHERE lower(u.fullName) LIKE %:keyword% "
          + "   OR lower(u.email) LIKE %:keyword%")
  Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
