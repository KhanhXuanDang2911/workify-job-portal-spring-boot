package beworkify.repository;

import beworkify.entity.OTPCode;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OTPRepository extends JpaRepository<OTPCode, Long> {
    Optional<OTPCode> findByCode(String code);

    void deleteByCode(String code);
}
