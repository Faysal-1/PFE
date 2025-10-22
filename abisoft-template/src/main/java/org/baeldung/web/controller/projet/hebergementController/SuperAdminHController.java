package org.baeldung.web.controller.projet.hebergementController;

import java.util.List;
import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.hebergementModel.ValidationStatus;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.repository.HebergementRepository;
import org.baeldung.persistence.repository.VilleRepository;
import org.baeldung.persistence.repository.PayRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.notification.Notification;
import org.baeldung.persistence.model.hebergementModel.HebergementImage;
import org.baeldung.persistence.repository.HebergementImageRepository;
import org.baeldung.service.NotificationService;
import java.util.ArrayList;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import javax.servlet.http.HttpServletRequest;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.Optional;
import java.io.IOException;
import java.io.InputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;
import org.springframework.util.StringUtils;
import java.util.Map;

@Controller
@RequestMapping("/admin")
public class SuperAdminHController {

    @Autowired
    private org.baeldung.persistence.repository.ChambreRepository chambreRepository;


    private static final Logger logger = LoggerFactory.getLogger(SuperAdminHController.class);
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");

    @Autowired
    private HebergementRepository hebergementRepository;
  
    @Autowired
    private org.baeldung.service.hebergementService.HebergementService hebergementService;
  
    @Autowired
    private UserRepository userRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private PayRepository payRepository;

    @Autowired
    private HebergementImageRepository hebergementImageRepository;
    
    @Autowired
    private NotificationService notificationService;
    
    @InitBinder
    public void initBinder(WebDataBinder binder) {
        // Enregistrer un éditeur personnalisé pour convertir les chaînes de date en LocalDate
        binder.registerCustomEditor(LocalDate.class, new PropertyEditorSupport() {
            @Override
            public void setAsText(String text) throws IllegalArgumentException {
                if (text == null || text.trim().isEmpty()) {
                    setValue(null);
                } else {
                    try {
                        // Essayer d'abord avec le format standard
                        setValue(LocalDate.parse(text, DATE_FORMAT));
                    } catch (Exception e) {
                        try {
                            // Si ça échoue, essayer avec d'autres formats courants
                            if (text.matches("\\d{4}-\\d{1,2}-\\d{1,2}")) {
                                // Format ISO standard (yyyy-MM-dd)
                                setValue(LocalDate.parse(text, DateTimeFormatter.ISO_LOCAL_DATE));
                            } else if (text.matches("\\d{2}/\\d{2}/\\d{4}")) {
                                // Format dd/MM/yyyy
                                setValue(LocalDate.parse(text, DateTimeFormatter.ofPattern("dd/MM/yyyy")));
                            } else {
                                logger.error("Erreur lors du parsing de la date: " + text, e);
                                throw new IllegalArgumentException("Format de date invalide: " + text);
                            }
                        } catch (Exception ex) {
                            logger.error("Erreur lors du parsing de la date avec formats alternatifs: " + text, ex);
                            throw new IllegalArgumentException("Format de date invalide: " + text);
                        }
                    }
                }
            }
            
            @Override
            public String getAsText() {
                LocalDate value = (LocalDate) getValue();
                return (value != null ? value.format(DATE_FORMAT) : "");
            }
        });
    }

    // Afficher le tableau de bord principal
    @GetMapping
    public String afficherTableauDeBord(Model model) {
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName());
        
        if (currentUser != null) {
            // Récupérer les notifications non lues
            List<Notification> notifications = notificationService.getNotificationsNonLues(currentUser);
            long notificationCount = notificationService.countNotificationsNonLues(currentUser);
            
            model.addAttribute("notifications", notifications);
            model.addAttribute("notificationCount", notificationCount);
            model.addAttribute("userRole", determinerRole(currentUser));
        } else {
            model.addAttribute("notifications", List.of());
            model.addAttribute("notificationCount", 0);
        }
        
