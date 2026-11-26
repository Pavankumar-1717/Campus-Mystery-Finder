package com.campus.lostfound.service;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.*;

@Service
public class ItemService {

    private final ItemRepository repository;
    private final Path uploadDir;

    public ItemService(ItemRepository repository, @Value("${app.upload.dir:uploads}") String uploadPath) {
        this.repository = repository;
        this.uploadDir = Paths.get(uploadPath);
        try {
            Files.createDirectories(uploadDir);
        } catch (IOException e) {
            throw new IllegalStateException("Failed to create upload directory at " + uploadPath, e);
        }
    }

    public Item saveItem(Item item, MultipartFile image, String cameraData, String reporterEmail) throws IOException {
        if (reporterEmail != null && !reporterEmail.isBlank()) {
            item.setReportedByEmail(reporterEmail.trim().toLowerCase());
        }

        // Handle multipart file upload (gallery or standard file upload)
        if (image != null && !image.isEmpty()) {
            String contentType = image.getContentType();
            if (contentType == null || !contentType.startsWith("image/")) {
                throw new IllegalArgumentException("Only image files are permitted (JPEG, PNG, WebP).");
            }
            if (image.getSize() > 20 * 1024 * 1024) {
                throw new IllegalArgumentException("Image exceeds the maximum allowed size of 20 MB.");
            }
            String originalName = image.getOriginalFilename();
            String ext = (originalName != null && originalName.contains(".")) ?
                originalName.substring(originalName.lastIndexOf(".")) : ".jpg";
            String safeName = "IMG_" + UUID.randomUUID().toString().substring(0, 12) + ext;
            Files.copy(image.getInputStream(), uploadDir.resolve(safeName), StandardCopyOption.REPLACE_EXISTING);
            item.setImageName(safeName);
        }
        // Handle base64 camera snapshot (taken live from the browser camera viewfinder)
        else if (cameraData != null && cameraData.startsWith("data:image/")) {
            String safeName = saveBase64Image(cameraData);
            item.setImageName(safeName);
        }

        return repository.save(item);
    }

    private String saveBase64Image(String dataUrl) throws IOException {
        int commaIndex = dataUrl.indexOf(",");
        if (commaIndex == -1) return null;
        String metadata = dataUrl.substring(0, commaIndex);
        String base64Data = dataUrl.substring(commaIndex + 1);

        String ext = ".jpg";
        if (metadata.contains("png")) ext = ".png";
        else if (metadata.contains("webp")) ext = ".webp";

        byte[] decodedBytes = Base64.getDecoder().decode(base64Data);
        String fileName = "CAM_" + UUID.randomUUID().toString().substring(0, 12) + ext;
        Files.write(uploadDir.resolve(fileName), decodedBytes, StandardOpenOption.CREATE, StandardOpenOption.TRUNCATE_EXISTING);
        return fileName;
    }

    public List<Item> searchFoundItems(String query, String category) {
        List<Item> items = repository.findByReportTypeAndStatusOrderByCreatedAtDesc("FOUND", "OPEN");

        if (query != null && !query.isBlank()) {
            String q = query.trim().toLowerCase();
            items = items.stream().filter(item ->
                (item.getName() != null && item.getName().toLowerCase().contains(q)) ||
                (item.getCategory() != null && item.getCategory().toLowerCase().contains(q)) ||
                (item.getColour() != null && item.getColour().toLowerCase().contains(q)) ||
                (item.getBrand() != null && item.getBrand().toLowerCase().contains(q))
            ).toList();
        }

        if (category != null && !category.isBlank() && !"ALL".equalsIgnoreCase(category)) {
            items = items.stream().filter(item ->
                category.equalsIgnoreCase(item.getCategory())
            ).toList();
        }

        return items;
    }

    public List<Item> getRecent(String type, int limit) {
        return repository.findByReportTypeAndStatusOrderByCreatedAtDesc(type, "OPEN")
            .stream().limit(limit).toList();
    }

    public List<Item> getByReporter(String email) {
        if (email == null) return Collections.emptyList();
        return repository.findByReportedByEmailOrderByCreatedAtDesc(email.trim().toLowerCase());
    }

    public Optional<Item> findById(Long id) {
        return repository.findById(id);
    }

    public Optional<Item> findByCode(String code) {
        return repository.findByItemCode(code);
    }

    public List<Item> findAll() {
        return repository.findAll();
    }

    public void updateStatus(Long itemId, String newStatus) {
        repository.findById(itemId).ifPresent(item -> {
            item.setStatus(newStatus);
            repository.save(item);
        });
    }

    public void deleteItem(Long itemId) {
        repository.deleteById(itemId);
    }

    public long countFound() {
        return repository.countByReportType("FOUND");
    }

    public long countLost() {
        return repository.countByReportType("LOST");
    }

    public long countOpen() {
        return repository.countByStatus("OPEN");
    }

    public long countClaimed() {
        return repository.countByStatus("CLAIMED");
    }
}
