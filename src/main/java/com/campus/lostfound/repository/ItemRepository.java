package com.campus.lostfound.repository;

import com.campus.lostfound.model.Item;
import org.springframework.data.jpa.repository.JpaRepository;
import java.util.List;
import java.util.Optional;

public interface ItemRepository extends JpaRepository<Item, Long> {
    List<Item> findByReportTypeAndStatusOrderByCreatedAtDesc(String reportType, String status);
    List<Item> findByReportTypeOrderByCreatedAtDesc(String reportType);
    List<Item> findByReportTypeAndStatusAndNameContainingIgnoreCaseOrderByCreatedAtDesc(
        String reportType, String status, String name);
    List<Item> findByReportedByEmailOrderByCreatedAtDesc(String email);
    Optional<Item> findByItemCode(String itemCode);
    long countByReportType(String reportType);
    long countByStatus(String status);
}
