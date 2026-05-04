package com.cgi.tracker.service;

import com.cgi.tracker.model.Personnel;
import com.cgi.tracker.repository.PersonnelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class PersonnelService {

    private final PersonnelRepository personnelRepository;

    public PersonnelService(PersonnelRepository personnelRepository) {
        this.personnelRepository = personnelRepository;
    }

    public List<Personnel> getAllPersonnel() {
        return personnelRepository.findAll();
    }

    public List<Personnel> getActivePersonnel() {
        return personnelRepository.findByActiveTrue();
    }

    public Personnel getById(Long id) {
        return personnelRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Personnel not found with id: " + id));
    }

    public List<Personnel> getByUnit(String unit) {
        return personnelRepository.findByUnitContainingIgnoreCase(unit);
    }

    public List<Personnel> getByRank(String rank) {
        return personnelRepository.findByRank(rank);
    }

    public Personnel create(Personnel personnel) {
        if (personnelRepository.findByEmail(personnel.getEmail()).isPresent()) {
            throw new IllegalArgumentException("Personnel with email already exists: " + personnel.getEmail());
        }
        return personnelRepository.save(personnel);
    }

    public Personnel update(Long id, Personnel updated) {
        Personnel existing = getById(id);
        existing.setFirstName(updated.getFirstName());
        existing.setLastName(updated.getLastName());
        existing.setRank(updated.getRank());
        existing.setUnit(updated.getUnit());
        existing.setEmail(updated.getEmail());
        existing.setActive(updated.isActive());
        return personnelRepository.save(existing);
    }

    public void deactivate(Long id) {
        Personnel personnel = getById(id);
        personnel.setActive(false);
        personnelRepository.save(personnel);
    }

    public void delete(Long id) {
        personnelRepository.deleteById(id);
    }

    public List<Personnel> getPersonnelWithAssets() {
        return personnelRepository.findPersonnelWithAssignedAssets();
    }
}
