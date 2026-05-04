package com.cgi.tracker.repository;

import com.cgi.tracker.model.Personnel;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends JpaRepository<Personnel, Long> {

    // Find all active personnel
    List<Personnel> findByActiveTrue();

    // Find by unit
    List<Personnel> findByUnitContainingIgnoreCase(String unit);

    // Find by rank
    List<Personnel> findByRank(String rank);

    // Find by email
    Optional<Personnel> findByEmail(String email);

    // Custom JPQL - find personnel with assigned assets
    @Query("SELECT DISTINCT p FROM Personnel p JOIN p.assets a WHERE a.status = 'ASSIGNED'")
    List<Personnel> findPersonnelWithAssignedAssets();
}
