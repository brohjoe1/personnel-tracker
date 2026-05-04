package com.cgi.tracker.service;

import com.cgi.tracker.model.Asset;
import com.cgi.tracker.model.Personnel;
import com.cgi.tracker.repository.AssetRepository;
import com.cgi.tracker.repository.PersonnelRepository;
import jakarta.persistence.EntityNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;

@Service
@Transactional
public class AssetService {

    private final AssetRepository assetRepository;
    private final PersonnelRepository personnelRepository;

    public AssetService(AssetRepository assetRepository, PersonnelRepository personnelRepository) {
        this.assetRepository = assetRepository;
        this.personnelRepository = personnelRepository;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getById(Long id) {
        return assetRepository.findById(id)
                .orElseThrow(() -> new EntityNotFoundException("Asset not found with id: " + id));
    }

    public List<Asset> getByStatus(String status) {
        return assetRepository.findByStatus(Asset.AssetStatus.valueOf(status.toUpperCase()));
    }

    public List<Asset> getByCategory(String category) {
        return assetRepository.findByCategory(Asset.AssetCategory.valueOf(category.toUpperCase()));
    }

    public List<Asset> getUnassignedAssets() {
        return assetRepository.findByAssignedToIsNull();
    }

    public List<Asset> getAssetsByPersonnel(Long personnelId) {
        return assetRepository.findByAssignedToId(personnelId);
    }

    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset update(Long id, Asset updated) {
        Asset existing = getById(id);
        existing.setName(updated.getName());
        existing.setSerialNumber(updated.getSerialNumber());
        existing.setCategory(updated.getCategory());
        existing.setStatus(updated.getStatus());
        return assetRepository.save(existing);
    }

    // Assign asset to a personnel member
    public Asset assign(Long assetId, Long personnelId) {
        Asset asset = getById(assetId);
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new EntityNotFoundException("Personnel not found with id: " + personnelId));

        if (asset.getStatus() == Asset.AssetStatus.MAINTENANCE) {
            throw new IllegalStateException("Cannot assign asset currently in maintenance: " + assetId);
        }

        asset.setAssignedTo(personnel);
        asset.setStatus(Asset.AssetStatus.ASSIGNED);
        return assetRepository.save(asset);
    }

    // Unassign asset from personnel
    public Asset unassign(Long assetId) {
        Asset asset = getById(assetId);
        asset.setAssignedTo(null);
        asset.setStatus(Asset.AssetStatus.UNASSIGNED);
        return assetRepository.save(asset);
    }

    // Send asset to maintenance
    public Asset sendToMaintenance(Long assetId) {
        Asset asset = getById(assetId);
        asset.setAssignedTo(null);
        asset.setStatus(Asset.AssetStatus.MAINTENANCE);
        return assetRepository.save(asset);
    }

    public void delete(Long id) {
        assetRepository.deleteById(id);
    }
}
