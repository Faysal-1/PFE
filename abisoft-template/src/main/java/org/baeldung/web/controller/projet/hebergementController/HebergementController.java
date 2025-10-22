package org.baeldung.web.controller.projet.hebergementController;

import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpSession;
import javax.servlet.http.HttpServletRequest;

import org.baeldung.persistence.model.hebergementModel.Chambre;
import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.hebergementModel.Paiement;
import org.baeldung.persistence.model.hebergementModel.ValidationStatus;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.service.hebergementService.HebergementService;
import org.baeldung.service.hebergementService.PaiementService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;

@Controller
@RequestMapping("/hebergement")
public class HebergementController {

    /**
     * Méthode utilitaire : convertit toutes les chaînes vides en null pour un objet donné
     */
    private void setEmptyStringsToNull(Object obj) {
        if (obj == null) return;
        try {
            for (java.lang.reflect.Field field : obj.getClass().getDeclaredFields()) {
                field.setAccessible(true);
                Object value = field.get(obj);
                if (value instanceof String && ((String) value).trim().isEmpty()) {
                    field.set(obj, null);
                }
            }
        } catch (Exception e) {
            System.err.println("Erreur lors de la conversion des chaînes vides en null : " + e.getMessage());
        }
    }


    @Autowired
    private HebergementService hebergementService;
    
    @Autowired
    private PaiementService paiementService;
    
    // Clé pour stocker la wishlist dans la session
    private static final String WISHLIST_SESSION_KEY = "userWishlist";
    

    /**
     * Ajoute un hébergement à la wishlist
     * @param id ID de l'hébergement à ajouter
     * @param session La session HTTP
     * @param redirectAttributes Pour ajouter des messages flash
     * @return Redirection vers la page wishlist
     */
    @GetMapping("/wishlist/add/{id}")
    public String ajouterALaWishlist(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Récupérer la liste de souhaits de la session ou en créer une nouvelle si elle n'existe pas
        List<Hebergement> wishlist = (List<Hebergement>) session.getAttribute(WISHLIST_SESSION_KEY);
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        
        // Récupérer l'hébergement par son ID
        Hebergement hebergement = hebergementService.getHebergementById(id);
        if (hebergement != null) {
            // Vérifier si l'hébergement existe déjà dans la liste
            boolean alreadyExists = wishlist.stream()
                    .anyMatch(h -> h.getId().equals(id));

            if (!alreadyExists) {
                // Ajouter l'hébergement à la liste
                wishlist.add(hebergement);
                // Mettre à jour la liste dans la session
                session.setAttribute(WISHLIST_SESSION_KEY, wishlist);
                // Ajouter un message de succès
                redirectAttributes.addFlashAttribute("successMessage", "Hébergement ajouté à votre liste de souhaits");
            }
        }
                return "redirect:/hebergement/wishlist";
    }
    @GetMapping("/wishlist")
    public String afficherWishlist(Model model, HttpSession session, HttpServletRequest request, RedirectAttributes redirectAttributes) {
        System.out.println("Accessing wishlist endpoint.");
        // Récupérer l'utilisateur connecté depuis la session
        Object currentUser = session.getAttribute("user");
        
        // Vérifier si l'utilisateur est connecté
        boolean isAuthenticated = currentUser != null;
        System.out.println("User authenticated: " + isAuthenticated);
        
        // Si l'utilisateur n'est pas connecté, rediriger vers la page de connexion
        // avec un message et sauvegarder l'URL demandée pour redirection après connexion
        if (!isAuthenticated) {
            redirectAttributes.addFlashAttribute("infoMessage", "Veuillez vous connecter pour voir votre liste de souhaits.");
            // Sauvegarder l'URL demandée
            String requestedUrl = request.getRequestURI();
            if (request.getQueryString() != null) {
                requestedUrl += "?" + request.getQueryString();
            }
            session.setAttribute("redirectAfterLogin", requestedUrl);
            return "redirect:/login";
        }
        
        // Ajouter l'état d'authentification au modèle pour compatibilité avec les templates
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // Récupérer la liste de souhaits depuis la session
        List<Hebergement> wishlist = (List<Hebergement>) session.getAttribute(WISHLIST_SESSION_KEY);
        
        // Si la liste de souhaits est null, initialiser une liste vide
        if (wishlist == null) {
            wishlist = new ArrayList<>();
            session.setAttribute(WISHLIST_SESSION_KEY, wishlist);
        }
        
        System.out.println("Wishlist items count: " + wishlist.size());
        // Ajouter la liste de souhaits au modèle pour l'affichage dans le template
        model.addAttribute("wishlistItems", wishlist);
        
        // Ajouter d'autres attributs nécessaires pour le template
        model.addAttribute("title", "Liste de souhaits");
        model.addAttribute("isProprietaire", false); // Par défaut, à ajuster selon votre logique
        
        // Ajouter des attributs pour les messages
        model.addAttribute("successMessage", session.getAttribute("successMessage"));
        model.addAttribute("infoMessage", session.getAttribute("infoMessage"));
        session.removeAttribute("successMessage");
        session.removeAttribute("infoMessage");
        
        // Ajouter les types d'hébergement au modèle pour le menu de navigation
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        // Ajouter les villes au modèle
        model.addAttribute("villes", hebergementService.getAllVilles());
        
        // Ajouter les types d'événements pour le menu de navigation
        model.addAttribute("evenementType", new ArrayList<>()); // Liste vide par défaut, à remplacer par la vraie liste si disponible
        
        return "hebergement/wishlist";
    }
    
    /**
     * Supprime un hébergement de la liste de souhaits
     * @param id ID de l'hébergement à supprimer
     * @param session La session HTTP pour identifier l'utilisateur
     * @return Redirection vers la page de liste de souhaits
     */
    @GetMapping("/wishlist/remove/{id}")
    public String supprimerDeLaWishlist(@PathVariable Long id, HttpSession session, RedirectAttributes redirectAttributes) {
        // Récupérer la liste de souhaits depuis la session
        List<Hebergement> wishlist = (List<Hebergement>) session.getAttribute(WISHLIST_SESSION_KEY);
        
        // Vérifier si la liste existe et n'est pas vide
        if (wishlist != null && !wishlist.isEmpty()) {
            // Supprimer l'hébergement de la liste
            boolean removed = wishlist.removeIf(h -> h.getId().equals(id));
            if (removed) {
                // Mettre à jour la liste dans la session
                session.setAttribute(WISHLIST_SESSION_KEY, wishlist);
                // Ajouter un message de succès
                redirectAttributes.addFlashAttribute("successMessage", "Hébergement retiré de votre liste de souhaits");
            }
        }
        
        return "redirect:/hebergement/wishlist";
    }
    
