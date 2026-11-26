package com.campus.lostfound.service;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.repository.ClaimRepository;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class ClaimService {

    private final ClaimRepository claimRepository;
    private final ItemRepository itemRepository;
    private final MatchingEngineService matchingEngine;
    private final Path uploadDir;

    public ClaimService(ClaimRepository claimRepository,
                        ItemRepository itemRepository,
                        MatchingEngineService matchingEngine,
                        @Value("${app.upload.dir:uploads}") String uploadPath) {
        this.claimRepository = claimRepository;
        this.itemRepository = itemRepository;
        this.matchingEngine = matchingEngine;
        this.uploadDir = Paths.get(uploadPath);
    }

    public Claim submitClaim(Claim claim, MultipartFile proofImage, Long itemId, String claimantEmail) throws IOException {
        Item item = itemRepository.findById(itemId)
            .orElseThrow(() -> new IllegalArgumentException("Item not found."));

        claim.setItemId(item.getId());
        claim.setItemName(item.getName());
        claim.setClaimantEmail(claimantEmail.trim().toLowerCase());

        // Handle proof image if provided
        if (proofImage != null && !proofImage.isEmpty()) {
            String originalName = proofImage.getOriginalFilename();
            String ext = (originalName != null && originalName.contains(".")) ?
                originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String safeName = "PROOF_" + UUID.randomUUID().toString().substring(0, 10) + ext;
            Files.copy(proofImage.getInputStream(), uploadDir.resolve(safeName), StandardCopyOption.REPLACE_EXISTING);
            claim.setProofImageName(safeName);
        }

        // Run smart verification match engine
        MatchingEngineService.MatchBreakdown breakdown = matchingEngine.evaluateClaim(claim, item);
        claim.setMatchScore(breakdown.getTotalScore());
        claim.setStatus("PENDING");

        return claimRepository.save(claim);
    }

    public List<Claim> getClaimsByClaimant(String email) {
        if (email == null) return Collections.emptyList();
        return claimRepository.findByClaimantEmailOrderByCreatedAtDesc(email.trim().toLowerCase());
    }

    public List<Claim> getPendingClaims() {
        return claimRepository.findByStatusOrderByCreatedAtDesc("PENDING");
    }

    public List<Claim> getAllClaims() {
        return claimRepository.findAllByOrderByCreatedAtDesc();
    }

    public Optional<Claim> findById(Long id) {
        return claimRepository.findById(id);
    }

    public Claim approveClaim(Long claimId, String adminRemarks, String pickupLocation) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found."));

        claim.setStatus("APPROVED");
        claim.setReviewedAt(LocalDateTime.now());

        // Generate distinctive pickup pass code
        String passCode = "PASS-" + UUID.randomUUID().toString().substring(0, 6).toUpperCase();
        claim.setClaimPassCode(passCode);

        String remarks = (adminRemarks != null && !adminRemarks.isBlank()) ? adminRemarks : "Ownership verified by Administrator.";
        if (pickupLocation != null && !pickupLocation.isBlank()) {
            remarks += " Pickup Location: " + pickupLocation;
        } else {
            remarks += " Pickup at Campus Security Office / Admin Desk.";
        }
        claim.setAdminRemarks(remarks);

        // Update target item status
        itemRepository.findById(claim.getItemId()).ifPresent(item -> {
            item.setStatus("CLAIMED");
            itemRepository.save(item);
        });

        return claimRepository.save(claim);
    }

    public Claim rejectClaim(Long claimId, String adminRemarks) {
        Claim claim = claimRepository.findById(claimId)
            .orElseThrow(() -> new IllegalArgumentException("Claim not found."));

        claim.setStatus("REJECTED");
        claim.setReviewedAt(LocalDateTime.now());
        claim.setAdminRemarks((adminRemarks != null && !adminRemarks.isBlank()) ? adminRemarks :
            "The details provided did not sufficiently match the verified item characteristics.");

        return claimRepository.save(claim);
    }

    public long countPending() {
        return claimRepository.countByStatus("PENDING");
    }

    public long countApproved() {
        return claimRepository.countByStatus("APPROVED");
    }
}
