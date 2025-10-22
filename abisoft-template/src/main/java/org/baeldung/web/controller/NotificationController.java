package org.baeldung.web.controller;

import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.notification.Notification;
import org.baeldung.service.NotificationService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.baeldung.persistence.dao.UserRepository;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@Controller
@RequestMapping("/notifications")
public class NotificationController {

    @Autowired
    private NotificationService notificationService;

    @Autowired
    private UserRepository userRepository;

    /**
     * Affiche la liste des notifications pour l'utilisateur connecté
     */
    @GetMapping
    public String afficherNotifications(Model model) {
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName());

        if (currentUser != null) {
            List<Notification> notifications = notificationService.getAllNotifications(currentUser);
            model.addAttribute("notifications", notifications);
            model.addAttribute("pageTitle", "Mes notifications");
            return "notifications/liste";
        }

        return "redirect:/admin";
    }

    /**
     * Marque une notification comme lue
     */
    @PostMapping("/marquer-lu/{id}")
    public ResponseEntity<Map<String, Object>> marquerCommeLue(@PathVariable Long id) {
        Map<String, Object> response = new HashMap<>();
        
        try {
            notificationService.marquerCommeLue(id);
            response.put("success", true);
            response.put("message", "Notification marquée comme lue");
        } catch (Exception e) {
            response.put("success", false);
            response.put("message", "Erreur: " + e.getMessage());
        }
        
        return ResponseEntity.ok(response);
    }

    /**
     * Récupère le nombre de notifications non lues pour l'utilisateur connecté (pour l'affichage du badge)
     */
    @GetMapping("/count-non-lues")
    @ResponseBody
    public Map<String, Object> countNotificationsNonLues() {
        Map<String, Object> response = new HashMap<>();
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User currentUser = userRepository.findByEmail(auth.getName());
            if (currentUser != null) {
                long count = notificationService.countNotificationsNonLues(currentUser);
                response.put("count", count);
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
            }
        } else {
            response.put("success", false);
            response.put("message", "Utilisateur non authentifié");
        }
        
        return response;
    }
    
    /**
     * Récupère les notifications non lues pour l'utilisateur connecté (pour le menu déroulant)
     */
    @GetMapping("/non-lues")
    @ResponseBody
    public Map<String, Object> getNotificationsNonLues() {
        Map<String, Object> response = new HashMap<>();
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            User currentUser = userRepository.findByEmail(auth.getName());
            if (currentUser != null) {
                List<Notification> notifications = notificationService.getNotificationsNonLues(currentUser);
                response.put("notifications", notifications);
                response.put("success", true);
            } else {
                response.put("success", false);
                response.put("message", "Utilisateur non trouvé");
                response.put("notifications", new ArrayList<>());
            }
        } else {
            response.put("success", false);
            response.put("message", "Utilisateur non authentifié");
            response.put("notifications", new ArrayList<>());
        }
        
        return response;
    }
}