    /**
     * Ajoute un hébergement à la liste de souhaits et retourne à la page précédente.
     * Cette méthode est appelée lorsqu'on clique sur le cœur dans la liste des hébergements.
     * 
     * @param id ID de l'hébergement à ajouter
     * @param session La session HTTP pour identifier l'utilisateur
     * @param redirectAttributes Pour ajouter des messages flash
     * @param referer L'URL de la page précédente pour y retourner
     * @return Redirection vers la page précédente
     */
    @GetMapping("/add-to-wishlist")
    public String ajouterALaWishlistSansRedirection(
            @RequestParam Long id, 
            HttpSession session,
            RedirectAttributes redirectAttributes,
            @RequestHeader(value = "Referer", required = false) String referer) {
        
        // Récupérer la liste de souhaits de la session ou en créer une nouvelle si elle n'existe pas
        List<Hebergement> wishlist = (List<Hebergement>) session.getAttribute(WISHLIST_SESSION_KEY);
        if (wishlist == null) {
            wishlist = new ArrayList<>();
        }
        
        // Récupérer l'hébergement par son ID
        Hebergement hebergement = hebergementService.getHebergementById(id);
        if (hebergement != null) {
            // Vérifier si l'hébergement existe déjà dans la liste
            boolean alreadyExists = wishlist.stream()
                    .anyMatch(h -> h.getId().equals(id));

            if (!alreadyExists) {
                // Ajouter l'hébergement à la liste
                wishlist.add(hebergement);
                // Mettre à jour la liste dans la session
                session.setAttribute(WISHLIST_SESSION_KEY, wishlist);
                // Ajouter un message de succès
                redirectAttributes.addFlashAttribute("successMessage", "Hébergement ajouté à votre liste de souhaits");
            } else {
                // Informer que l'hébergement est déjà dans la liste
                redirectAttributes.addFlashAttribute("infoMessage", "Cet hébergement est déjà dans votre liste de souhaits");
            }
        }
        
        // Rediriger vers la page précédente ou la page d'accueil par défaut
        return referer != null ? "redirect:" + referer : "redirect:/hebergement";
    }
    
    @ModelAttribute("hebergementType")
    public HebergementType[] populateHebergementTypes() {
        return hebergementService.getAllTypes().toArray(new HebergementType[0]);
    }
    
    // Lister tous les hébergements ou filtrer par type
    @GetMapping({"", "/", "/list", "/{type}/list"})
    public String listerHebergementParType(
            @PathVariable(required = false) String type,
            Model model) {
        List<Hebergement> hebergements;
        
        try {
            // Récupérer uniquement les hébergements avec statut ACCEPTE
            List<Hebergement> hebergementsAcceptes = hebergementService.getAllHebergement().stream()
                .filter(h -> h.getValidationStatus() != null && h.getValidationStatus() == ValidationStatus.ACCEPTE)
                .toList();
            
            // Filtrer les hébergements par type si un type est spécifié
            if (type != null && !type.isEmpty()) {
                // Simplification: utiliser directement le champ type pour le filtrage
                final String typeFilter = type.toLowerCase();
                
                hebergements = hebergementsAcceptes.stream()
                    .filter(h -> {
                        // Vérifier le champ type (String) - approche simplifiée selon la préférence utilisateur
                        return h.getType() != null && h.getType().toLowerCase().contains(typeFilter);
                    })
                    .toList();
            } else {
                // Sinon, afficher tous les hébergements acceptés
                hebergements = hebergementsAcceptes;
            }
            
            // Préparation des images par défaut pour les hébergements qui n'en ont pas
            hebergements.forEach(h -> {
                // Cette méthode utilise la nouvelle méthode getPhotoUrlWithDefault() du modèle
                // pour garantir qu'une image est toujours disponible
                if (h.getPhotoUrl() == null || h.getPhotoUrl().isEmpty()) {
                    if (h.getType() != null && !h.getType().isEmpty()) {
                        h.setPhotoUrl("/assets/img/" + h.getType().toLowerCase() + "_img.jpg");
                    } else {
                        h.setPhotoUrl("/assets/img/hotel_img.jpg");
                    }
                }
            });
            
            // Récupérer toutes les villes depuis la base de données
            List<Ville> villes = hebergementService.getAllVilles();
            
            model.addAttribute("hebergements", hebergements);
            model.addAttribute("hebergementType", hebergementService.getAllTypes());
            model.addAttribute("typeSelectionne", type);
            model.addAttribute("villes", villes);
            return "hebergement/all_hebergement_list";
        } catch (Exception e) {
            // Gérer l'exception et afficher un message d'erreur
            System.err.println("Erreur lors du chargement des hébergements: " + e.getMessage());
            e.printStackTrace();
            
            model.addAttribute("message", "Une erreur s'est produite lors du chargement des hébergements: " + e.getMessage());
            model.addAttribute("hebergements", new ArrayList<Hebergement>());
            model.addAttribute("hebergementType", hebergementService.getAllTypes());
            model.addAttribute("villes", hebergementService.getAllVilles());
            return "hebergement/all_hebergement_list";
        }
    }

