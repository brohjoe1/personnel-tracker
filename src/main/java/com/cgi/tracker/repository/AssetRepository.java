package com.cgi.tracker.repository;

import com.cgi.tracker.model.Asset;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.Optional;

@Repository
public interface AssetRepository extends JpaRepository<Asset, Long> {

    Optional<Asset> findBySerialNumber(String serialNumber);

    List<Asset> findByStatus(Asset.AssetStatus status);

    List<Asset> findByCategory(Asset.AssetCategory category);

    List<Asset> findByAssignedToId(Long personnelId);

    List<Asset> findByAssignedToIsNull();
}
