package com.campus.lostfound.controller;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.service.ItemService;
import com.campus.lostfound.service.MatchingEngineService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.*;

@Controller
@RequestMapping("/admin")
public class AdminController {

    private final ItemService itemService;
    private final ClaimService claimService;
    private final MatchingEngineService matchingEngine;

    public AdminController(ItemService itemService,
                           ClaimService claimService,
                           MatchingEngineService matchingEngine) {
        this.itemService = itemService;
        this.claimService = claimService;
        this.matchingEngine = matchingEngine;
    }

    /**
     * Admin Dashboard: Statistics and Claims Review Queue (Conditions 6, 7)
     */
    @GetMapping("/dashboard")
    public String dashboard(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) return "redirect:/login";

        List<Claim> pendingClaims = claimService.getPendingClaims();
        // Pre-fetch items for side-by-side comparison
        Map<Long, Item> itemMap = new HashMap<>();
        Map<Long, MatchingEngineService.MatchBreakdown> breakdownMap = new HashMap<>();

        for (Claim c : pendingClaims) {
            itemService.findById(c.getItemId()).ifPresent(item -> {
                itemMap.put(c.getItemId(), item);
                breakdownMap.put(c.getId(), matchingEngine.evaluateClaim(c, item));
            });
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("totalFound", itemService.countFound());
        model.addAttribute("totalLost", itemService.countLost());
        model.addAttribute("openItems", itemService.countOpen());
        model.addAttribute("claimedItems", itemService.countClaimed());
        model.addAttribute("pendingClaimsCount", claimService.countPending());
        model.addAttribute("approvedClaimsCount", claimService.countApproved());
        model.addAttribute("pendingClaims", pendingClaims);
        model.addAttribute("itemMap", itemMap);
        model.addAttribute("breakdownMap", breakdownMap);

        return "admin_dashboard";
    }

    /**
     * Admin Approve Claim: generates claim pass code and marks item claimed
     */
    @PostMapping("/claim/{claimId}/approve")
    public String approveClaim(@PathVariable Long claimId,
                               @RequestParam(required = false) String adminRemarks,
                               @RequestParam(required = false) String pickupLocation,
                               RedirectAttributes redirect) {
        try {
            Claim approved = claimService.approveClaim(claimId, adminRemarks, pickupLocation);
            redirect.addFlashAttribute("success", "Claim " + approved.getClaimCode() + " APPROVED! Pass Code generated: " + approved.getClaimPassCode());
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error approving claim: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * Admin Reject Claim
     */
    @PostMapping("/claim/{claimId}/reject")
    public String rejectClaim(@PathVariable Long claimId,
                              @RequestParam(required = false) String adminRemarks,
                              RedirectAttributes redirect) {
        try {
            Claim rejected = claimService.rejectClaim(claimId, adminRemarks);
            redirect.addFlashAttribute("success", "Claim " + rejected.getClaimCode() + " rejected.");
        } catch (Exception e) {
            redirect.addFlashAttribute("error", "Error rejecting claim: " + e.getMessage());
        }
        return "redirect:/admin/dashboard";
    }

    /**
     * Manage all reported items
     */
    @GetMapping("/items")
    public String manageItems(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) return "redirect:/login";

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("items", itemService.findAll());
        return "admin_items";
    }

    @PostMapping("/items/{id}/status")
    public String updateItemStatus(@PathVariable Long id,
                                   @RequestParam String status,
                                   RedirectAttributes redirect) {
        itemService.updateStatus(id, status);
        redirect.addFlashAttribute("success", "Item status updated to " + status);
        return "redirect:/admin/items";
    }

    @PostMapping("/items/{id}/delete")
    public String deleteItem(@PathVariable Long id, RedirectAttributes redirect) {
        itemService.deleteItem(id);
        redirect.addFlashAttribute("success", "Item record removed.");
        return "redirect:/admin/items";
    }

    /**
     * View all claims history
     */
    @GetMapping("/claims")
    public String claimsHistory(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null || !currentUser.isAdmin()) return "redirect:/login";

        List<Claim> allClaims = claimService.getAllClaims();
        Map<Long, Item> itemMap = new HashMap<>();
        for (Claim c : allClaims) {
            itemService.findById(c.getItemId()).ifPresent(item -> itemMap.put(c.getItemId(), item));
        }

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("allClaims", allClaims);
        model.addAttribute("itemMap", itemMap);
        return "admin_claims";
    }
}
