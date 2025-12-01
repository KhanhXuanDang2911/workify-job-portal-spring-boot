package beworkify.repository;

import beworkify.entity.Role;
import beworkify.enumeration.UserRole;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Role entities. Provides methods for CRUD operations and custom
 * queries related to user roles.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
  @Query("select distinct r from Role r")
  List<Role> findAllRoles();

  @Query("select distinct r from Role r where r.role = :role")
  Optional<Role> findByRole(@Param("role") UserRole role);

  boolean existsByRole(UserRole role);

  @Query(
      "SELECT CASE WHEN COUNT(r) > 0 THEN true ELSE false END "
          + "FROM Role r "
          + "WHERE r.role = :role "
          + "  AND r.id != :id")
  boolean existsByRoleExceptForId(@Param("role") UserRole role, @Param("id") Long id);
}
