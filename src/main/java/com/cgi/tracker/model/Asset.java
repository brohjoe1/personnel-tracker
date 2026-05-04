package com.cgi.tracker.model;

import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;

@Document(collection = "assets")
public class Asset {

    @Id
    private String id;

    @NotBlank(message = "Serial number is required")
    @Indexed(unique = true)
    private String serialNumber;

    @NotBlank(message = "Asset name is required")
    private String name;

    private AssetCategory category;

    private AssetStatus status = AssetStatus.UNASSIGNED;

    // Reference to personnel by ID (String)
    private String assignedToId;
    private String assignedToName; // Denormalized for convenience

    // Enums
    public enum AssetCategory {
        WEAPON, VEHICLE, COMMS, EQUIPMENT
    }

    public enum AssetStatus {
        ASSIGNED, UNASSIGNED, MAINTENANCE, DECOMMISSIONED
    }

    // Constructors
    public Asset() {}

    public Asset(String serialNumber, String name, AssetCategory category) {
        this.serialNumber = serialNumber;
        this.name = name;
        this.category = category;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AssetCategory getCategory() { return category; }
    public void setCategory(AssetCategory category) { this.category = category; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public String getAssignedToId() { return assignedToId; }
    public void setAssignedToId(String assignedToId) { this.assignedToId = assignedToId; }
    public String getAssignedToName() { return assignedToName; }
    public void setAssignedToName(String assignedToName) { this.assignedToName = assignedToName; }
}
