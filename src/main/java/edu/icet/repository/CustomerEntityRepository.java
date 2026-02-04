package edu.icet.repository;

import edu.icet.model.entity.CustomerEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface CustomerEntityRepository extends JpaRepository<CustomerEntity,String>{
    Optional<CustomerEntity>findbyEmail(String email);

    boolean existByEmail(String email);

}
