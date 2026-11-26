package com.campus.lostfound.controller;

import com.campus.lostfound.model.User;
import com.campus.lostfound.service.UserService;
import jakarta.servlet.http.HttpSession;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Optional;

@Controller
public class AuthController {

    private final UserService userService;
    private static final String ADMIN_SETUP_CODE = "CAMPUS-ADMIN-2026";

    public AuthController(UserService userService) {
        this.userService = userService;
    }

    /**
     * Entry Page: Selection between Student Login and Admin Login (Condition 8)
     */
    @GetMapping("/login")
    public String loginPage(@RequestParam(required = false) String role,
                            @RequestParam(required = false) String tab,
                            Model model, HttpSession session) {
        User user = (User) session.getAttribute("currentUser");
        if (user != null) {
            return user.isAdmin() ? "redirect:/admin/dashboard" : "redirect:/";
        }
        model.addAttribute("selectedRole", role != null ? role.toUpperCase() : "STUDENT");
        model.addAttribute("activeTab", tab != null ? tab : "student");
        return "login";
    }

    /**
     * Student Login with Email (Condition 7)
     */
    @PostMapping("/login/student")
    public String studentLogin(@RequestParam String email,
                               @RequestParam(required = false) String fullName,
                               HttpSession session,
                               RedirectAttributes redirect) {
        if (email == null || email.isBlank() || !email.contains("@")) {
            redirect.addFlashAttribute("error", "Please provide a valid campus student email.");
            return "redirect:/login?role=STUDENT";
        }

        User student = userService.authenticateStudent(email, fullName);
        session.setAttribute("currentUser", student);
        redirect.addFlashAttribute("success", "Welcome, " + student.getFullName() + "! Let's find or report campus items.");
        return "redirect:/";
    }

    /**
     * Admin Login with Email & Password (Condition 7)
     */
    @PostMapping("/login/admin")
    public String adminLogin(@RequestParam String email,
                             @RequestParam String password,
                             HttpSession session,
                             RedirectAttributes redirect) {
        if (email == null || email.isBlank() || password == null || password.isBlank()) {
            redirect.addFlashAttribute("error", "Please enter your admin email and password.");
            return "redirect:/login?role=ADMIN";
        }

        Optional<User> adminOpt = userService.authenticateAdmin(email, password);
        if (adminOpt.isPresent()) {
            session.setAttribute("currentUser", adminOpt.get());
            redirect.addFlashAttribute("success", "Admin access granted. Welcome to Authority Control Center.");
            return "redirect:/admin/dashboard";
        } else {
            redirect.addFlashAttribute("error", "Invalid admin credentials. Please check your email and password, or set up your password below.");
            return "redirect:/login?role=ADMIN";
        }
    }

    /**
     * Admin Password Creation / Setup Page (Condition 7: "admin should have to login with email and want to create a password")
     */
    @GetMapping("/admin/setup")
    public String adminSetupForm(Model model) {
        return "admin_setup";
    }

    @PostMapping("/admin/setup")
    public String processAdminSetup(@RequestParam String email,
                                    @RequestParam String fullName,
                                    @RequestParam String password,
                                    @RequestParam String confirmPassword,
                                    @RequestParam String securityCode,
                                    RedirectAttributes redirect) {
        if (!ADMIN_SETUP_CODE.equals(securityCode.trim())) {
            redirect.addFlashAttribute("error", "Invalid Admin Security Key! Default key is CAMPUS-ADMIN-2026");
            return "redirect:/admin/setup";
        }

        if (!password.equals(confirmPassword)) {
            redirect.addFlashAttribute("error", "Passwords do not match.");
            return "redirect:/admin/setup";
        }

        if (password.length() < 6) {
            redirect.addFlashAttribute("error", "Password must be at least 6 characters long.");
            return "redirect:/admin/setup";
        }

        userService.registerOrUpdateAdmin(email, fullName, password);
        redirect.addFlashAttribute("success", "Admin password successfully created! You can now log in.");
        return "redirect:/login?role=ADMIN";
    }

    @GetMapping("/logout")
    public String logout(HttpSession session, RedirectAttributes redirect) {
        session.invalidate();
        redirect.addFlashAttribute("success", "You have been logged out securely.");
        return "redirect:/login";
    }
}
