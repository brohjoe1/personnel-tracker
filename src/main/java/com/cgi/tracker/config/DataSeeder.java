package com.cgi.tracker.config;

import com.cgi.tracker.model.Asset;
import com.cgi.tracker.model.Personnel;
import com.cgi.tracker.repository.AssetRepository;
import com.cgi.tracker.repository.PersonnelRepository;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DataSeeder {

    private static final Logger log = LoggerFactory.getLogger(DataSeeder.class);

    @Bean
    CommandLineRunner seedData(PersonnelRepository personnelRepo,
                               AssetRepository assetRepo) {
        return args -> {
            if (personnelRepo.count() > 0) {
                log.info("Database already seeded, skipping.");
                return;
            }

            log.info("Seeding database...");

            Personnel mitchell = personnelRepo.save(new Personnel(
                    "James", "Mitchell", "SSG", "1st Logistics Battalion", "j.mitchell@army.mil"));
            Personnel torres = personnelRepo.save(new Personnel(
                    "Sandra", "Torres", "CPT", "HHC 3rd Brigade", "s.torres@army.mil"));
            Personnel wallace = personnelRepo.save(new Personnel(
                    "Kevin", "Wallace", "SPC", "204th Support Company", "k.wallace@army.mil"));
            Personnel chen = personnelRepo.save(new Personnel(
                    "Diana", "Chen", "LTC", "G4 Division", "d.chen@army.mil"));

            assetRepo.save(createAsset("SN-M4-00142", "M4 Carbine",
                    Asset.AssetCategory.WEAPON, Asset.AssetStatus.ASSIGNED,
                    mitchell.getId(), "James Mitchell"));
            assetRepo.save(createAsset("SN-HMMWV-0887", "HMMWV",
                    Asset.AssetCategory.VEHICLE, Asset.AssetStatus.ASSIGNED,
                    torres.getId(), "Sandra Torres"));
            assetRepo.save(createAsset("SN-PRC152-0034", "AN/PRC-152 Radio",
                    Asset.AssetCategory.COMMS, Asset.AssetStatus.ASSIGNED,
                    mitchell.getId(), "James Mitchell"));
            assetRepo.save(createAsset("SN-LAPTOP-2201", "Toughbook CF-33",
                    Asset.AssetCategory.EQUIPMENT, Asset.AssetStatus.ASSIGNED,
                    chen.getId(), "Diana Chen"));
            assetRepo.save(createAsset("SN-M4-00199", "M4 Carbine",
                    Asset.AssetCategory.WEAPON, Asset.AssetStatus.UNASSIGNED,
                    null, null));
            assetRepo.save(createAsset("SN-GEN-0055", "Generator 5KW",
                    Asset.AssetCategory.EQUIPMENT, Asset.AssetStatus.MAINTENANCE,
                    null, null));

            log.info("Database seeded: {} personnel, {} assets",
                    personnelRepo.count(), assetRepo.count());
        };
    }

    private Asset createAsset(String serial, String name, Asset.AssetCategory category,
                               Asset.AssetStatus status, String assignedToId,
                               String assignedToName) {
        Asset asset = new Asset();
        asset.setSerialNumber(serial);
        asset.setName(name);
        asset.setCategory(category);
        asset.setStatus(status);
        asset.setAssignedToId(assignedToId);
        asset.setAssignedToName(assignedToName);
        return asset;
    }
}
