package edu.icet.repository;

import edu.icet.model.entity.AdminEntity;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface AdminSignInRepository extends JpaRepository<AdminEntity, Long> {

    boolean existsByEmail(String email);

}
