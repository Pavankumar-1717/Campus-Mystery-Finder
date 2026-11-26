package com.campus.lostfound.model;

import jakarta.persistence.*;
import java.time.LocalDateTime;
import java.util.UUID;

@Entity
@Table(name = "claims")
public class Claim {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String claimCode = "CLM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();

    @Column(nullable = false)
    private Long itemId;

    private String itemName;

    @Column(nullable = false)
    private String claimantEmail;

    private String claimantName;
    private String claimantPhone;

    // The answers provided by claimant (Conditions 4 & 5)
    private String claimLocation; // Where it was lost
    private String claimColour;   // What color
    private String claimSize;     // Small, Medium, Big
    private Boolean claimIsElectronic = false; // Is it electronic or what
    private String claimBrand;

    @Column(length = 2000)
    private String secretAnswer; // Specific unique markings, secret clues

    @Column(length = 2000)
    private String additionalProof;

    private String proofImageName;

    private int matchScore = 0; // Calculated match percentage (0 - 100)

    @Column(nullable = false)
    private String status = "PENDING"; // "PENDING", "APPROVED", "REJECTED"

    private String claimPassCode; // Generated pass code for campus security desk pickup

    @Column(length = 1000)
    private String adminRemarks;

    private LocalDateTime createdAt = LocalDateTime.now();
    private LocalDateTime reviewedAt;

    public Claim() {}

    public Long getId() { return id; }
    public void setId(Long id) { this.id = id; }

    public String getClaimCode() { return claimCode; }
    public void setClaimCode(String claimCode) { this.claimCode = claimCode; }

    public Long getItemId() { return itemId; }
    public void setItemId(Long itemId) { this.itemId = itemId; }

    public String getItemName() { return itemName; }
    public void setItemName(String itemName) { this.itemName = itemName; }

    public String getClaimantEmail() { return claimantEmail; }
    public void setClaimantEmail(String claimantEmail) { this.claimantEmail = claimantEmail; }

    public String getClaimantName() { return claimantName; }
    public void setClaimantName(String claimantName) { this.claimantName = claimantName; }

    public String getClaimantPhone() { return claimantPhone; }
    public void setClaimantPhone(String claimantPhone) { this.claimantPhone = claimantPhone; }

    public String getClaimLocation() { return claimLocation; }
    public void setClaimLocation(String claimLocation) { this.claimLocation = claimLocation; }

    public String getClaimColour() { return claimColour; }
    public void setClaimColour(String claimColour) { this.claimColour = claimColour; }

    public String getClaimSize() { return claimSize; }
    public void setClaimSize(String claimSize) { this.claimSize = claimSize; }

    public Boolean getClaimIsElectronic() { return claimIsElectronic; }
    public void setClaimIsElectronic(Boolean claimIsElectronic) { this.claimIsElectronic = claimIsElectronic; }

    public String getClaimBrand() { return claimBrand; }
    public void setClaimBrand(String claimBrand) { this.claimBrand = claimBrand; }

    public String getSecretAnswer() { return secretAnswer; }
    public void setSecretAnswer(String secretAnswer) { this.secretAnswer = secretAnswer; }

    public String getAdditionalProof() { return additionalProof; }
    public void setAdditionalProof(String additionalProof) { this.additionalProof = additionalProof; }

    public String getProofImageName() { return proofImageName; }
    public void setProofImageName(String proofImageName) { this.proofImageName = proofImageName; }

    public int getMatchScore() { return matchScore; }
    public void setMatchScore(int matchScore) { this.matchScore = matchScore; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getClaimPassCode() { return claimPassCode; }
    public void setClaimPassCode(String claimPassCode) { this.claimPassCode = claimPassCode; }

    public String getAdminRemarks() { return adminRemarks; }
    public void setAdminRemarks(String adminRemarks) { this.adminRemarks = adminRemarks; }

    public LocalDateTime getCreatedAt() { return createdAt; }
    public void setCreatedAt(LocalDateTime createdAt) { this.createdAt = createdAt; }

    public LocalDateTime getReviewedAt() { return reviewedAt; }
    public void setReviewedAt(LocalDateTime reviewedAt) { this.reviewedAt = reviewedAt; }
}
