package com.cgi.tracker.service;

import com.cgi.tracker.kafka.AssetEvent;
import com.cgi.tracker.kafka.AssetEventProducer;
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
    private final AssetEventProducer eventProducer;

    public AssetService(AssetRepository assetRepository, 
                        PersonnelRepository personnelRepository,
                        AssetEventProducer eventProducer) {
        this.assetRepository = assetRepository;
        this.personnelRepository = personnelRepository;
        this.eventProducer = eventProducer;
    }

    public List<Asset> getAllAssets() {
        return assetRepository.findAll();
    }

    public Asset getById(String id) {
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
        return assetRepository.findByAssignedToIdIsNull();
    }

    public List<Asset> getAssetsByPersonnel(String personnelId) {
        return assetRepository.findByAssignedToId(personnelId);
    }

    public Asset create(Asset asset) {
        return assetRepository.save(asset);
    }

    public Asset update(String id, Asset updated) {
        Asset existing = getById(id);
        existing.setName(updated.getName());
        existing.setSerialNumber(updated.getSerialNumber());
        existing.setCategory(updated.getCategory());
        existing.setStatus(updated.getStatus());
        return assetRepository.save(existing);
    }

    // Assign asset to a personnel member
    public Asset assign(String assetId, String personnelId) {
        Asset asset = getById(assetId);
        Personnel personnel = personnelRepository.findById(personnelId)
                .orElseThrow(() -> new EntityNotFoundException("Personnel not found with id: " + personnelId));

        if (asset.getStatus() == Asset.AssetStatus.MAINTENANCE) {
            throw new IllegalStateException("Cannot assign asset currently in maintenance: " + assetId);
        }

        asset.setAssignedToId(personnelId);
        asset.setAssignedToName(personnel.getFirstName() + " " + personnel.getLastName());
        asset.setStatus(Asset.AssetStatus.ASSIGNED);
        Asset saved = assetRepository.save(asset);
        
        eventProducer.publishAssetEvent(new AssetEvent(
                saved.getId(), saved.getSerialNumber(), saved.getName(),
                "ASSIGNED", personnelId,
                personnel.getFirstName() + " " + personnel.getLastName()));
        
        return saved;
    }

    // Unassign asset from personnel
    public Asset unassign(String assetId) {
        Asset asset = getById(assetId);
        String personnelName = asset.getAssignedToName() != null ? asset.getAssignedToName() : "N/A";
        String personnelId = asset.getAssignedToId();
        
        asset.setAssignedToId(null);
        asset.setAssignedToName(null);
        asset.setStatus(Asset.AssetStatus.UNASSIGNED);
        Asset saved = assetRepository.save(asset);
        
        eventProducer.publishAssetEvent(new AssetEvent(
                saved.getId(), saved.getSerialNumber(), saved.getName(),
                "UNASSIGNED", personnelId, personnelName));
        
        return saved;
    }

    // Send asset to maintenance
    public Asset sendToMaintenance(String assetId) {
        Asset asset = getById(assetId);
        asset.setAssignedToId(null);
        asset.setAssignedToName(null);
        asset.setStatus(Asset.AssetStatus.MAINTENANCE);
        return assetRepository.save(asset);
    }

    public void delete(String id) {
        assetRepository.deleteById(id);
    }
}
