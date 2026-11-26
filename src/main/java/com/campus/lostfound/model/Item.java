package com.campus.lostfound.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "items")
public class Item {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String itemCode = "CMF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @NotBlank
    @Column(nullable = false)
    private String name;

    private String colour;

    private String size; // "Small", "Medium", "Big"

    private Boolean isElectronic = false;

    private String category; // "Electronics", "Bottles", "Stationery", "Accessories", "Bags", "Clothing", "Other"

    private String location;

    private String brand;

    @Column(length = 2000)
    private String description;

    @Column(length = 1000)
    private String secretClue; // Hidden detail for mystery verification (e.g. sticker, initials, scratches)

    private String imageName;

    @Column(nullable = false)
    private String reportType = "FOUND"; // "LOST" or "FOUND"

    @Column(nullable = false)
    private String status = "OPEN"; // "OPEN", "CLAIMED", "RESOLVED"

    private String reportedByEmail;

    private LocalDateTime eventDate;

    private LocalDateTime createdAt = LocalDateTime.now();

    public Item() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getItemCode() { return itemCode; }
    public void setItemCode(String itemCode) { this.itemCode = itemCode; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getColour() { return colour; }
    public void setColour(String colour) { this.colour = colour; }

    public String getSize() { return size; }
    public void setSize(String size) { this.size = size; }

    public Boolean getIsElectronic() { return isElectronic; }
    public void setIsElectronic(Boolean isElectronic) { this.isElectronic = isElectronic; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public String getLocation() { return location; }
    public void setLocation(String location) { this.location = location; }

    public String getBrand() { return brand; }
    public void setBrand(String brand) { this.brand = brand; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public String getSecretClue() { return secretClue; }
    public void setSecretClue(String secretClue) { this.secretClue = secretClue; }

    public String getImageName() { return imageName; }
    public void setImageName(String imageName) { this.imageName = imageName; }

    public String getReportType() { return reportType; }
    public void setReportType(String reportType) { this.reportType = reportType; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getReportedByEmail() { return reportedByEmail; }
    public void setReportedByEmail(String reportedByEmail) { this.reportedByEmail = reportedByEmail; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }
}
