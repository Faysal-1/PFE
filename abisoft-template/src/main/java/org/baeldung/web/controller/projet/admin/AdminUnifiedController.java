package org.baeldung.web.controller.projet.admin;

import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.baeldung.persistence.model.hebergementModel.ValidationStatus;
import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestParam;

import org.baeldung.persistence.repository.HebergementRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.repository.VilleRepository;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.User;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

/**
 * Contrôleur unifié pour l'interface d'administration
 * Permet d'accéder aux mêmes templates avec des droits différents selon le rôle
 */
@Controller
@RequestMapping("/admin")
public class AdminUnifiedController {
    
    private static final Logger logger = LoggerFactory.getLogger(AdminUnifiedController.class);
    
    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private HebergementRepository hebergementRepository;
    
    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private org.baeldung.persistence.repository.EvenmentRepository evenementRepository;
    
    /**
     * Page d'accueil du tableau de bord
     */
    @Transactional
    @GetMapping("/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User user = null;
        
        // Vérifier si l'authentification est valide et si l'utilisateur existe
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            user = userRepository.findByEmail(auth.getName());
            
            // Stocker l'utilisateur dans la session si nécessaire
            HttpSession session = request.getSession();
            if (user != null && session.getAttribute("user1") == null) {
                session.setAttribute("user1", user);
            }
        } else {
            // Si pas d'authentification, essayer de récupérer depuis la session
            HttpSession session = request.getSession(false);
            if (session != null) {
                user = (User) session.getAttribute("user1");
            }
        }
        
        // Ajouter l'utilisateur au modèle
        if (user != null) {
            ret.addObject("user", user);
            ret.addObject("connectedUser", user);
            
            // Ajouter le nom complet de l'utilisateur pour l'affichage
            String fullName = user.getFirstName() + " " + user.getLastName();
            ret.addObject("userFullName", fullName);
            
            // Ajouter l'ID de l'utilisateur pour les requêtes de propriétaire
            ret.addObject("userId", user.getId());
        }
        
        // Vérifier les rôles une seule fois
        boolean isSuperAdmin = hasAuthority(auth, "SUPERADMIN_PRIVILEGE");
        boolean isHebergementAdmin = isSuperAdmin || hasAuthority(auth, "HEBERGEMENT_ADMIN_PRIVILEGE");
        boolean isEvenementAdmin = isSuperAdmin || hasAuthority(auth, "EVENEMENT_ADMIN_PRIVILEGE");
        boolean isProprietaire = hasAuthority(auth, "PROPRIETAIRE_PRIVILEGE");
        
        // Ajouter des indicateurs de rôle pour Thymeleaf
        ret.addObject("isSuperAdmin", isSuperAdmin);
        ret.addObject("isHebergementAdmin", isHebergementAdmin);
        ret.addObject("isEvenementAdmin", isEvenementAdmin);
        ret.addObject("isProprietaire", isProprietaire);
        
        // Compter les hébergements en attente pour les administrateurs
        if (isHebergementAdmin || isSuperAdmin) {
            long enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
            ret.addObject("enAttente", enAttente);
        }
        
        // Pour les propriétaires, compter leurs hébergements par statut
        if (isProprietaire && user != null) {
            List<Hebergement> hebergementsProprietaire = hebergementRepository.findByProprietaireId(user.getId());
            long enAttente = hebergementsProprietaire.stream()
                .filter(h -> h.getValidationStatus() == ValidationStatus.EN_ATTENTE)
                .count();
            long acceptes = hebergementsProprietaire.stream()
                .filter(h -> h.getValidationStatus() == ValidationStatus.ACCEPTE)
                .count();
            long refuses = hebergementsProprietaire.stream()
                .filter(h -> h.getValidationStatus() == ValidationStatus.REFUSE)
                .count();
            
            ret.addObject("mesHebergementsEnAttente", enAttente);
            ret.addObject("mesHebergementsAcceptes", acceptes);
            ret.addObject("mesHebergementsRefuses", refuses);
            ret.addObject("mesHebergementsTotal", hebergementsProprietaire.size());
        }
        
        // Utiliser le template SuperAdmin existant
        ret.setViewName("SuperAdmin/index");
        return ret;
    }
    
    @GetMapping("/evenements")
    public String listeEvenements(Model model) {
        prepareCommonModelAttributes(model);
        model.addAttribute("evenements", evenementRepository.findAll()); // Injection de la liste des événements
        return "evenment/all_evenment_list";
    }
    
    /**
     * Formulaire d'ajout d'événement
     */
    @GetMapping("/add-evenement")
    public String ajouterEvenement(Model model) {
        // Préparer les attributs communs du modèle
        prepareCommonModelAttributes(model);
        
        return "SuperAdmin/db-vendor-add-evenment";
    }
    
    /**
     * Récupère l'utilisateur courant
     */
    private User getCurrentUser() {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
            return userRepository.findByEmail(auth.getName());
        }
        return null;
    }
    
    /**
     * Vérifie si l'utilisateur a une autorité spécifique
     */
    private boolean hasAuthority(Authentication auth, String authority) {
        return auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals(authority));
    }
    
    /**
     * Prépare les attributs communs du modèle (utilisateur connecté, rôles, etc.)
     * @param model Le modèle à enrichir
     * @return L'utilisateur connecté (peut être null)
     */
    private User prepareCommonModelAttributes(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
        // Vérifier si l'authentification est valide
        if (auth == null || !auth.isAuthenticated() || auth.getName().equals("anonymousUser")) {
            // Pas d'utilisateur authentifié
            model.addAttribute("isAuthenticated", false);
            return null;
        }
        
        // Récupérer l'utilisateur connecté
        User currentUser = userRepository.findByEmail(auth.getName());
        
        if (currentUser != null) {
            // Ajouter l'utilisateur au modèle
            model.addAttribute("user", currentUser);
            model.addAttribute("connectedUser", currentUser);
            model.addAttribute("userId", currentUser.getId());
            model.addAttribute("userFullName", currentUser.getFirstName() + " " + currentUser.getLastName());
            model.addAttribute("isAuthenticated", true);
            
            // Vérifier les rôles une seule fois
            boolean isSuperAdmin = hasAuthority(auth, "SUPERADMIN_PRIVILEGE");
            boolean isHebergementAdmin = isSuperAdmin || hasAuthority(auth, "HEBERGEMENT_ADMIN_PRIVILEGE");
            boolean isEvenementAdmin = isSuperAdmin || hasAuthority(auth, "EVENEMENT_ADMIN_PRIVILEGE");
            boolean isProprietaire = hasAuthority(auth, "PROPRIETAIRE_PRIVILEGE");
            
            // Ajouter des indicateurs de rôle pour Thymeleaf
            model.addAttribute("isSuperAdmin", isSuperAdmin);
            model.addAttribute("isHebergementAdmin", isHebergementAdmin);
            model.addAttribute("isEvenementAdmin", isEvenementAdmin);
            model.addAttribute("isProprietaire", isProprietaire);
            
            // Ajouter des attributs spécifiques pour les propriétaires
            if (isProprietaire) {
                model.addAttribute("proprietaireId", currentUser.getId());
            }
        } else {
            model.addAttribute("isAuthenticated", false);
        }
        
        return currentUser;
    }
}
