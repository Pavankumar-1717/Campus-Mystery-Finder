package com.campus.lostfound.controller;

import com.campus.lostfound.model.Claim;
import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.ClaimService;
import com.campus.lostfound.service.ItemService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import java.util.List;

@Controller
public class HomeController {

    private final ItemService itemService;
    private final ClaimService claimService;

    public HomeController(ItemService itemService, ClaimService claimService) {
        this.itemService = itemService;
        this.claimService = claimService;
    }

    /**
     * Condition 1: Once logged in, starts with "Did you lose or find something in campus?"
     * if (found == report found with camera/details)
     * if (lost == search & verification claim)
     */
    @GetMapping("/")
    public String home(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("recentFound", itemService.getRecent("FOUND", 6));
        model.addAttribute("recentLost", itemService.getRecent("LOST", 4));
        model.addAttribute("totalFound", itemService.countFound());
        model.addAttribute("totalLost", itemService.countLost());
        return "index";
    }

    /**
     * Student Dashboard: View my reports, track my submitted claims and Claim Pass codes
     */
    @GetMapping("/student/dashboard")
    public String studentDashboard(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        if (currentUser == null) return "redirect:/login";

        List<Item> myItems = itemService.getByReporter(currentUser.getEmail());
        List<Claim> myClaims = claimService.getClaimsByClaimant(currentUser.getEmail());

        model.addAttribute("currentUser", currentUser);
        model.addAttribute("myItems", myItems);
        model.addAttribute("myClaims", myClaims);
        return "student_dashboard";
    }
}
