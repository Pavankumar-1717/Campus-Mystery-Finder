package com.campus.lostfound.service;

import com.campus.lostfound.model.User;
import com.campus.lostfound.repository.UserRepository;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Optional;

@Service
public class UserService {

    private final UserRepository userRepository;
    private static final String SALT = "CampusMysteryFinderSecretSalt2026";

    public UserService(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    /**
     * Authenticates a student via email.
     * If the student does not exist, registers them automatically.
     */
    public User authenticateStudent(String email, String fullName) {
        String cleanEmail = email.trim().toLowerCase();
        return userRepository.findByEmailIgnoreCase(cleanEmail)
            .map(user -> {
                if (fullName != null && !fullName.isBlank() && (user.getFullName() == null || user.getFullName().isBlank())) {
                    user.setFullName(fullName.trim());
                    userRepository.save(user);
                }
                return user;
            })
            .orElseGet(() -> {
                String displayName = (fullName != null && !fullName.isBlank()) ? fullName.trim() : cleanEmail.split("@")[0];
                User newStudent = new User(cleanEmail, displayName, "STUDENT", null);
                return userRepository.save(newStudent);
            });
    }

    /**
     * Authenticates an admin using email and password.
     */
    public Optional<User> authenticateAdmin(String email, String password) {
        String cleanEmail = email.trim().toLowerCase();
        Optional<User> userOpt = userRepository.findByEmailIgnoreCase(cleanEmail);
        if (userOpt.isPresent()) {
            User user = userOpt.get();
            if (user.isAdmin() && user.getPasswordHash() != null) {
                String hash = hashPassword(password);
                if (hash.equals(user.getPasswordHash())) {
                    return Optional.of(user);
                }
            }
        }
        return Optional.empty();
    }

    /**
     * Registers a new admin or updates an existing admin's password.
     */
    public User registerOrUpdateAdmin(String email, String fullName, String password) {
        String cleanEmail = email.trim().toLowerCase();
        String hash = hashPassword(password);
        User admin = userRepository.findByEmailIgnoreCase(cleanEmail)
            .orElse(new User());

        admin.setEmail(cleanEmail);
        admin.setFullName((fullName != null && !fullName.isBlank()) ? fullName.trim() : "Campus Administrator");
        admin.setRole("ADMIN");
        admin.setPasswordHash(hash);
        return userRepository.save(admin);
    }

    public boolean adminExists() {
        return !userRepository.findByRole("ADMIN").isEmpty();
    }

    public Optional<User> findByEmail(String email) {
        return userRepository.findByEmailIgnoreCase(email);
    }

    public static String hashPassword(String password) {
        try {
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedHash = digest.digest((SALT + password).getBytes(StandardCharsets.UTF_8));
            StringBuilder hexString = new StringBuilder(2 * encodedHash.length);
            for (byte b : encodedHash) {
                String hex = Integer.toHexString(0xff & b);
                if (hex.length() == 1) {
                    hexString.append('0');
                }
                hexString.append(hex);
            }
            return hexString.toString();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException("SHA-256 algorithm not available", e);
        }
    }
}