        return "SuperAdmin/index";
    }
    
    // Afficher la liste des hébergements
    @GetMapping("/hebergements")
    public String afficherListeHebergements(@RequestParam(value = "filter", required = false) String filter, Model model, HttpServletRequest request) {
        // Récupérer les messages de la session
        String successMessage = (String) request.getSession().getAttribute("successMessage");
        String errorMessage = (String) request.getSession().getAttribute("errorMessage");
        
        if (successMessage != null) {
            model.addAttribute("successMessage", successMessage);
            request.getSession().removeAttribute("successMessage");
        }
        
        if (errorMessage != null) {
            model.addAttribute("errorMessage", errorMessage);
            request.getSession().removeAttribute("errorMessage");
        }
        
        // Compter les hébergements en attente pour l'affichage du badge
        long enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
        model.addAttribute("enAttente", enAttente);
        
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Récupérer les indicateurs de rôle déjà ajoutés au modèle
        boolean isHebergementAdmin = (boolean) model.getAttribute("isHebergementAdmin");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        boolean isProprietaire = (boolean) model.getAttribute("isProprietaire");
        
        List<Hebergement> hebergements;
        
        // Filtrer les hébergements selon le paramètre filter
        if ("en_attente".equals(filter)) {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.EN_ATTENTE);
            model.addAttribute("filterActive", true);
            model.addAttribute("filterType", "en_attente");
            model.addAttribute("pageTitle", "Hébergements en attente de validation");
        } else if ("accepte".equals(filter)) {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.ACCEPTE);
            model.addAttribute("filterActive", true);
            model.addAttribute("filterType", "accepte");
            model.addAttribute("pageTitle", "Hébergements validés");
        } else if ("refuse".equals(filter)) {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.REFUSE);
            model.addAttribute("filterActive", true);
            model.addAttribute("filterType", "refuse");
            model.addAttribute("pageTitle", "Hébergements refusés");
        } else {
            // Si c'est un propriétaire, ne montrer que ses propres hébergements
            if (isProprietaire && !isHebergementAdmin && !isSuperAdmin) {
                // Récupérer l'utilisateur connecté
                Authentication auth = SecurityContextHolder.getContext().getAuthentication();
                User currentUser = null;
                if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                    currentUser = userRepository.findByEmail(auth.getName());
                }
                
                if (currentUser != null) {
                    // Pour les propriétaires, on affiche tous leurs hébergements avec leur statut
                    // et on n'utilise pas les filtres au-dessus du tableau
                        hebergements = hebergementRepository.findByProprietaireId(currentUser.getId());
                    
                    // Ajouter les compteurs par statut pour le propriétaire
                    model.addAttribute("acceptes", hebergementRepository.findByProprietaireIdAndValidationStatus(
                            currentUser.getId(), ValidationStatus.ACCEPTE).size());
                    model.addAttribute("refuses", hebergementRepository.findByProprietaireIdAndValidationStatus(
                            currentUser.getId(), ValidationStatus.REFUSE).size());
                    model.addAttribute("enAttentes", hebergementRepository.findByProprietaireIdAndValidationStatus(
                            currentUser.getId(), ValidationStatus.EN_ATTENTE).size());
                    
                    // Indiquer qu'il s'agit d'un propriétaire pour adapter l'affichage dans la vue
                    model.addAttribute("isPropView", true);
                } else {
                    hebergements = hebergementRepository.findAll();
                }
            } else {
                // Pour les administrateurs, montrer tous les hébergements
                hebergements = hebergementRepository.findAll();
                model.addAttribute("isPropView", false);
            }
            model.addAttribute("filterActive", false);
            model.addAttribute("pageTitle", "Tous les hébergements");
        }
        
        model.addAttribute("hebergements", hebergements);
        return "SuperAdmin/db-vendor-hebergement";
    }

    @GetMapping("/hebergements/add")
public String afficherFormulaireAjout(Model model) {
    addRoleInformation(model);
    model.addAttribute("hebergement", new Hebergement());
    model.addAttribute("validationMessage", "Votre hébergement sera soumis à validation par un administrateur avant d'être visible par les clients.");
    long enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
    model.addAttribute("enAttente", enAttente);
    model.addAttribute("hebergementTypes", hebergementService.getAllTypes());
    model.addAttribute("villes", villeRepository.findAll());
    model.addAttribute("pays", payRepository.findAll());
    model.addAttribute("tarifTypes", HebergementTarifType.values());
    return "SuperAdmin/db-vendor-add-hebergement";
}
    
    /**
     * Affiche le formulaire d'édition d'un hébergement
     */
    @GetMapping("/hebergements/edit/{id}")
