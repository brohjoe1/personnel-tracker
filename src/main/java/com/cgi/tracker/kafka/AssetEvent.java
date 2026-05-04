package com.cgi.tracker.kafka;

import java.time.LocalDateTime;

public class AssetEvent {

    private String assetId;
    private String serialNumber;
    private String assetName;
    private String action;
    private String personnelId;
    private String personnelName;
    private String timestamp;

    public AssetEvent() {}

    public AssetEvent(String assetId, String serialNumber, String assetName,
                      String action, String personnelId, String personnelName) {
        this.assetId = assetId;
        this.serialNumber = serialNumber;
        this.assetName = assetName;
        this.action = action;
        this.personnelId = personnelId;
        this.personnelName = personnelName;
        this.timestamp = LocalDateTime.now().toString();
    }

    public String getAssetId() { return assetId; }
    public void setAssetId(String assetId) { this.assetId = assetId; }
    public String getSerialNumber() { return serialNumber; }
    public void setSerialNumber(String serialNumber) { this.serialNumber = serialNumber; }
    public String getAssetName() { return assetName; }
    public void setAssetName(String assetName) { this.assetName = assetName; }
    public String getAction() { return action; }
    public void setAction(String action) { this.action = action; }
    public String getPersonnelId() { return personnelId; }
    public void setPersonnelId(String personnelId) { this.personnelId = personnelId; }
    public String getPersonnelName() { return personnelName; }
    public void setPersonnelName(String personnelName) { this.personnelName = personnelName; }
    public String getTimestamp() { return timestamp; }
    public void setTimestamp(String timestamp) { this.timestamp = timestamp; }
}
