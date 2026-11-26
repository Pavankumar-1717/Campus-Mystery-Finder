package com.campus.lostfound.controller;

import com.campus.lostfound.model.Item;
import com.campus.lostfound.model.User;
import com.campus.lostfound.service.ItemService;
import jakarta.servlet.http.HttpSession;
import jakarta.validation.Valid;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.io.IOException;
import java.time.LocalDateTime;

@Controller
public class ItemController {

    private final ItemService itemService;

    public ItemController(ItemService itemService) {
        this.itemService = itemService;
    }

    /**
     * Conditions 2 & 3: Found item report form
     * - Allows camera capture or gallery media import
     * - Description: location where found, colour, size (small/medium/big), is electronic or what, secret clues
     */
    @GetMapping("/report/found")
    public String reportFoundForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("item", new Item());
        model.addAttribute("type", "FOUND");
        return "report_found";
    }

    @PostMapping("/report/found")
    public String submitFound(@Valid @ModelAttribute Item item,
                              BindingResult errors,
                              @RequestParam(value = "image", required = false) MultipartFile image,
                              @RequestParam(value = "cameraData", required = false) String cameraData,
                              HttpSession session,
                              RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            redirect.addFlashAttribute("error", "Please fill in all required fields.");
            return "redirect:/report/found";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String reporterEmail = (currentUser != null) ? currentUser.getEmail() : null;

        item.setReportType("FOUND");
        item.setStatus("OPEN");
        if (item.getEventDate() == null) item.setEventDate(LocalDateTime.now());

        try {
            Item saved = itemService.saveItem(item, image, cameraData, reporterEmail);
            redirect.addFlashAttribute("success", "Found item listed with code " + saved.getItemCode() + "! Thank you for helping your fellow student.");
        } catch (IllegalArgumentException | IOException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/report/found";
        }

        return "redirect:/";
    }

    /**
     * Lost item report form (allows students to log an item they lost so finders can match it)
     */
    @GetMapping("/report/lost")
    public String reportLostForm(Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("item", new Item());
        model.addAttribute("type", "LOST");
        return "report_lost";
    }

    @PostMapping("/report/lost")
    public String submitLost(@Valid @ModelAttribute Item item,
                             BindingResult errors,
                             @RequestParam(value = "image", required = false) MultipartFile image,
                             @RequestParam(value = "cameraData", required = false) String cameraData,
                             HttpSession session,
                             RedirectAttributes redirect) {
        if (errors.hasErrors()) {
            redirect.addFlashAttribute("error", "Please fill in all required fields.");
            return "redirect:/report/lost";
        }

        User currentUser = (User) session.getAttribute("currentUser");
        String reporterEmail = (currentUser != null) ? currentUser.getEmail() : null;

        item.setReportType("LOST");
        item.setStatus("OPEN");
        if (item.getEventDate() == null) item.setEventDate(LocalDateTime.now());

        try {
            Item saved = itemService.saveItem(item, image, cameraData, reporterEmail);
            redirect.addFlashAttribute("success", "Lost report created (" + saved.getItemCode() + "). We will notify you if a matching item is found!");
        } catch (IllegalArgumentException | IOException ex) {
            redirect.addFlashAttribute("error", ex.getMessage());
            return "redirect:/report/lost";
        }

        return "redirect:/student/dashboard";
    }

    /**
     * Condition 4: Lost person searches product name (e.g. earbuds, bottle, calculator)
     */
    @GetMapping("/search")
    public String search(@RequestParam(required = false) String q,
                         @RequestParam(required = false) String category,
                         Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);
        model.addAttribute("items", itemService.searchFoundItems(q, category));
        model.addAttribute("q", q != null ? q : "");
        model.addAttribute("category", category != null ? category : "ALL");
        return "search";
    }

    /**
     * Item details view (Mystery Finder card)
     * For privacy/anti-fraud (Condition 6), secret clues are NOT shown publicly.
     */
    @GetMapping("/item/{id}")
    public String details(@PathVariable Long id, Model model, HttpSession session) {
        User currentUser = (User) session.getAttribute("currentUser");
        model.addAttribute("currentUser", currentUser);

        return itemService.findById(id).map(item -> {
            model.addAttribute("item", item);
            return "details";
        }).orElse("redirect:/search");
    }
}
