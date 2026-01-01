package edu.icet.repository;

import edu.icet.model.entity.AdminEntity;
import org.springframework.boot.autoconfigure.data.jpa.JpaRepositoriesAutoConfiguration;
import org.springframework.data.jpa.repository.JpaRepository;

public interface AdminSignInRepository extends JpaRepository<AdminEntity, String> {




}
