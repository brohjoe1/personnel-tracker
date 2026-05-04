package com.cgi.tracker.controller;

import com.cgi.tracker.model.Asset;
import com.cgi.tracker.service.AssetService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.util.List;

@RestController
@RequestMapping("/api/assets")
public class AssetController {

    private final AssetService assetService;

    public AssetController(AssetService assetService) {
        this.assetService = assetService;
    }

    // GET all assets
    @GetMapping
    public ResponseEntity<List<Asset>> getAll(
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String category) {

        if (status != null) return ResponseEntity.ok(assetService.getByStatus(status));
        if (category != null) return ResponseEntity.ok(assetService.getByCategory(category));
        return ResponseEntity.ok(assetService.getAllAssets());
    }

    // GET single asset
    @GetMapping("/{id}")
    public ResponseEntity<Asset> getById(@PathVariable String id) {
        return ResponseEntity.ok(assetService.getById(id));
    }

    // GET unassigned assets
    @GetMapping("/unassigned")
    public ResponseEntity<List<Asset>> getUnassigned() {
        return ResponseEntity.ok(assetService.getUnassignedAssets());
    }

    // GET assets for a specific personnel member
    @GetMapping("/personnel/{personnelId}")
    public ResponseEntity<List<Asset>> getByPersonnel(@PathVariable String personnelId) {
        return ResponseEntity.ok(assetService.getAssetsByPersonnel(personnelId));
    }

    // POST create asset
    @PostMapping
    public ResponseEntity<Asset> create(@Valid @RequestBody Asset asset) {
        return ResponseEntity.status(HttpStatus.CREATED).body(assetService.create(asset));
    }

    // PUT update asset
    @PutMapping("/{id}")
    public ResponseEntity<Asset> update(@PathVariable String id,
                                         @Valid @RequestBody Asset asset) {
        return ResponseEntity.ok(assetService.update(id, asset));
    }

    // PATCH assign asset to personnel
    @PatchMapping("/{id}/assign/{personnelId}")
    public ResponseEntity<Asset> assign(@PathVariable String id,
                                         @PathVariable String personnelId) {
        return ResponseEntity.ok(assetService.assign(id, personnelId));
    }

    // PATCH unassign asset
    @PatchMapping("/{id}/unassign")
    public ResponseEntity<Asset> unassign(@PathVariable String id) {
        return ResponseEntity.ok(assetService.unassign(id));
    }

    // PATCH send to maintenance
    @PatchMapping("/{id}/maintenance")
    public ResponseEntity<Asset> maintenance(@PathVariable String id) {
        return ResponseEntity.ok(assetService.sendToMaintenance(id));
    }

    // DELETE asset
    @DeleteMapping("/{id}")
    public ResponseEntity<Void> delete(@PathVariable String id) {
        assetService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
