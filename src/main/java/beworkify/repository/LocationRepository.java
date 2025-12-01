package beworkify.repository;

import beworkify.entity.Location;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

/**
 * Repository interface for managing Location entities. Provides methods for CRUD operations related
 * to locations.
 *
 * @author KhanhDX
 * @since 1.0.0
 */
@Repository
public interface LocationRepository extends JpaRepository<Location, Long> {}
