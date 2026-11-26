package com.campus.lostfound.repository;

import com.campus.lostfound.model.Claim;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ClaimRepository extends JpaRepository<Claim, Long> {
    List<Claim> findByItemIdOrderByCreatedAtDesc(Long itemId);
    List<Claim> findByClaimantEmailOrderByCreatedAtDesc(String claimantEmail);
    List<Claim> findByStatusOrderByCreatedAtDesc(String status);
    List<Claim> findAllByOrderByCreatedAtDesc();
    Optional<Claim> findByClaimCode(String claimCode);
    long countByStatus(String status);
}
