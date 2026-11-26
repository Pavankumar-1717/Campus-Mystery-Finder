package com.campus.lostfound.service;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import org.springframework.stereotype.Service;

import java.util.*;

@Service
public class MatchingEngineService {

    public static class MatchBreakdown {
        private final int totalScore;
        private final String confidenceLevel; // "HIGH", "MODERATE", "LOW"
        private final Map<String, Integer> categoryScores = new LinkedHashMap<>();
        private final List<String> matchNotes = new ArrayList<>();

        public MatchBreakdown(int totalScore, String confidenceLevel) {
            this.totalScore = totalScore;
            this.confidenceLevel = confidenceLevel;
        }

        public int getTotalScore() { return totalScore; }
        public String getConfidenceLevel() { return confidenceLevel; }
        public Map<String, Integer> getCategoryScores() { return categoryScores; }
        public List<String> getMatchNotes() { return matchNotes; }
    }

    /**
     * Compares the claimant's answers with the found item's details.
     * Evaluates:
     * 1. Colour (20 pts)
     * 2. Size (15 pts)
     * 3. Electronic Type (15 pts)
     * 4. Location Proximity (20 pts)
     * 5. Brand (10 pts)
     * 6. Secret Clue / Unique Markings (20 pts)
     * Total possible score = 100 pts.
     */
    public MatchBreakdown evaluateClaim(Claim claim, Item foundItem) {
        int score = 0;
        MatchBreakdown breakdown = new MatchBreakdown(0, "LOW");

        // 1. Colour Matching (20 points max)
        int colourScore = evaluateColour(claim.getClaimColour(), foundItem.getColour());
        score += colourScore;
        breakdown.getCategoryScores().put("Colour Match", colourScore);
        if (colourScore >= 15) breakdown.getMatchNotes().add("Color matched closely (" + foundItem.getColour() + ").");

        // 2. Size Matching (15 points max)
        int sizeScore = evaluateSize(claim.getClaimSize(), foundItem.getSize());
        score += sizeScore;
        breakdown.getCategoryScores().put("Size Match", sizeScore);
        if (sizeScore > 0) breakdown.getMatchNotes().add("Size classification aligned (" + foundItem.getSize() + ").");

        // 3. Electronic Type Matching (15 points max)
        int electronicScore = evaluateElectronic(claim.getClaimIsElectronic(), foundItem.getIsElectronic());
        score += electronicScore;
        breakdown.getCategoryScores().put("Electronic Type", electronicScore);
        if (electronicScore > 0) breakdown.getMatchNotes().add("Electronic classification matched (" + (foundItem.getIsElectronic() ? "Electronic" : "Non-electronic") + ").");

        // 4. Location Proximity (20 points max)
        int locationScore = evaluateLocation(claim.getClaimLocation(), foundItem.getLocation());
        score += locationScore;
        breakdown.getCategoryScores().put("Location Proximity", locationScore);
        if (locationScore >= 10) breakdown.getMatchNotes().add("Location proximity confirmed near " + foundItem.getLocation() + ".");

        // 5. Brand Matching (10 points max)
        int brandScore = evaluateText(claim.getClaimBrand(), foundItem.getBrand(), 10);
        score += brandScore;
        breakdown.getCategoryScores().put("Brand Match", brandScore);

        // 6. Secret Clue / Unique Marks Verification (20 points max)
        int secretScore = evaluateSecretClue(claim.getSecretAnswer(), foundItem.getSecretClue());
        score += secretScore;
        breakdown.getCategoryScores().put("Secret Clues & Unique Marks", secretScore);
        if (secretScore >= 12) breakdown.getMatchNotes().add("Secret identifier/marking strongly confirmed!");

        // Normalize score between 0 and 100
        int finalScore = Math.min(100, Math.max(0, score));

        String confidenceLevel;
        if (finalScore >= 75) {
            confidenceLevel = "HIGH";
        } else if (finalScore >= 45) {
            confidenceLevel = "MODERATE";
        } else {
            confidenceLevel = "LOW";
        }

        MatchBreakdown finalBreakdown = new MatchBreakdown(finalScore, confidenceLevel);
        finalBreakdown.getCategoryScores().putAll(breakdown.getCategoryScores());
        finalBreakdown.getMatchNotes().addAll(breakdown.getMatchNotes());
        return finalBreakdown;
    }

    private int evaluateColour(String claimColour, String itemColour) {
        if (claimColour == null || itemColour == null) return 0;
        String c1 = claimColour.trim().toLowerCase();
        String c2 = itemColour.trim().toLowerCase();
        if (c1.equals(c2)) return 20;
        if (c1.contains(c2) || c2.contains(c1)) return 15;
        // Check for common color sub-words
        String[] words1 = c1.split("\\s+");
        for (String w : words1) {
            if (w.length() > 2 && c2.contains(w)) return 12;
        }
        return 0;
    }

    private int evaluateSize(String claimSize, String itemSize) {
        if (claimSize == null || itemSize == null) return 0;
        return claimSize.trim().equalsIgnoreCase(itemSize.trim()) ? 15 : 0;
    }

    private int evaluateElectronic(Boolean claimIsElectronic, Boolean itemIsElectronic) {
        if (claimIsElectronic == null || itemIsElectronic == null) return 5;
        return claimIsElectronic.equals(itemIsElectronic) ? 15 : 0;
    }

    private int evaluateLocation(String claimLoc, String itemLoc) {
        if (claimLoc == null || itemLoc == null) return 0;
        String l1 = claimLoc.trim().toLowerCase();
        String l2 = itemLoc.trim().toLowerCase();
        if (l1.equals(l2)) return 20;
        if (l1.contains(l2) || l2.contains(l1)) return 15;

        // Check for keyword intersection (e.g. "Library", "Canteen", "Block A")
        String[] tokens = l1.split("[\\s,.-]+");
        int matches = 0;
        for (String t : tokens) {
            if (t.length() > 3 && l2.contains(t)) matches++;
        }
        return matches > 0 ? Math.min(15, matches * 8) : 0;
    }

    private int evaluateText(String s1, String s2, int maxScore) {
        if (s1 == null || s2 == null) return 0;
        String str1 = s1.trim().toLowerCase();
        String str2 = s2.trim().toLowerCase();
        if (str1.isEmpty() || str2.isEmpty()) return 0;
        if (str1.equals(str2)) return maxScore;
        if (str1.contains(str2) || str2.contains(str1)) return (int) (maxScore * 0.8);
        return 0;
    }

    private int evaluateSecretClue(String claimantAnswer, String finderSecret) {
        if (claimantAnswer == null || finderSecret == null) return 0;
        String a = claimantAnswer.trim().toLowerCase();
        String f = finderSecret.trim().toLowerCase();
        if (a.isEmpty() || f.isEmpty()) return 0;

        if (a.equals(f)) return 20;
        if (a.contains(f) || f.contains(a)) return 16;

        // Keyword overlap
        String[] wordsA = a.split("[\\s,.-]+");
        int matchedWords = 0;
        for (String w : wordsA) {
            if (w.length() > 2 && f.contains(w)) {
                matchedWords++;
            }
        }
        return Math.min(20, matchedWords * 7);
    }
}