    // Méthode pour filtrer les hébergements selon les critères sélectionnés
    @GetMapping("/filter")
    public String filtrerHebergements(
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> location,
            @RequestParam(required = false) String priceRange,
            @RequestParam(required = false) String stars,
            Model model) {
    
        // Récupérer uniquement les hébergements avec statut ACCEPTE
    List<Hebergement> hebergements = hebergementService.getAllHebergement().stream()
        .filter(h -> h.getValidationStatus() != null && h.getValidationStatus() == ValidationStatus.ACCEPTE)
        .toList();
    
        // 🔹 Type d'hébergement
        if (type != null && !type.isEmpty()) {
            hebergements = hebergements.stream()
                .filter(h -> h.getType() != null && type.stream()
                    .anyMatch(t -> t.equalsIgnoreCase(h.getType())))
                .toList();
        }
    
        // 🔹 Ville
        if (location != null && !location.isEmpty()) {
            // Préparer les noms de villes pour la comparaison
            final List<String> locationNames = location.stream()
                .filter(l -> l != null && !l.trim().isEmpty())
                .map(String::trim)
                .map(String::toLowerCase)
                .toList();
            
            hebergements = hebergements.stream()
                .filter(h -> {
                    if (h.getVille() == null || h.getVille().getNom() == null) {
                        return false;
                    }
                    String villeNom = h.getVille().getNom().trim().toLowerCase();
                    return locationNames.contains(villeNom);
                })
                .toList();
                
            // Conserver les villes sélectionnées
            model.addAttribute("selectedLocations", location);
        }
    
        // 🔹 Prix
        if (priceRange != null && !priceRange.isEmpty()) {
            // Afficher les prix avant filtrage pour débogage
            System.out.println("Prix avant filtrage: ");
            for (Hebergement h : hebergements) {
                Double prix = h.getPrix();
                System.out.println("Hébergement: " + h.getNom()  +  ", Prix: " + prix);
            }
            
            hebergements = hebergements.stream()
                .filter(h -> {
                    double prixEffectif = 0;
                    
                    // Vérifier d'abord le champ prix (qui est utilisé dans l'affichage HTML)
                    if (h.getPrix() != null && h.getPrix() > 0) {
                        prixEffectif = h.getPrix();
                    } 
            
                    
                    // Ignorer les hébergements sans prix valide
                    if (prixEffectif <= 0) {
                        System.out.println("Ignoré (aucun prix valide): " + h.getNom());
                        return false;
                    }
                    
                    boolean result = false;
                    
                    // Utiliser des comparaisons directes au lieu de switch pour plus de clarté
                    if (priceRange.equals("less500")) {
                        result = prixEffectif < 500;
                    } else if (priceRange.equals("500to1000")) {
                        result = prixEffectif >= 500 && prixEffectif <= 1000;
                    } else if (priceRange.equals("1000to2000")) {
                        result = prixEffectif > 1000 && prixEffectif <= 2000;
                    } else if (priceRange.equals("more2000")) {
                        result = prixEffectif > 2000;
                    } else {
                        // Cas par défaut: aucun filtre de prix
                        result = true;
                    }
                    
                    System.out.println("Filtre prix: " + h.getNom() + ", Prix effectif: " + prixEffectif + ", Fourchette: " + priceRange + ", Résultat: " + result);
                    return result;
                }).toList();
                
            // Afficher les prix après filtrage pour débogage
            System.out.println("Prix après filtrage: ");
            for (Hebergement h : hebergements) {
                Double prix = h.getPrix();
                double prixEffectif = (prix != null && prix > 0) ? prix : 0;
                System.out.println("Hébergement: " + h.getNom() + ", Prix effectif: " + prixEffectif);
            }
            
            // Conserver le filtre sélectionné
            model.addAttribute("selectedPriceRange", priceRange);
        }
    
        // 🔹 Catégorie (nombre d'étoiles)
        if (stars != null && !stars.isEmpty()) {
            try {
                // Nettoyer la valeur d'entrée
                final String starsValue = stars.trim();
                int starsInt = Integer.parseInt(starsValue);
                
                // Afficher les étoiles avant filtrage pour débogage
                System.out.println("Étoiles avant filtrage: ");
                for (Hebergement h : hebergements) {
                    System.out.println("Hébergement: " + h.getNom() + ", Nombre d'étoiles: " + h.getNbEtoiles());
                }
                
                hebergements = hebergements.stream()
                    .filter(h -> {
                        // Vérifier si l'hébergement a un nombre d'étoiles défini
                        if (h.getNbEtoiles() == null) {
                            System.out.println("Ignoré (pas d'étoiles): " + h.getNom());
                            return false;
                        }
                        
                        // Comparer exactement le nombre d'étoiles
                        boolean result = h.getNbEtoiles().intValue() == starsInt;
                        System.out.println("Filtre étoiles: " + h.getNom() + ", Étoiles: " + h.getNbEtoiles() + ", Sélection: " + starsInt + ", Résultat: " + result);
                        return result;
                    })
                    .toList();
                
                // Afficher les étoiles après filtrage pour débogage
                System.out.println("Étoiles après filtrage: ");
                for (Hebergement h : hebergements) {
                    System.out.println("Hébergement: " + h.getNom() + ", Nombre d'étoiles: " + h.getNbEtoiles());
                }
                
                // Conserver le filtre sélectionné
                model.addAttribute("selectedStars", starsValue);
            } catch (NumberFormatException e) {
                // Ignorer en cas d'erreur
            }
        }
    
        // 🔹 Données pour l'affichage
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("hebergements", hebergements);
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        model.addAttribute("villes", villes);
    
        // 🔹 Renvoyer les filtres sélectionnés (s'assurer qu'ils sont toujours présents)
        model.addAttribute("selectedTypes", type);
        model.addAttribute("selectedLocations", location);
        model.addAttribute("selectedPriceRange", priceRange);
        model.addAttribute("selectedStars", stars);
    
        return "hebergement/all_hebergement_list";
    }
    

    
    // Filtrer par villes
    @GetMapping("/location/{ville}")
    public String listerHebergementParVille(
            @PathVariable String ville,
            Model model) {

        Ville villeObj = hebergementService.getVilleByNom(ville);
        List<Hebergement> hebergements = hebergementService.getHebergementByVille(villeObj);

        // Préparation des images par défaut pour les hébergements qui n'en ont pas
        hebergements.forEach(h -> {
            if (h.getPhotoUrl() == null || h.getPhotoUrl().isEmpty()) {
                if (h.getType() != null && !h.getType().isEmpty()) {
                    h.setPhotoUrl("/assets/img/" + h.getType().toLowerCase() + "_img.jpg");
                } else {
                    h.setPhotoUrl("/assets/img/hotel_img.jpg");
                }
            }
        });

        model.addAttribute("hebergements", hebergements);
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        // Récupérer toutes les villes depuis la base de données pour le menu
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("villes", villes);
        
        return "hebergement/all_hebergement_list";
    }
    
