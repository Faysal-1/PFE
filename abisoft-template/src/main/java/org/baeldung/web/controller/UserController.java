package org.baeldung.web.controller;

import java.util.Locale;

import org.baeldung.persistence.repository.security.ActiveUserStore;
import org.baeldung.service.IUserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.core.userdetails.User;

@Controller
public class UserController {

    @Autowired
    ActiveUserStore activeUserStore;

    @Autowired
    IUserService userService;

    @RequestMapping(value = "/loggedUsers", method = RequestMethod.GET)
    public String getLoggedUsers(final Locale locale, final Model model) {
        model.addAttribute("users", activeUserStore.getUsers());
        return "users";
    }

    @RequestMapping(value = "/loggedUsersFromSessionRegistry", method = RequestMethod.GET)
    public String getLoggedUsersFromSessionRegistry(final Locale locale, final Model model) {
        model.addAttribute("users", userService.getUsersFromSessionRegistry());
        return "users";
    }
    
   
    @GetMapping({"/profile", "/SuperAdmin/profile"})
    public String showProfile(Model model, @AuthenticationPrincipal User userDetails) {
        // Sécurité : vérifie que l'utilisateur est connecté
        if (userDetails == null) {
            return "redirect:/login";
        }

        // Récupère l'utilisateur connecté à partir de son email (username)
        org.baeldung.persistence.model.User user = userService.findUserByEmail(userDetails.getUsername());
        if (user == null) {
            model.addAttribute("errorMessage", "Utilisateur introuvable.");
            return "error";
        }
        model.addAttribute("user", user);

        // Récupère tous les rôles de l'utilisateur (affichage sous forme de liste)
        String userRoles = user.getRoles().stream()
            .map(role -> role.getName())
            .reduce((r1, r2) -> r1 + ", " + r2)
            .orElse("ROLE_CLIENT");
        model.addAttribute("userRole", userRoles);
        return "profile";
    }

    @org.springframework.web.bind.annotation.PostMapping({"/profile/update", "/SuperAdmin/profile/update"})
    public String updateProfile(@org.springframework.web.bind.annotation.ModelAttribute("user") org.baeldung.persistence.model.User updatedUser,
                                @AuthenticationPrincipal User userDetails,
                                Model model) {
        org.baeldung.persistence.model.User user = userService.findUserByEmail(userDetails.getUsername());
        if (user == null) {
            model.addAttribute("errorMessage", "Utilisateur introuvable.");
            return "error";
        }
        // Mets à jour les champs autorisés
        user.setFirstName(updatedUser.getFirstName());
        user.setLastName(updatedUser.getLastName());
        user.setEmail(updatedUser.getEmail());
        // Ajoute d'autres setters si besoin (ville, téléphone, etc.)
        userService.saveRegisteredUser(user);

        model.addAttribute("user", user);
        model.addAttribute("userRole", user.getRoles().stream().map(r -> r.getName()).reduce((r1, r2) -> r1 + ", " + r2).orElse("ROLE_CLIENT"));
        model.addAttribute("successMessage", "Profil mis à jour avec succès !");
        return "profile";
    }
}
