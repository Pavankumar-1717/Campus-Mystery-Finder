package com.campus.lostfound.controller;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;

@Controller
@RequestMapping("/claim")
public class ClaimController {

    private final ClaimService claimService;
    private final ItemService itemService;

    public ClaimController(ClaimService claimService, ItemService itemService) {
        this.claimService = claimService;
        this.itemService = itemService;
    }

    /**
     * Conditions 5 & 6: Ask the lost person about the object details
     * (where lost, what colour, small or big, is it electronic or what, unique markers)
     */
    @GetMapping("/{itemId}")
    public String claimVerificationForm(@PathVariable Long itemId, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        Item item = itemService.findById(itemId).orElse(null);
        if (item == null || !"OPEN".equalsIgnoreCase(item.getStatus())) {
            return "redirect:/search";
        }

        Claim claim = new Claim();
        claim.setItemId(item.getId());
        claim.setItemName(item.getName());
        claim.setClaimantName(currentUser.getFullName());
        claim.setClaimantEmail(currentUser.getEmail());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("item", item);
        model.addAttribute("claim", claim);
        return "claim";
    }

    /**
     * Processes claimant's verification answers, scores match, stores claim for admin
     */
    @PostMapping("/{itemId}")
    public String processClaim(@PathVariable Long itemId,
                               @ModelAttribute Claim claim,
                               @RequestParam(value = "proofImage", required = false) MultipartFile proofImage,
                               HttpSession session,
                               RedirectAttributes redirect) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        try {
            Claim savedClaim = claimService.submitClaim(claim, proofImage, itemId, currentUser.getEmail());
            int score = savedClaim.getMatchScore();

            String message;
            if (score >= 75) {
                message = "Verification submitted! High match score (" + score + "%). Your claim has been prioritized for admin approval.";
            } else if (score >= 45) {
                message = "Verification submitted! Moderate match score (" + score + "%). The administrator will review your answers against the object.";
            } else {
                message = "Verification submitted! Match score: " + score + "%. The administrator will review your answers.";
            }

            redirect.addFlashAttribute("success", message);
            return "redirect:/student/dashboard";
        } catch (IllegalArgumentException | IOException ex) {
            redirect.addFlashAttribute("error", "Failed to submit claim: " + ex.getMessage());
            return "redirect:/claim/" + itemId;
        }
    }
}