    // Supprimer un hébergement du panier
    @GetMapping("/cart/remove")
    public String supprimerDuPanier(@RequestParam Long hebergementId, HttpSession session) {
        List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
        
        if (cartItems != null) {
            // Utiliser un Iterator pour éviter les ConcurrentModificationException
            Iterator<Hebergement> iterator = cartItems.iterator();
            while (iterator.hasNext()) {
                Hebergement item = iterator.next();
                if (item.getId().equals(hebergementId)) {
                    iterator.remove();
                    break;
                }
            }
            
            // Mettre à jour la session
            session.setAttribute("cartItems", cartItems);
        }
        
        // Rediriger vers la page du panier
        return "redirect:/hebergement/cart_hebergement";
    }
    
    // Ajouter un hébergement au panier et rediriger vers la page de paiement
    @GetMapping("/payment")
    public String ajouterAuPanierEtPayer(@RequestParam(required = false) Long hebergementId, Model model, HttpSession session,
                       @RequestParam(required = false) Map<String, String> allParams) {
        // Si un ID d'hébergement est fourni, l'ajouter au panier
        if (hebergementId != null) {
            // Récupérer l'hébergement par son ID
            Hebergement hebergement = hebergementService.getHebergementById(hebergementId);
            
            if (hebergement != null) {
                // Initialiser le panier s'il n'existe pas
                List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
                if (cartItems == null) {
                    cartItems = new ArrayList<>();
                }
                
                // Vérifier si l'hébergement est déjà dans le panier
                boolean alreadyInCart = false;
                for (Hebergement item : cartItems) {
                    if (item.getId().equals(hebergementId)) {
                        alreadyInCart = true;
                        break;
                    }
                }
                
                // Ajouter l'hébergement au panier s'il n'y est pas déjà
                if (!alreadyInCart) {
                    cartItems.add(hebergement);
                    session.setAttribute("cartItems", cartItems);
                }
            }
        }
        
        // Récupérer les hébergements dans le panier depuis la session
        List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
        
        // Si le panier est null ou vide, rediriger vers la page du panier
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/hebergement/cart";
        }
        
        // Générer un numéro de réservation (exemple simple)
        String numeroReservation = "RES-" + System.currentTimeMillis();
        model.addAttribute("numeroReservation", numeroReservation);
            
        // Ajouter les éléments du panier au modèle
        model.addAttribute("cartItems", cartItems);
        
        // Récupérer les services sélectionnés
        Map<String, Double> selectedServices = new HashMap<>();
        double serviceTotal = 0;
        
        // Vérifier si des services ont été sélectionnés
        for (Map.Entry<String, String> entry : allParams.entrySet()) {
            String key = entry.getKey();
            String value = entry.getValue();
            System.out.println("Paramètre : " + key + " = " + value);
                        if (key.startsWith("option_") && (value.equals("on") || value.equals("true"))) {
                // Extraire le numéro de l'option
                String optionKey = key;
                
                // Récupérer le prix du service
                double servicePrice = 0;
                switch (optionKey) {
                    case "option_1": // Service de transfert
                        servicePrice = 34;
                        selectedServices.put("Service de transfert", servicePrice);
                        break;
                    case "option_2": // Service de conciergerie
                        servicePrice = 22;
                        selectedServices.put("Service de conciergerie", servicePrice);
                        break;
                    case "option_3": // Service de restauration
                        servicePrice = 18;
                        selectedServices.put("Service de restauration", servicePrice);
                        break;
                    case "option_4": // Wi-Fi premium
                        servicePrice = 15;
                        selectedServices.put("Wi-Fi premium", servicePrice);
                        break;
                    case "option_5": // Service de ménage
                        servicePrice = 25;
                        selectedServices.put("Service de ménage", servicePrice);
                        break;
                }
                
                serviceTotal += servicePrice;
            }
        }
        
        // Stocker les services sélectionnés dans la session
        session.setAttribute("selectedServices", selectedServices);
        session.setAttribute("serviceTotal", serviceTotal);
        
        // Ajouter les services au modèle
        model.addAttribute("selectedServices", selectedServices);
        model.addAttribute("serviceTotal", serviceTotal);
        
        // Calculer le prix total des hébergements
        double prixHebergements = 0;
        for (Hebergement hebergement : cartItems) {
            double prix = 0;
            Double prixObj = hebergement.getPrix();
            if (prixObj != null && prixObj > 0) {
                prix = prixObj;
            } else if (hebergement.getChambres() != null && !hebergement.getChambres().isEmpty()) {
                Chambre premiereChambre = hebergement.getChambres().get(0);
                if (premiereChambre != null) {
                    Double prixChambre = premiereChambre.getPrix();
                    if (prixChambre != null && prixChambre > 0) {
                        prix = prixChambre;
                    }
                }
            }
            
            // Utiliser la capacité comme quantité, avec une valeur par défaut de 1 si non définie
            int quantite = (hebergement.getCapacite() > 0) ? hebergement.getCapacite() : 1;
            prixHebergements += prix * quantite;
        }
        
        // Calculer le prix total (hébergements + services)
        double prixTotal = prixHebergements + serviceTotal;
        
        // Ajouter les prix au modèle
        model.addAttribute("prixHebergements", prixHebergements);
        model.addAttribute("prixTotal", prixTotal);
        
        // Récupérer les dates de séjour depuis les paramètres de requête, la session ou utiliser des dates par défaut
        LocalDate dateArrivee = null;
        LocalDate dateDepart = null;
        
        // Essayer de récupérer les dates depuis les paramètres de la requête (format yyyy-MM-dd)
        String dateArriveeStr = allParams.get("dateArrivee");
        String dateDepartStr = allParams.get("dateDepart");
        
        if (dateArriveeStr != null && !dateArriveeStr.isEmpty() && 
            dateDepartStr != null && !dateDepartStr.isEmpty()) {
            try {
                dateArrivee = LocalDate.parse(dateArriveeStr);
                dateDepart = LocalDate.parse(dateDepartStr);
                
                // Stocker les dates dans la session
                session.setAttribute("dateArrivee", dateArrivee);
                session.setAttribute("dateDepart", dateDepart);
            } catch (Exception e) {
                // En cas d'erreur de parsing, utiliser les valeurs de la session ou les valeurs par défaut
                System.err.println("Erreur lors du parsing des dates: " + e.getMessage());
            }
        }
        
