package edu.icet.repository;

import edu.icet.model.entity.AdminEntity;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AdminSignInRepository extends JpaRepository<AdminEntity, Long> {

    Optional<AdminEntity>findbyEmail(String email);

    boolean existsByEmail(String email);

}
