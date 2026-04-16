package edu.icet.repository;

import edu.icet.model.entity.RentalRequestEntity;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;

@Repository
public interface RentalRequestRepository extends JpaRepository<RentalRequestEntity, Long> {
    long countByStatusIn(Collection<String> statuses);
}
