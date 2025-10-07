package beworkify.repository;

import beworkify.entity.Role;
import beworkify.enumeration.UserRole;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {
    @Query("select distinct r from Role r")
    List<Role> findAllRoles();

    @Query("select distinct r from Role r where r.role = :role")
    Optional<Role> findByRole(@Param("role") UserRole role);

    boolean existsByRole(UserRole role);

    @Query("select case when count(r) > 0 then true else false end from Role r where r.role = :role and r.id != :id")
    boolean existsByRoleExceptForId(@Param("role") UserRole role, @Param("id") Long id);
}
