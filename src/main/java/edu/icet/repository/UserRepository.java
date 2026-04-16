package edu.icet.repository;

import edu.icet.model.entity.User;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface UserRepository extends JpaRepository<User, Long> {

    Optional<User> findByEmail(String email);

    Optional<User> findByEmailIgnoreCase(String email);

    List<User> findAllByOrderByCreatedAtDesc();

    long countByAccessStatus(String accessStatus);

    long countByRoleAndAccessStatus(String role, String accessStatus);
}
