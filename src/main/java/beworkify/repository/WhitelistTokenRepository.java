package beworkify.repository;

import beworkify.entity.WhitelistToken;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Repository
public interface WhitelistTokenRepository extends JpaRepository<WhitelistToken, Long> {
    void deleteByToken(String token);

    void deleteByEmail(String email);

    boolean existsByToken(String token);

    @Modifying
    @Transactional
    @Query("delete from WhitelistToken t where t.expiredTime < :now")
    void deleteByExpiredToken(@Param("now") LocalDateTime now);
}