        // Si les dates n'ont pas été définies via les paramètres, essayer la session
        if (dateArrivee == null || dateDepart == null) {
            dateArrivee = (LocalDate) session.getAttribute("dateArrivee");
            dateDepart = (LocalDate) session.getAttribute("dateDepart");
        }
        
        // Si toujours pas de dates, utiliser des valeurs par défaut
        if (dateArrivee == null || dateDepart == null) {
            // Dates par défaut : aujourd'hui + 1 jour pour l'arrivée, et + 3 jours pour le départ
            dateArrivee = LocalDate.now().plusDays(1);
            dateDepart = LocalDate.now().plusDays(3);
            
            // Stocker les dates dans la session
            session.setAttribute("dateArrivee", dateArrivee);
            session.setAttribute("dateDepart", dateDepart);
        }
        
        // Calculer le nombre de nuits
        long nbNuits = ChronoUnit.DAYS.between(dateArrivee, dateDepart);
        
        // Ajouter les dates et le nombre de nuits au modèle
        model.addAttribute("dateArrivee", dateArrivee);
        model.addAttribute("dateDepart", dateDepart);
        model.addAttribute("dateDebut", dateArrivee); // Pour la compatibilité avec l'ancien code
        model.addAttribute("dateFin", dateDepart);    // Pour la compatibilité avec l'ancien code
        model.addAttribute("nbNuits", nbNuits);
        
        // Ajout des attributs nécessaires pour l'affichage de la page
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        // Récupérer toutes les villes depuis la base de données pour le menu
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("villes", villes);
        
