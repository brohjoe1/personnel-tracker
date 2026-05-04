package com.cgi.tracker.controller;

import com.cgi.tracker.model.Personnel;
import com.cgi.tracker.service.PersonnelService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/personnel")
public class PersonnelController {

    private final PersonnelService personnelService;

    public PersonnelController(PersonnelService personnelService) {
        this.personnelService = personnelService;
    }

    // GET all personnel
    @GetMapping
    public ResponseEntity<List<Personnel>> getAll(
            @RequestParam(required = false) String unit,
            @RequestParam(required = false) String rank,
            @RequestParam(required = false, defaultValue = "false") boolean activeOnly) {

        if (unit != null) return ResponseEntity.ok(personnelService.getByUnit(unit));
        if (rank != null) return ResponseEntity.ok(personnelService.getByRank(rank));
        if (activeOnly) return ResponseEntity.ok(personnelService.getActivePersonnel());
        return ResponseEntity.ok(personnelService.getAllPersonnel());
    }

    // GET single personnel by ID
    @GetMapping("/{id}")
    public ResponseEntity<Personnel> getById(@PathVariable Long id) {
        return ResponseEntity.ok(personnelService.getById(id));
    }

    // GET personnel with assigned assets
    @GetMapping("/with-assets")
    public ResponseEntity<List<Personnel>> getWithAssets() {
        return ResponseEntity.ok(personnelService.getPersonnelWithAssets());
    }

    // POST create new personnel
    @PostMapping
    public ResponseEntity<Personnel> create(@Valid @RequestBody Personnel personnel) {
        Personnel created = personnelService.create(personnel);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    // PUT update personnel
    @PutMapping("/{id}")
    public ResponseEntity<Personnel> update(@PathVariable Long id,
                                             @Valid @RequestBody Personnel personnel) {
        return ResponseEntity.ok(personnelService.update(id, personnel));
    }

    // PATCH deactivate personnel (soft delete)
    @PatchMapping("/{id}/deactivate")
    public ResponseEntity<Void> deactivate(@PathVariable Long id) {
        personnelService.deactivate(id);
        return ResponseEntity.noContent().build();
    }

    // DELETE personnel
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        personnelService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
