package org.baeldung.web.controller.projet.SuperAdmin;

import org.baeldung.persistence.model.User;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.ui.Model;

/**
 * Contrôleur qui ajoute des attributs communs à toutes les vues SuperAdmin
 * Notamment la gestion des images de profil en fonction du rôle de l'utilisateur
 */
@ControllerAdvice(basePackages = "org.baeldung.web.controller.projet")
public class SuperAdminCommonAttributesController {

    /**
     * Ajoute l'image de profil appropriée en fonction du rôle de l'utilisateur
     * @param model Le modèle Thymeleaf
     * @param authentication L'objet d'authentification Spring Security
     */
    @ModelAttribute
    public void addCommonAttributes(Model model, Authentication authentication) {
        if (authentication != null && authentication.isAuthenticated() && !authentication.getName().equals("anonymousUser")) {
            try {
                User user = (User) authentication.getPrincipal();
                String userRole = "";
                
                // Récupérer le premier rôle si disponible
                if (!user.getRoles().isEmpty()) {
                    userRole = user.getRoles().iterator().next().getName();
                }
                
                // Sélection de l'image en fonction du rôle
                String profileImage;
                if (userRole.equals("ROLE_ADMIN") || userRole.contains("ADMIN")) {
                    profileImage = "/assets/img/yahia.jpg";
                } else if (userRole.equals("ROLE_PROPRIETAIRE") || userRole.contains("PROPRIETAIRE")) {
                    profileImage = "/assets/img/ahmed.jpg";
                } else if (userRole.equals("ROLE_CLIENT") || userRole.contains("CLIENT")) {
                    profileImage = "/assets/img/ismail.jpg";
                } else {
                    // Image par défaut si le rôle n'est pas reconnu
                    profileImage = "/assets/img/tourist_guide_pic.jpg";
                }
                
                model.addAttribute("userProfileImage", profileImage);
            } catch (Exception e) {
                // En cas d'erreur, utiliser l'image par défaut
                model.addAttribute("userProfileImage", "/assets/img/tourist_guide_pic.jpg");
            }
        } else {
            // Utilisateur non authentifié
            model.addAttribute("userProfileImage", "/assets/img/tourist_guide_pic.jpg");
        }
    }
}