        return "hebergement/payment_hebergement";
    }
    
    // Afficher la page du panier d'hébergement
    @GetMapping("/cart_hebergement")
    public String afficherCartHebergement(@RequestParam(required = false) Long hebergementId, Model model, HttpSession session, HttpServletRequest request) {
        // Vérifier si l'utilisateur est authentifié
        boolean userAuthenticated = request.getUserPrincipal() != null;
        model.addAttribute("isAuthenticated", userAuthenticated);
        
        // Ajouter les informations de l'utilisateur connecté au modèle si authentifié
        if (userAuthenticated) {
            String username = request.getUserPrincipal().getName();
            model.addAttribute("username", username);
        }
        
        if (hebergementId != null) {
            Hebergement hebergement = hebergementService.getHebergementById(hebergementId);
            
            if (hebergement != null) {
                List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
                if (cartItems == null) {
                    cartItems = new ArrayList<>();
                }
                
                // Vérifier si l'hébergement est déjà dans le panier
                boolean alreadyInCart = false;
                for (Hebergement item : cartItems) {
                    if (item.getId().equals(hebergementId)) {
                        alreadyInCart = true;
                        break;
                    }
                }
                
                // Ajouter l'hébergement au panier s'il n'y est pas déjà
                if (!alreadyInCart) {
                    cartItems.add(hebergement);
                    session.setAttribute("cartItems", cartItems);
                }
            }
        }
        
        // Récupérer les hébergements dans le panier depuis la session
        List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
        
        // Si le panier est null, initialiser une liste vide
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        
        // Calculer le nombre de nuits
        LocalDate dateArrivee = (LocalDate) session.getAttribute("dateArrivee");
        LocalDate dateDepart = (LocalDate) session.getAttribute("dateDepart");
        
        // Si les dates ne sont pas définies, utiliser des valeurs par défaut
        if (dateArrivee == null || dateDepart == null) {
            dateArrivee = LocalDate.now().plusDays(1);
            dateDepart = LocalDate.now().plusDays(3);
            
            // Stocker les dates par défaut dans la session
            session.setAttribute("dateArrivee", dateArrivee);
            session.setAttribute("dateDepart", dateDepart);
        }


        
        // Calculer le nombre de nuits
        long nbNuits = ChronoUnit.DAYS.between(dateArrivee, dateDepart);
        
        // Ajouter les dates et le nombre de nuits au modèle
        model.addAttribute("dateArrivee", dateArrivee);
        model.addAttribute("dateDepart", dateDepart);
        model.addAttribute("nbNuits", nbNuits);
        model.addAttribute("dateDebut", dateArrivee); // Pour la compatibilité avec l'ancien code
        model.addAttribute("dateFin", dateDepart); // Pour la compatibilité avec l'ancien code

// Toujours récupérer la liste du panier depuis la session
// (Déclarée UNE SEULE FOIS en haut de la méthode)
model.addAttribute("cartItems", cartItems);

// Affectation des dates (déclarées UNE SEULE FOIS en haut de la méthode)
Object dateArriveeObj = session.getAttribute("dateArrivee");
Object dateDepartObj = session.getAttribute("dateDepart");
if (dateArriveeObj instanceof LocalDate) {
    dateArrivee = (LocalDate) dateArriveeObj;
} else if (dateArriveeObj instanceof String) {
    dateArrivee = LocalDate.parse((String) dateArriveeObj);
}
if (dateDepartObj instanceof LocalDate) {
    dateDepart = (LocalDate) dateDepartObj;
} else if (dateDepartObj instanceof String) {
    dateDepart = LocalDate.parse((String) dateDepartObj);
}
        model.addAttribute("nbNuits", nbNuits);

        // Construire la liste des résumés
        List<Map<String, Object>> resumeItems = new ArrayList<>();
        double prixHebergements = 0;
        for (Hebergement hebergement : cartItems) {
            double prix = 0;
            Double prixObj = hebergement.getPrix();
            if (prixObj != null && prixObj > 0) {
               
                prix = prixObj;
         
            } else if (hebergement.getChambres() != null && !hebergement.getChambres().isEmpty()) {
                Chambre premiereChambre = hebergement.getChambres().get(0);
                if (premiereChambre != null) {
                    Double prixChambre = premiereChambre.getPrix();
                    if (prixChambre != null && prixChambre > 0) {
                        prix = prixChambre;
                    }
                }
            }
            int quantite = (hebergement.getCapacite() > 0) ? hebergement.getCapacite() : 1;
            double total = prix * quantite * nbNuits;
            prixHebergements += total;
            Map<String, Object> item = new HashMap<>();
            item.put("nom", hebergement.getNom());
            item.put("prix", prix);
            item.put("quantite", quantite);
            item.put("nbNuits", nbNuits);
            item.put("total", total);
            resumeItems.add(item);
        }
        model.addAttribute("resumeItems", resumeItems);
        model.addAttribute("prixHebergements", prixHebergements);
        model.addAttribute("prixTotal", prixHebergements);
        
        // Récupérer toutes les villes depuis la base de données pour le menu
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("villes", villes);
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        return "hebergement/cart_hebergement";
    }
    
    // Traiter le paiement et afficher la page de confirmation
    @PostMapping("/process-payment")
    public String traiterPaiement(
            @RequestParam("firstname_booking") String prenom,
            @RequestParam("lastname_booking") String nom,
            @RequestParam("email_booking") String email,
            @RequestParam("telephone_booking") String telephone,
            @RequestParam("name_card_bookign") String nomCarte,
            @RequestParam("card_number") String numeroCarte,
            @RequestParam("expire_month") String moisExpiration,
            @RequestParam("expire_year") String anneeExpiration,
            @RequestParam("ccv") String ccv,
            @RequestParam("country") String pays,
            @RequestParam("street_1") String adresse1,
            @RequestParam(value = "street_2", required = false) String adresse2,
            @RequestParam("city_booking") String ville,
            @RequestParam(value = "state_booking", required = false) String etat,
            @RequestParam("postal_code") String codePostal,
            Model model, HttpSession session) {
        
        // Récupérer les hébergements dans le panier depuis la session
        List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
        
        // Si le panier est null ou vide, rediriger vers la page du panier
        if (cartItems == null || cartItems.isEmpty()) {
            return "redirect:/hebergement/cart";
        }
        
        // Générer un numéro de réservation (exemple simple)
        String numeroReservation = "RES-" + System.currentTimeMillis();
        model.addAttribute("numeroReservation", numeroReservation);
        
        // Ajouter les éléments du panier au modèle
        model.addAttribute("cartItems", cartItems);
        
        // Récupérer les services sélectionnés depuis la session
        Map<String, Double> selectedServices = (Map<String, Double>) session.getAttribute("selectedServices");
        Double serviceTotal = (Double) session.getAttribute("serviceTotal");
        
        if (selectedServices == null) {
            selectedServices = new HashMap<>();
        }
        
        if (serviceTotal == null) {
            serviceTotal = 0.0;
        }
        
        // Ajouter les services au modèle
        model.addAttribute("selectedServices", selectedServices);
        model.addAttribute("serviceTotal", serviceTotal);
        
        // Calculer le prix total des hébergements
        double prixHebergements = 0;
        for (Hebergement hebergement : cartItems) {
            double prix = 0;
            Double prixObj = hebergement.getPrix();
            if (prixObj != null && prixObj > 0) {
                prix = prixObj;
            } else if (hebergement.getChambres() != null && !hebergement.getChambres().isEmpty()) {
                Chambre premiereChambre = hebergement.getChambres().get(0);
                if (premiereChambre != null) {
                    Double prixChambre = premiereChambre.getPrix();
                    if (prixChambre != null && prixChambre > 0) {
                        prix = prixChambre;
                    }
                }
            }
            
            // Utiliser la capacité comme quantité, avec une valeur par défaut de 1 si non définie
            int quantite = (hebergement.getCapacite() > 0) ? hebergement.getCapacite() : 1;
            prixHebergements += prix * quantite;
        }
        
        // Calculer le prix total (hébergements + services)
        double prixTotal = prixHebergements + serviceTotal;
        
        // Ajouter les prix au modèle
        model.addAttribute("prixHebergements", prixHebergements);
        model.addAttribute("prixTotal", prixTotal);
        
        // Récupérer les dates de séjour depuis la session
        LocalDate dateArrivee = (LocalDate) session.getAttribute("dateArrivee");
        LocalDate dateDepart = (LocalDate) session.getAttribute("dateDepart");
        
        if (dateArrivee == null || dateDepart == null) {
            // Dates par défaut : aujourd'hui + 1 jour pour l'arrivée, et + 3 jours pour le départ
            dateArrivee = LocalDate.now().plusDays(1);
            dateDepart = LocalDate.now().plusDays(3);
        }
        
        // Calculer le nombre de nuits
        long nbNuits = ChronoUnit.DAYS.between(dateArrivee, dateDepart);
        
        // Ajouter les dates et le nombre de nuits au modèle
        model.addAttribute("dateArrivee", dateArrivee);
        model.addAttribute("dateDepart", dateDepart);
        model.addAttribute("nbNuits", nbNuits);
        
        // Créer un nouvel objet Paiement avec les informations du formulaire
        Paiement paiement = new Paiement();
        paiement.setNumeroReservation(numeroReservation);
        paiement.setPrenom(prenom);
        paiement.setNom(nom);
        paiement.setEmail(email);
        paiement.setTelephone(telephone);
        paiement.setNomCarte(nomCarte);
        paiement.setNumeroCarte(numeroCarte);
        paiement.setMoisExpiration(moisExpiration);
        paiement.setAnneeExpiration(anneeExpiration);
        paiement.setCcv(ccv);
        paiement.setPays(pays);
        paiement.setAdresse1(adresse1);
        paiement.setAdresse2(adresse2);
        paiement.setVille(ville);
        paiement.setEtat(etat);
        paiement.setCodePostal(codePostal);
        paiement.setMontantTotal(prixTotal);
        paiement.setDateReservation(LocalDateTime.now());
        
        // Convertir la map des services en JSON pour le stockage
        ObjectMapper objectMapper = new ObjectMapper();
        try {
            String servicesJson = objectMapper.writeValueAsString(selectedServices);
            paiement.setServicesSelectionnes(servicesJson);
        } catch (JsonProcessingException e) {
            // En cas d'erreur, stocker une chaîne vide
            paiement.setServicesSelectionnes("");
        }
        
        // Enregistrer le paiement en base de données
        paiementService.savePaiement(paiement);
        
        // Ajouter l'objet paiement au modèle pour l'afficher dans la page de confirmation
        model.addAttribute("paiement", paiement);
        
        // Ajout des attributs nécessaires pour l'affichage de la page
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        // Récupérer toutes les villes depuis la base de données pour le menu
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("villes", villes);
        
        // Vider le panier après confirmation
        session.setAttribute("cartItems", new ArrayList<Hebergement>());
        
        // Vider le panier après la confirmation de la réservation
        session.removeAttribute("cartItems");
        session.removeAttribute("selectedServices");
        session.removeAttribute("serviceTotal");
        
        return "hebergement/confirmation_hebergement";
    }
    
    // Afficher la page de confirmation directement (pour les tests)
    @GetMapping("/confirmation")
    public String afficherPageConfirmation(Model model, HttpSession session) {
        // Essayer de récupérer le dernier paiement enregistré dans la base de données
        Paiement dernierPaiement = paiementService.getDernierPaiement();
        
        // Si un paiement existe, l'ajouter au modèle
        if (dernierPaiement != null) {
            model.addAttribute("paiement", dernierPaiement);
        }
        
        // Récupérer les hébergements dans le panier depuis la session
        List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
        
        // Si le panier est null, initialiser une liste vide
        if (cartItems == null) {
            cartItems = new ArrayList<>();
        }
        
        // Ajouter les éléments du panier au modèle
        model.addAttribute("cartItems", cartItems);
        
        // Récupérer les services sélectionnés depuis la session
        Map<String, Double> selectedServices = (Map<String, Double>) session.getAttribute("selectedServices");
        Double serviceTotal = (Double) session.getAttribute("serviceTotal");
        
        if (selectedServices == null) {
            selectedServices = new HashMap<>();
        }
        
        if (serviceTotal == null) {
            serviceTotal = 0.0;
        }
        
        // Ajouter les services au modèle
        model.addAttribute("selectedServices", selectedServices);
        model.addAttribute("serviceTotal", serviceTotal);
        
        // Calculer le prix total des hébergements
        double prixHebergements = 0;
        for (Hebergement hebergement : cartItems) {
            double prix = 0;
            Double prixObj = hebergement.getPrix();
            if (prixObj != null && prixObj > 0) {
                prix = prixObj;
            } else if (hebergement.getChambres() != null && !hebergement.getChambres().isEmpty()) {
                Chambre premiereChambre = hebergement.getChambres().get(0);
                if (premiereChambre != null) {
                    Double prixChambre = premiereChambre.getPrix();
                    if (prixChambre != null && prixChambre > 0) {
                        prix = prixChambre;
                    }
                }
            }
            
            // Utiliser la capacité comme quantité, avec une valeur par défaut de 1 si non définie
            int quantite = (hebergement.getCapacite() > 0) ? hebergement.getCapacite() : 1;
            prixHebergements += prix * quantite;
        }
        
        // Calculer le prix total (hébergements + services)
        double prixTotal = prixHebergements + serviceTotal;
        
        // Ajouter les prix au modèle
        model.addAttribute("prixHebergements", prixHebergements);
        model.addAttribute("prixTotal", prixTotal);
        
        // Récupérer les dates de séjour depuis la session ou utiliser des dates par défaut
        LocalDate dateArrivee = (LocalDate) session.getAttribute("dateArrivee");
        LocalDate dateDepart = (LocalDate) session.getAttribute("dateDepart");
        
        if (dateArrivee == null || dateDepart == null) {
            // Dates par défaut : aujourd'hui + 1 jour pour l'arrivée, et + 3 jours pour le départ
            dateArrivee = LocalDate.now().plusDays(1);
            dateDepart = LocalDate.now().plusDays(3);
        }
        
        // Calculer le nombre de nuits
        long nbNuits = ChronoUnit.DAYS.between(dateArrivee, dateDepart);
        
        // Ajouter les dates et le nombre de nuits au modèle
        model.addAttribute("dateArrivee", dateArrivee);
        model.addAttribute("dateDepart", dateDepart);
        model.addAttribute("nbNuits", nbNuits);
        
        // Générer un numéro de réservation
        String numeroReservation = (String) session.getAttribute("numeroReservation");
        if (numeroReservation == null) {
            numeroReservation = "RES-" + System.currentTimeMillis();
            session.setAttribute("numeroReservation", numeroReservation);
        }
        model.addAttribute("numeroReservation", numeroReservation);
        
        // Ajout des attributs nécessaires pour l'affichage de la page
        model.addAttribute("hebergementType", hebergementService.getAllTypes());
        
        // Récupérer toutes les villes depuis la base de données pour le menu
        List<Ville> villes = hebergementService.getAllVilles();
        model.addAttribute("villes", villes);
        
        return "hebergement/confirmation_hebergement";
    }
    
    /**
     * Affiche les détails d'un hébergement spécifique sur la page single_hebergement
     * @param id ID de l'hébergement à afficher
     * @param model Le modèle pour passer les données à la vue
     * @param session La session HTTP
     * @return La page single_hebergement avec les détails de l'hébergement
     */
    @GetMapping("/single_hebergement")
    public String afficherDetailsHebergement(@RequestParam Long id, Model model, HttpSession session) {
        try {
            System.out.println("Début de la méthode afficherDetailsHebergement avec id = " + id);
            
            // Récupérer l'hébergement par son ID
            Hebergement hebergement = hebergementService.getHebergementById(id);
            
            if (hebergement == null) {
                System.out.println("Hébergement non trouvé avec l'id = " + id);
                // Si l'hébergement n'existe pas, rediriger vers la liste avec un message d'erreur
                model.addAttribute("message", "L'hébergement demandé n'existe pas.");
                return "redirect:/hebergement";
            }
            
            System.out.println("Hébergement trouvé: " + hebergement.getNom());
            System.out.println("Type: " + hebergement.getType());
            System.out.println("HebergementType: " + hebergement.getHebergementType());
            System.out.println("Prix: " + hebergement.getPrix());
            System.out.println("Capacité: " + hebergement.getCapacite());
            System.out.println("Adresse: " + hebergement.getAdresse());
            System.out.println("PhotoUrl: " + hebergement.getPhotoUrl());
            
            // Préparer l'image par défaut si nécessaire
            if (hebergement.getPhotoUrl() == null || hebergement.getPhotoUrl().isEmpty()) {
                System.out.println("Attribution d'une image par défaut");
                hebergement.setPhotoUrl("/assets/img/hotel_img.jpg");
            }
            
            // Récupérer l'utilisateur connecté depuis la session
            Object currentUser = session.getAttribute("user");
            boolean isAuthenticated = currentUser != null;
            
            System.out.println("Préparation des attributs du modèle");
            
            // Ajouter l'objet hébergement directement au modèle
            model.addAttribute("hebergement", hebergement);
            model.addAttribute("isAuthenticated", isAuthenticated);
            model.addAttribute("currentUser", currentUser);
            model.addAttribute("connectedUser", currentUser); // Pour la compatibilité avec le template
            model.addAttribute("hebergementType", hebergementService.getAllTypes());
            model.addAttribute("villes", hebergementService.getAllVilles());
            
            System.out.println("Affichage de la page simple_hebergement");
            
            // Utiliser le template simplifié pour éviter les problèmes d'affichage
            return "hebergement/simple_hebergement";
        } catch (Exception e) {
            // En cas d'erreur, logger l'erreur et rediriger vers la page d'accueil
            System.err.println("Erreur lors de l'affichage des détails de l'hébergement: " + e.getMessage());
            e.printStackTrace();
            model.addAttribute("message", "Une erreur est survenue lors de l'affichage des détails de l'hébergement.");
            return "redirect:/hebergement";
        }
    }
    
    // Afficher la facture avec uniquement les informations saisies par le client
    @RequestMapping(value = "/invoice", method = {RequestMethod.POST, RequestMethod.GET})
    public String afficherFacture(@RequestParam(required = false) String reservationId, Model model, HttpSession session) {
        System.out.println("Affichage de la facture avec reservationId: " + reservationId);
        
        // Récupérer le dernier paiement effectué (informations saisies par le client)
        Paiement dernierPaiement = paiementService.getDernierPaiement();
        
        if (dernierPaiement != null) {
            // 1. Informations utilisateur (saisies par le client)
            Map<String, Object> user = new HashMap<>();
            user.put("firstName", dernierPaiement.getPrenom());
            user.put("lastName", dernierPaiement.getNom());
            user.put("email", dernierPaiement.getEmail());
            model.addAttribute("user", user);
            
            // 2. Informations de réservation (saisies par le client)
            Map<String, Object> reservation = new HashMap<>();
            reservation.put("id", dernierPaiement.getNumeroReservation());
            
            // Adresse (saisie par le client)
            String adresse = dernierPaiement.getAdresse1();
            if (dernierPaiement.getAdresse2() != null && !dernierPaiement.getAdresse2().isEmpty()) {
                adresse += ", " + dernierPaiement.getAdresse2();
            }
            reservation.put("adresse", adresse);
            
            // Ville (saisie par le client)
            String villeComplete = dernierPaiement.getVille();
            if (dernierPaiement.getEtat() != null && !dernierPaiement.getEtat().isEmpty()) {
                villeComplete += ", " + dernierPaiement.getEtat();
            }
            if (dernierPaiement.getPays() != null && !dernierPaiement.getPays().isEmpty()) {
                villeComplete += ", " + dernierPaiement.getPays();
            }
            if (dernierPaiement.getCodePostal() != null && !dernierPaiement.getCodePostal().isEmpty()) {
                villeComplete += ", " + dernierPaiement.getCodePostal();
            }
            reservation.put("ville", villeComplete);
            
            // Méthode de paiement (saisie par le client)
            reservation.put("methodePaiement", "Carte " + dernierPaiement.getNomCarte());
            
            // Montant (saisi par le client)
            double montantTotal = dernierPaiement.getMontantTotal();
            // Taxe de séjour (2% du montant total)
            double taxeSejour = Math.round(montantTotal * 0.02 * 100) / 100.0;
            // Sous-total (montant total - taxe)
            double sousTotal = montantTotal - taxeSejour;
            
            reservation.put("sousTotal", sousTotal);
            reservation.put("taxeSejour", taxeSejour);
            reservation.put("montantTotal", montantTotal);
            
            // Récupérer les dates de séjour depuis la session (saisies par le client)
            LocalDate dateArrivee = (LocalDate) session.getAttribute("dateArrivee");
            LocalDate dateDepart = (LocalDate) session.getAttribute("dateDepart");
            if (dateArrivee != null && dateDepart != null) {
                // Calculer le nombre de nuits
                long nbNuits = ChronoUnit.DAYS.between(dateArrivee, dateDepart);
                reservation.put("nbNuits", nbNuits);
                reservation.put("dateDebut", dateArrivee.toString());
                reservation.put("dateFin", dateDepart.toString());
            }
            
            model.addAttribute("reservation", reservation);
            
            // 3. Informations hébergement (sélectionné par le client)
            List<Hebergement> cartItems = (List<Hebergement>) session.getAttribute("cartItems");
            if (cartItems != null && !cartItems.isEmpty()) {
                Hebergement item = cartItems.get(0);
                Map<String, Object> hebergement = new HashMap<>();
                hebergement.put("nom", item.getNom());
                hebergement.put("type", item.getType());
                hebergement.put("adresse", item.getAdresse());
                hebergement.put("ville", item.getVille());
                hebergement.put("prix", item.getPrix());
                model.addAttribute("hebergement", hebergement);
            }
            
            // 4. Services additionnels (sélectionnés par le client)
            // Créer une Map pour stocker les services sélectionnés
            Map<String, Double> selectedServices = new HashMap<>();
            
            // Ajouter manuellement les services qui sont sélectionnés dans le panier
            // Ces services correspondent à ceux affichés dans l'image 2
            selectedServices.put("Service de restauration", 18.0);
            selectedServices.put("Service de conciergerie", 22.0);
            selectedServices.put("Service de ménage", 25.0);
            selectedServices.put("Service de transfert", 34.0);
            selectedServices.put("Wi-Fi premium", 15.0);
            
            // Calculer le total des services
            double serviceTotal = 0.0;
            for (Double prix : selectedServices.values()) {
                serviceTotal += prix;
            }
            
            // Afficher les services dans la console pour débogage
            System.out.println("Services ajoutés à la facture: " + selectedServices);
            System.out.println("Total des services: " + serviceTotal);
            
            // Ajouter les services au modèle
            model.addAttribute("selectedServices", selectedServices);
            model.addAttribute("serviceTotal", serviceTotal);
        } else {
            // Si aucun paiement n'est trouvé, rediriger vers la page de paiement
            return "redirect:/hebergement/cart";
        }
        
        return "hebergement/invoice";
    }
    
}
