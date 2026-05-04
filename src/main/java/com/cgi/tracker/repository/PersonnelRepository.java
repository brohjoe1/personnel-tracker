package com.cgi.tracker.repository;

import com.cgi.tracker.model.Personnel;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface PersonnelRepository extends MongoRepository<Personnel, String> {

    // Find all active personnel
    List<Personnel> findByActiveTrue();

    // Find by unit
    List<Personnel> findByUnitContainingIgnoreCase(String unit);

    // Find by rank
    List<Personnel> findByRank(String rank);

    // Find by email
    Optional<Personnel> findByEmail(String email);
}
