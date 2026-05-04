package com.cgi.tracker.model;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;

@Entity
@Table(name = "asset")
public class Asset {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @NotBlank(message = "Serial number is required")
    @Column(name = "serial_number", unique = true, nullable = false)
    private String serialNumber;

    @NotBlank(message = "Asset name is required")
    @Column(name = "name", nullable = false)
    private String name;

    @Enumerated(EnumType.STRING)
    @Column(name = "category")
    private AssetCategory category;

    @Enumerated(EnumType.STRING)
    @Column(name = "status")
    private AssetStatus status = AssetStatus.UNASSIGNED;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "assigned_to_id")
    @JsonBackReference
    private Personnel assignedTo;

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
    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getName() { return name; }
    public void setName(String name) { this.name = name; }
    public AssetCategory getCategory() { return category; }
    public void setCategory(AssetCategory category) { this.category = category; }
    public AssetStatus getStatus() { return status; }
    public void setStatus(AssetStatus status) { this.status = status; }
    public Personnel getAssignedTo() { return assignedTo; }
    public void setAssignedTo(Personnel assignedTo) { this.assignedTo = assignedTo; }

    // Convenience: return just the assigned person's ID in JSON
    public Long getAssignedToId() {
        return assignedTo != null ? assignedTo.getId() : null;
    }
}