public String afficherFormulaireEdition(@PathVariable Long id, Model model, HttpServletRequest request) {
    try {
        // Vérifier si l'hébergement existe
        Hebergement hebergement = hebergementRepository.findById(id).orElse(null);
        if (hebergement == null) {
            request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
            return "redirect:/admin/db-vendor-hebergement";
        }
        addRoleInformation(model);
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("pays", payRepository.findAll());
        model.addAttribute("hebergementTypes", HebergementType.values());
        model.addAttribute("tarifTypes", HebergementTarifType.values());
        // Ajouter l'hébergement au modèle
        model.addAttribute("hebergement", hebergement);
        model.addAttribute("isEditing", true); // Indiquer qu'il s'agit d'une édition
        model.addAttribute("isSuperAdminEdit", true); // Paramètre spécifique pour le mode édition par un superadmin
        model.addAttribute("pageTitle", "Modifier un hébergement");
        // Compter les hébergements en attente pour l'affichage du badge
        long enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
        model.addAttribute("enAttente", enAttente);
        return "SuperAdmin/db-vendor-add-hebergement";
    } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de l'affichage du formulaire d'édition de l'hébergement " + id, e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/admin/db-vendor-hebergement";
        }
    }
    
    /**
     * Traite la mise à jour d'un hébergement existant
     */
    @PostMapping("/hebergements/update")
    public String mettreAJourHebergement(@ModelAttribute("hebergement") Hebergement hebergement,
                                     Model model, HttpServletRequest request) {
        try {
            Optional<Hebergement> optionalHebergementExistant = hebergementRepository.findById(hebergement.getId());
            if (!optionalHebergementExistant.isPresent()) {
                request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
                return "redirect:/admin/db-vendor-hebergement";
            }
            
            Hebergement hebergementExistant = optionalHebergementExistant.get();
            
            // Mettre à jour les propriétés de l'hébergement
            hebergementExistant.setNom(hebergement.getNom());
            hebergementExistant.setDescription(hebergement.getDescription());
            hebergementExistant.setAdresse(hebergement.getAdresse());
            hebergementExistant.setType(hebergement.getType());
            
            // Mettre à jour d'autres propriétés si elles existent
            if (hebergement.getPrix() != null) {
                hebergementExistant.setPrix(hebergement.getPrix());
            }
            
            if (hebergement.getCapacite() > 0) {
                hebergementExistant.setCapacite(hebergement.getCapacite());
            }
            
            // Enregistrer les modifications
            hebergementRepository.save(hebergementExistant);
            
            request.getSession().setAttribute("successMessage", "Hébergement mis à jour avec succès.");
            return "redirect:/admin/db-vendor-hebergement?success=true";
        } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de la mise à jour de l'hébergement", e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour: " + e.getMessage());
            return "redirect:/admin/db-vendor-hebergement?error=true";
        }
    }

    // Traitement de l'ajout
    @PostMapping("/hebergements/add")
    public String ajouterHebergement(
            @ModelAttribute Hebergement hebergement,
            @RequestParam("imageFile") MultipartFile imageFile,
            @RequestParam(value = "dateDebutStr", required = false) String dateDebutStr,
            @RequestParam(value = "dateFinStr", required = false) String dateFinStr,
            @RequestParam Map<String, String> params,
            Model model,
            HttpServletRequest request) {
        
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Vérifier si l'utilisateur est un propriétaire
        boolean isProprietaire = (boolean) model.getAttribute("isProprietaire");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        
        // Si c'est un super admin sans rôle de propriétaire, rediriger vers la liste des hébergements
        if (isSuperAdmin && !isProprietaire) {
            request.getSession().setAttribute("errorMessage", "Seuls les propriétaires peuvent ajouter des hébergements.");
            return "redirect:/admin/hebergements";
        }

        try {
            // Récupérer l'utilisateur connecté et le définir comme propriétaire
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            if (auth != null && auth.isAuthenticated() && !auth.getName().equals("anonymousUser")) {
                User currentUser = userRepository.findByEmail(auth.getName());
                if (currentUser != null) {
                    // Définir l'utilisateur connecté comme propriétaire de l'hébergement
                    hebergement.setProprietaire(currentUser);
                    logger.info("Propriétaire défini automatiquement: " + currentUser.getEmail());
                }
            }
              //zobon
            // Traitement de la liste des chambres dynamiques
            List<org.baeldung.persistence.model.hebergementModel.Chambre> chambres = new ArrayList<>();
            int index = 0;
            while (params.containsKey("chambres[" + index + "].type")) {
                String type = params.get("chambres[" + index + "].type");
                String prixStr = params.get("chambres[" + index + "].prix");
                String capaciteStr = params.get("chambres[" + index + "].capacite");
                if (type != null && prixStr != null && capaciteStr != null) {
                    try {
                        double prix = Double.parseDouble(prixStr);
                        int capacite = Integer.parseInt(capaciteStr);
                        org.baeldung.persistence.model.hebergementModel.Chambre chambre = new org.baeldung.persistence.model.hebergementModel.Chambre();
                        chambre.setType(type);
                        chambre.setPrix(prix);
                        chambre.setCapacite(capacite);
                        chambre.setHebergement(hebergement); // Associer à l'hébergement
                        chambres.add(chambre);
                    } catch (NumberFormatException e) {
                    }
                }
                index++;            }
            hebergementRepository.save(hebergement);
            if (!chambres.isEmpty()) {
                chambreRepository.saveAll(chambres);
            }
         //zobon
            // Traitement de l'image
            if (!imageFile.isEmpty()) {
                try {
                    // Créer un nom de fichier unique avec timestamp
                    String fileName = "uploaded_" + System.currentTimeMillis() + ".jpg";
                    
                    // Sauvegarder d'abord l'hébergement pour obtenir un ID
                    hebergement.setValidationStatus(ValidationStatus.EN_ATTENTE);
                    hebergementRepository.save(hebergement);
                    
                    // Définir le chemin où l'image sera sauvegardée
                    String uploadDir = "uploads/hebergements/" + hebergement.getId();
                    Path uploadPath = Paths.get(uploadDir);
                    
                    // Créer le répertoire s'il n'existe pas
                    if (!Files.exists(uploadPath)) {
                        Files.createDirectories(uploadPath);
                    }
                    
                    // Chemin complet du fichier
                    Path filePath = uploadPath.resolve(fileName);
                    
                    // Copier le fichier dans le répertoire de destination
                    try (InputStream inputStream = imageFile.getInputStream()) {
                        Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                        logger.info("Image sauvegardée avec succès: " + filePath);
                        
                        // Définir l'URL de la photo dans l'objet hébergement
                        String imageUrl = "/uploads/hebergements/" + hebergement.getId() + "/" + fileName;
                        hebergement.setPhotoUrl(imageUrl);
                        
                        // Mettre à jour l'hébergement avec l'URL de l'image
                        hebergementRepository.save(hebergement);
                    }
                } catch (Exception e) {
                    logger.error("Erreur lors du traitement de l'image", e);
                }
            } else {
                // Si pas d'image, sauvegarder l'hébergement quand même
                hebergement.setValidationStatus(ValidationStatus.EN_ATTENTE);
                hebergementRepository.save(hebergement);
            }
            
            // Conversion des dates
            if (dateDebutStr != null && !dateDebutStr.isEmpty()) {
                try {
                    hebergement.setDateDebut(LocalDate.parse(dateDebutStr, DATE_FORMAT));
                    logger.info("Date de début définie: " + hebergement.getDateDebut());
                } catch (Exception e) {
                    logger.error("Erreur de parsing de la date de début: " + dateDebutStr, e);
                }
            }
            
            if (dateFinStr != null && !dateFinStr.isEmpty()) {
                try {
                    hebergement.setDateFin(LocalDate.parse(dateFinStr, DATE_FORMAT));
                    logger.info("Date de fin définie: " + hebergement.getDateFin());
                } catch (Exception e) {
                    logger.error("Erreur de parsing de la date de fin: " + dateFinStr, e);
                }
            }
            
            // Définir un type par défaut si nécessaire
            if (hebergement.getType() == null && hebergement.getHebergementType() != null) {
                hebergement.setType(hebergement.getHebergementType().name());
            }
            
            hebergement.setValidationStatus(ValidationStatus.EN_ATTENTE);

            hebergementRepository.save(hebergement);
            
            notificationService.notifierNouvelHebergement(hebergement);   
            request.getSession().setAttribute("successMessage", "Hébergement ajouté avec succès et en attente de validation. Les administrateurs ont été notifiés.");        
            return "redirect:/admin/hebergements";
        } catch (Exception e) {
            logger.error("Erreur de sauvegarde", e);
            model.addAttribute("saveError", "Erreur lors de la sauvegarde: " + e.getMessage());
        }
        model.addAttribute("hebergementTypes", HebergementType.values());
        model.addAttribute("tarifTypes", HebergementTarifType.values());
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("pays", payRepository.findAll());
        return "SuperAdmin/db-vendor-add-hebergement";
    }
    
    @GetMapping("/hebergements-en-attente")
    public String listeHebergementsEnAttente(Model model, HttpServletRequest request) {
        addRoleInformation(model);
        
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName());
        
        List<Hebergement> hebergements;
        
        // Si c'est un propriétaire, ne montrer que ses hébergements
        boolean isProprietaire = (boolean) model.getAttribute("isProprietaire");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        boolean isHebergementAdmin = (boolean) model.getAttribute("isHebergementAdmin");
        
        if (isProprietaire && !isSuperAdmin && !isHebergementAdmin && currentUser != null) {
            hebergements = hebergementRepository.findByProprietaireAndValidationStatus(currentUser, ValidationStatus.EN_ATTENTE);
        } else {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.EN_ATTENTE);
        }
        
        model.addAttribute("hebergements", hebergements);
        model.addAttribute("pageTitle", "Hébergements en attente de validation");
        
        // Ajouter les compteurs par statut pour le propriétaire
        if (isProprietaire && currentUser != null) {
            model.addAttribute("acceptes", hebergementRepository.findByProprietaireIdAndValidationStatus(
                    currentUser.getId(), ValidationStatus.ACCEPTE).size());
            model.addAttribute("refuses", hebergementRepository.findByProprietaireIdAndValidationStatus(
                    currentUser.getId(), ValidationStatus.REFUSE).size());
            model.addAttribute("enAttente", hebergements.size());
        } else {
            model.addAttribute("enAttente", hebergements.size());
        }
        
        model.addAttribute("isPropView", true);
        return "SuperAdmin/db-vendor-hebergement";
    }
    
    /**
     * Valider un hébergement
     */
    @PostMapping("/valider-hebergement/{id}")
    public String validerHebergement(@PathVariable("id") Long id, Model model, HttpServletRequest request) {
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Vérifier que l'utilisateur est un administrateur
        boolean isHebergementAdmin = (boolean) model.getAttribute("isHebergementAdmin");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        
        if (!isHebergementAdmin && !isSuperAdmin) {
            return "redirect:/admin/index";
        }
        
        // Récupérer l'hébergement et le valider
        Hebergement hebergement = hebergementRepository.findById(id).orElse(null);
        if (hebergement != null) {
            hebergement.setValidationStatus(ValidationStatus.ACCEPTE);
            hebergementRepository.save(hebergement);
            
            // Envoyer une notification au propriétaire
            notificationService.notifierStatutHebergement(hebergement, true);
            
            // Stocker le message dans la session pour qu'il soit disponible après la redirection
            request.getSession().setAttribute("successMessage", "L'hébergement a été validé avec succès et le propriétaire a été notifié.");
        } else {
            // Stocker le message d'erreur dans la session
            request.getSession().setAttribute("errorMessage", "Hébergement introuvable.");
        }
        
        return "redirect:/admin/hebergements";
    }
    
    /**
     * Refuser un hébergement
     */
    @PostMapping("/refuser-hebergement/{id}")
    public String refuserHebergement(@PathVariable("id") Long id, @RequestParam("commentaire") String commentaire, Model model, HttpServletRequest request) {
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Vérifier que l'utilisateur est un administrateur
        boolean isHebergementAdmin = (boolean) model.getAttribute("isHebergementAdmin");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        boolean isProprietaire = (boolean) model.getAttribute("isProprietaire");
        
        if (!isHebergementAdmin && !isSuperAdmin) {
            return "redirect:/admin/index";
        }
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName());
        
        // Récupérer l'hébergement et le refuser
        Hebergement hebergement = hebergementRepository.findById(id).orElse(null);
        if (hebergement != null) {
            hebergement.setValidationStatus(ValidationStatus.REFUSE);
            hebergement.setCommentaireRefus(commentaire);
            hebergementRepository.save(hebergement);
            
            // Envoyer une notification au propriétaire
            if (hebergement.getProprietaire() != null) {
                notificationService.notifierStatutHebergement(hebergement, false);
            }
            
            // Stocker le message dans la session pour qu'il soit disponible après la redirection
            request.getSession().setAttribute("successMessage", "L'hébergement a été refusé.");
        } else {
            // Stocker le message d'erreur dans la session
            request.getSession().setAttribute("errorMessage", "Hébergement introuvable.");
        }
    
        List<Hebergement> hebergements;
        if (isProprietaire && !isSuperAdmin && !isHebergementAdmin && currentUser != null) {
            hebergements = hebergementRepository.findByProprietaireAndValidationStatus(currentUser, ValidationStatus.EN_ATTENTE);
        } else {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.EN_ATTENTE);
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.ACCEPTE);
        }
        
        model.addAttribute("hebergements", hebergements);
        model.addAttribute("pageTitle", "Hébergements acceptés");
        
        // Compter les hébergements en attente pour l'affichage du badge
        long enAttente = 0;
        if (isProprietaire && !isSuperAdmin && !isHebergementAdmin && currentUser != null) {
            enAttente = hebergementRepository.countByProprietaireAndValidationStatus(currentUser, ValidationStatus.EN_ATTENTE);
        } else {
            enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
        }
        
        model.addAttribute("enAttente", enAttente);
        model.addAttribute("isPropView", true);
        return "SuperAdmin/db-vendor-hebergement";
    }
    
    /**
     * Liste des hébergements refusés
     */
    @GetMapping("/hebergements-refuses")
    public String listeHebergementsRefuses(Model model, HttpServletRequest request) {
        // Ajouter les informations sur les rôles
        addRoleInformation(model);
        
        // Récupérer l'utilisateur connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        User currentUser = userRepository.findByEmail(auth.getName());
        
        // Récupérer les hébergements refusés
        List<Hebergement> hebergements;
        
        // Si c'est un propriétaire, ne montrer que ses hébergements
        boolean isProprietaire = (boolean) model.getAttribute("isProprietaire");
        boolean isSuperAdmin = (boolean) model.getAttribute("isSuperAdmin");
        boolean isHebergementAdmin = (boolean) model.getAttribute("isHebergementAdmin");
        
        if (isProprietaire && !isSuperAdmin && !isHebergementAdmin && currentUser != null) {
            hebergements = hebergementRepository.findByProprietaireAndValidationStatus(currentUser, ValidationStatus.REFUSE);
        } else {
            hebergements = hebergementRepository.findByValidationStatus(ValidationStatus.REFUSE);
        }
        
        model.addAttribute("hebergements", hebergements);
        model.addAttribute("pageTitle", "Hébergements refusés");
        
        // Compter les hébergements en attente pour l'affichage du badge
        long enAttente = 0;
        if (isProprietaire && !isSuperAdmin && !isHebergementAdmin && currentUser != null) {
            enAttente = hebergementRepository.countByProprietaireAndValidationStatus(currentUser, ValidationStatus.EN_ATTENTE);
        } else {
            enAttente = hebergementRepository.countByValidationStatus(ValidationStatus.EN_ATTENTE);
        }
        
        model.addAttribute("enAttente", enAttente);
        model.addAttribute("isPropView", true);
        return "SuperAdmin/db-vendor-hebergement";
    }
    
    /**
     * Ajoute les informations sur les rôles de l'utilisateur au modèle
     */
    private void addRoleInformation(Model model) {
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated()) {
            boolean isSuperAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("SUPERADMIN_PRIVILEGE"));
            boolean isHebergementAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("HEBERGEMENT_ADMIN_PRIVILEGE"));
            boolean isProprietaire = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("PROPRIETAIRE_PRIVILEGE"));
            boolean isEvenementAdmin = auth.getAuthorities().stream()
                    .anyMatch(a -> a.getAuthority().equals("EVENEMENT_ADMIN_PRIVILEGE"));
            
            model.addAttribute("isSuperAdmin", isSuperAdmin);
            model.addAttribute("isHebergementAdmin", isHebergementAdmin);
            model.addAttribute("isProprietaire", isProprietaire);
            model.addAttribute("isEvenementAdmin", isEvenementAdmin);
            
            // Déterminer si l'utilisateur peut ajouter des hébergements (uniquement les propriétaires)
            model.addAttribute("canAddHebergement", isProprietaire);
            
            // Déterminer si l'utilisateur peut valider/refuser des hébergements (super admin et admin hébergement)
            model.addAttribute("canValidateHebergement", isSuperAdmin || isHebergementAdmin);
            
            // Récupérer l'utilisateur connecté pour les notifications
            User currentUser = userRepository.findByEmail(auth.getName());
            if (currentUser != null) {
                // Ajouter le nombre de notifications non lues
                long notificationCount = notificationService.countNotificationsNonLues(currentUser);
                model.addAttribute("notificationCount", notificationCount);
            }
        } else {
            model.addAttribute("isSuperAdmin", false);
            model.addAttribute("isHebergementAdmin", false);
            model.addAttribute("isProprietaire", false);
            model.addAttribute("isEvenementAdmin", false);
            model.addAttribute("notificationCount", 0);
        }
    }
    
    /**
     * Détermine le rôle principal de l'utilisateur pour l'affichage
     */
    private String determinerRole(User user) {
        if (user == null) return "Utilisateur";
        
        // Vérifier les rôles par ordre de priorité
        if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_SUPERADMIN"))) {
            return "Super Admin";
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_HEBERGEMENT_ADMIN"))) {
            return "Admin Hébergement";
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_EVENEMENT_ADMIN"))) {
            return "Admin Événement";
        } else if (user.getRoles().stream().anyMatch(role -> role.getName().equals("ROLE_PROPRIETAIRE"))) {
            return "Propriétaire";
        }
        
        return "Utilisateur";
    }
    
    /**
     * Endpoint pour effacer les messages de session après leur affichage
     */
    @GetMapping("/clear-session-message")
    @ResponseBody
    public String clearSessionMessage(@RequestParam("type") String type, HttpServletRequest request) {
        if ("error".equals(type)) {
            request.getSession().removeAttribute("errorMessage");
        } else if ("success".equals(type)) {
            request.getSession().removeAttribute("successMessage");
        }
        return "{\"success\": true}";
    }
    
    /**
     * Met à jour un hébergement existant
     */
    @PostMapping("/hebergements/update-with-image")
    public String mettreAJourHebergementAvecImage(@ModelAttribute("hebergement") Hebergement hebergement,
                                       @RequestParam("imageFile") MultipartFile imageFile,
                                       HttpServletRequest request) {
        try {
            // Vérifier si l'hébergement existe
            Optional<Hebergement> optionalHebergementExistant = hebergementRepository.findById(hebergement.getId());
            if (!optionalHebergementExistant.isPresent()) {
                request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
                return "redirect:/admin/hebergements";
            }
            
            Hebergement hebergementExistant = optionalHebergementExistant.get();
            
            // Mettre à jour les propriétés de l'hébergement
            hebergementExistant.setNom(hebergement.getNom());
            hebergementExistant.setDescription(hebergement.getDescription());
            hebergementExistant.setAdresse(hebergement.getAdresse());
            hebergementExistant.setType(hebergement.getType());
            
            // Mettre à jour d'autres propriétés disponibles
            if (hebergement.getPrix() != null) {
                hebergementExistant.setPrix(hebergement.getPrix());
            }
            
            if (hebergement.getCapacite() > 0) {
                hebergementExistant.setCapacite(hebergement.getCapacite());
            }
            
            if (hebergement.getNbEtoiles() != null) {
                hebergementExistant.setNbEtoiles(hebergement.getNbEtoiles());
            }
            
            // Gérer l'image si une nouvelle a été téléchargée
            if (!imageFile.isEmpty()) {
                String fileName = StringUtils.cleanPath(imageFile.getOriginalFilename());
                String uploadDir = "uploads/hebergements/" + hebergementExistant.getId();
                
                // Enregistrer la nouvelle image
                Path uploadPath = Paths.get(uploadDir);
                if (!Files.exists(uploadPath)) {
                    Files.createDirectories(uploadPath);
                }
                
                try (InputStream inputStream = imageFile.getInputStream()) {
                    Path filePath = uploadPath.resolve(fileName);
                    Files.copy(inputStream, filePath, StandardCopyOption.REPLACE_EXISTING);
                    
                    // Stocker le chemin de l'image dans la propriété photoUrl
                    String imageUrl = "/uploads/hebergements/" + hebergementExistant.getId() + "/" + fileName;
                    hebergementExistant.setPhotoUrl(imageUrl);
                    
                    // Conserver le type original
                    if (hebergement.getType() != null) {
                        hebergementExistant.setType(hebergement.getType());
                    }
                }
            }
            
            // Enregistrer les modifications
            hebergementRepository.save(hebergementExistant);
            
            request.getSession().setAttribute("successMessage", "Hébergement mis à jour avec succès.");
            return "redirect:/admin/hebergements?success=true";
        } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de la mise à jour de l'hébergement", e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite lors de la mise à jour: " + e.getMessage());
            return "redirect:/admin/hebergements?error=true";
        }
    }
    
    /**
     * Supprime un hébergement avec gestion des erreurs de contrainte avancée
     */
    @PostMapping("/hebergements/delete/{id}")
    public String supprimerHebergement(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Vérifier si l'hébergement existe
            if (!hebergementRepository.existsById(id)) {
                request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
                return "redirect:/admin/hebergements";
            }
            
            // Supprimer l'hébergement
            hebergementRepository.deleteById(id);
            request.getSession().setAttribute("successMessage", "Hébergement supprimé avec succès.");
            return "redirect:/admin/hebergements?success=true";
        } catch (org.springframework.dao.DataIntegrityViolationException e) {
            // Gérer l'erreur de contrainte de clé étrangère avec identification du type de contrainte
            logger.error("Erreur de contrainte lors de la suppression de l'hébergement " + id, e);
            
            String errorMessage = "Impossible de supprimer cet hébergement car il est référencé par d'autres éléments.";
            String errorType = "unknown";
            
            // Obtenir la cause racine pour extraire le message d'erreur SQL réel
            Throwable rootCause = e;
            while (rootCause.getCause() != null) {
                rootCause = rootCause.getCause();
            }
            
            // Analyser le message d'erreur pour identifier le type de contrainte
            String errorDetails = rootCause.getMessage().toLowerCase();
            logger.info("Message d'erreur détaillé: " + errorDetails);
            
            if (errorDetails.contains("notification") || errorDetails.contains("notifications") || 
                errorDetails.contains("fk21was6g4h3omuotj91ica6y35")) { // Identifiant de la contrainte FK pour notifications
                errorType = "notification";
                errorMessage = "Impossible de supprimer cet hébergement car il est référencé par des notifications. Veuillez d'abord supprimer les notifications associées.";
            } else if (errorDetails.contains("reservation") || errorDetails.contains("reservations") || 
                       errorDetails.contains("réservation") || errorDetails.contains("réservations")) {
                errorType = "reservation";
                errorMessage = "Impossible de supprimer cet hébergement car il est référencé par des réservations. Veuillez d'abord supprimer les réservations associées.";
            } else if (errorDetails.contains("evaluation") || errorDetails.contains("evaluations") || 
                       errorDetails.contains("évaluation") || errorDetails.contains("évaluations")) {
                errorType = "evaluation";
                errorMessage = "Impossible de supprimer cet hébergement car il est référencé par des évaluations. Veuillez d'abord supprimer les évaluations associées.";
            }
            
            request.getSession().setAttribute("errorMessage", errorMessage);
            return "redirect:/admin/hebergements?error=constraint&errorType=" + errorType;
        } catch (Exception e) {
            // Gérer les autres types d'erreurs
            logger.error("Erreur lors de la suppression de l'hébergement " + id, e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite lors de la suppression: " + e.getMessage());
            return "redirect:/admin/hebergements?error=general";
        }
    }
    
    /**
     * Supprime toutes les notifications associées à un hébergement, puis supprime l'hébergement
     */
    @PostMapping("/hebergements/delete-with-notifications/{id}")
    public String supprimerHebergementAvecNotifications(@PathVariable Long id, HttpServletRequest request) {
        try {
            // Vérifier si l'hébergement existe
            if (!hebergementRepository.existsById(id)) {
                request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
                return "redirect:/admin/hebergements";
            }
            
            // Supprimer d'abord les notifications associées
            int notificationsCount = notificationService.supprimerNotificationsParHebergement(id);
            
            // Puis supprimer l'hébergement
            hebergementRepository.deleteById(id);
            
            // Message de succès avec le nombre de notifications supprimées
            String successMessage = "Hébergement supprimé avec succès";
            if (notificationsCount > 0) {
                successMessage += ", ainsi que " + notificationsCount + " notification" + (notificationsCount > 1 ? "s" : "") + " associée" + (notificationsCount > 1 ? "s" : "") + ".";
            } else {
                successMessage += ".";
            }
            
            request.getSession().setAttribute("successMessage", successMessage);
            return "redirect:/admin/hebergements?success=true";
        } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de la suppression de l'hébergement et des notifications associées " + id, e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite lors de la suppression: " + e.getMessage());
            return "redirect:/admin/hebergements?error=general";
        }
    }
    
    /**
     * Affiche les détails d'un hébergement
     */
    @GetMapping("/details-hebergement/{id}")
    public String afficherDetailsHebergement(@PathVariable Long id, Model model, HttpServletRequest request) {
        try {
            // Vérifier si l'hébergement existe
            Hebergement hebergement = hebergementRepository.findById(id).orElse(null);
            if (hebergement == null) {
                request.getSession().setAttribute("errorMessage", "Hébergement non trouvé.");
                return "redirect:/admin/db-vendor-hebergement";
            }
            
            // Ajouter les informations sur les rôles
            addRoleInformation(model);
            
            // Ajouter l'hébergement au modèle
            model.addAttribute("hebergement", hebergement);
            
            // Retourner la vue des détails
            return "SuperAdmin/db-vendor-details-hebergement";
        } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de l'affichage des détails de l'hébergement " + id, e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/admin/db-vendor-hebergement";
        }
    }
    
    /**
     * Affiche la page de profil de l'utilisateur
     */
    @GetMapping("/profile")
    public String afficherPageProfil(Model model, HttpServletRequest request) {
        try {
            // Récupérer l'utilisateur connecté
            Authentication auth = SecurityContextHolder.getContext().getAuthentication();
            User currentUser = userRepository.findByEmail(auth.getName());
            
            if (currentUser == null) {
                request.getSession().setAttribute("errorMessage", "Utilisateur non trouvé.");
                return "redirect:/admin";
            }
            
            // Ajouter les informations sur les rôles
            addRoleInformation(model);
            
            // Ajouter l'utilisateur au modèle
            model.addAttribute("user", currentUser);
            
            // Ajouter d'autres informations utiles pour la page de profil
            model.addAttribute("pageTitle", "Profil utilisateur");
            
            // Retourner la vue du profil
            return "SuperAdmin/profile";
        } catch (Exception e) {
            // Gérer les erreurs
            logger.error("Erreur lors de l'affichage de la page de profil", e);
            request.getSession().setAttribute("errorMessage", "Une erreur s'est produite: " + e.getMessage());
            return "redirect:/admin";
        }
    }
}
          