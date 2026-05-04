package com.cgi.tracker.model;

import com.fasterxml.jackson.annotation.JsonManagedReference;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.Indexed;
import org.springframework.data.mongodb.core.mapping.Document;
import java.util.ArrayList;
import java.util.List;

@Document(collection = "personnel")
public class Personnel {

    @Id
    private String id;

    @NotBlank(message = "First name is required")
    private String firstName;

    @NotBlank(message = "Last name is required")
    private String lastName;

    private String rank;

    private String unit;

    @Email(message = "Valid email required")
    @Indexed(unique = true)
    private String email;

    private boolean active = true;

    // Assets stored as embedded documents within the personnel document
    private List<Asset> assets = new ArrayList<>();

    // Constructors
    public Personnel() {}

    public Personnel(String firstName, String lastName, String rank, String unit, String email) {
        this.firstName = firstName;
        this.lastName = lastName;
        this.rank = rank;
        this.unit = unit;
        this.email = email;
    }

    // Getters and Setters
    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getFirstName() { return firstName; }
    public void setFirstName(String firstName) { this.firstName = firstName; }
    public String getLastName() { return lastName; }
    public void setLastName(String lastName) { this.lastName = lastName; }
    public String getRank() { return rank; }
    public void setRank(String rank) { this.rank = rank; }
    public String getUnit() { return unit; }
    public void setUnit(String unit) { this.unit = unit; }
    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }
    public boolean isActive() { return active; }
    public void setActive(boolean active) { this.active = active; }
    public List<Asset> getAssets() { return assets; }
    public void setAssets(List<Asset> assets) { this.assets = assets; }
}
