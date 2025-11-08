
package beworkify.repository;

import beworkify.entity.User;
import java.util.Optional;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {
	@Query("select distinct u from User u join fetch u.role r where u.email = :email")
	Optional<User> findByEmailWithRole(@Param("email") String email);

	Optional<User> findByEmail(String email);

	boolean existsByEmail(String email);

	boolean existsByEmailAndIdNot(String email, Long id);

	@Query("select u from User u where lower(u.fullName) like %:keyword% OR lower(u.email) like %:keyword%")
	Page<User> searchUsers(@Param("keyword") String keyword, Pageable pageable);
}
