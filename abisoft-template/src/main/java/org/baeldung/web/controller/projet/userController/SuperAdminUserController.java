package org.baeldung.web.controller.projet.userController;  
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.repository.PayRepository;
import org.baeldung.persistence.repository.VilleRepository;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.payModel.Pay;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.WebDataBinder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.InitBinder;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.beans.PropertyEditorSupport;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

@Controller
@RequestMapping("/SuperAdmin")
public class SuperAdminUserController {

    private final Logger logger = LoggerFactory.getLogger(getClass());

    @Autowired
    private UserRepository userRepository;
    
    @Autowired
    private RoleRepository roleRepository;
    
    @Autowired
    private PayRepository payRepository;
    
    @Autowired
    private PasswordEncoder passwordEncoder;
    
    @Autowired
    private VilleRepository villeRepository;
    
    private static final DateTimeFormatter DATE_FORMAT = DateTimeFormatter.ofPattern("yyyy-MM-dd");
    
    /**
     * Configure le convertisseur de dates pour ce contrôleur
     * @param binder Le binder de données web
     */
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

    /**
     * Affiche la liste de tous les utilisateurs
     * @param model Le modèle pour la vue
     * @return La page de liste des utilisateurs
     */
    @GetMapping("/db-vendor-user")
    public String afficherUtilisateurs(Model model) {
        List<User> users = userRepository.findAll();
        model.addAttribute("users", users);
        return "SuperAdmin/db-vendor-user";
    }

    /**
     * Affiche le formulaire d'ajout d'un utilisateur
     * @param model Le modèle pour la vue
     * @return La page du formulaire d'ajout
     */
    @GetMapping("/db-vendor-add-user")
    public String afficherFormulaireAjout(Model model) {
        // Préparer un nouvel utilisateur et les listes pour les dropdowns
        model.addAttribute("user", new User());
        model.addAttribute("pays", payRepository.findAll());
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "SuperAdmin/db-vendor-add-user";
    }
    /**
     * Traite l'ajout d'un nouvel utilisateur
     * @param user L'utilisateur à ajouter
     * @param roleIds Les IDs des rôles à attribuer
     * @param request La requête HTTP
     * @param redirectAttributes Les attributs de redirection
     * @param model Le modèle pour la vue
     * @return La redirection vers la liste des utilisateurs ou le formulaire en cas d'erreur
     */
    @PostMapping("/db-vendor-add-user")
    public String ajouterUtilisateur(
            User user, 
            @RequestParam("roleIds") List<Long> roleIds,
            HttpServletRequest request, 
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Vérifier si l'email existe déjà
            User existingUser = userRepository.findByEmail(user.getEmail());
            if (existingUser != null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Un compte existe déjà avec cet email: " + user.getEmail());
                return "redirect:/SuperAdmin/db-vendor-add-user";
            }
            
            // Encoder le mot de passe
            user.setPassword(passwordEncoder.encode(user.getPassword()));
            
            // Activer le compte par défaut
            user.setEnabled(true);
            
            // Assigner les rôles
            Collection<Role> roles = new ArrayList<>();
            for (Long roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(roles::add);
            }
            user.setRoles(roles);
            
            // Sauvegarder l'utilisateur
            userRepository.save(user);
            
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur ajouté avec succès");
            return "redirect:/SuperAdmin/db-vendor-user";
        } catch (Exception e) {
            logger.error("Erreur de sauvegarde", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la sauvegarde: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/SuperAdmin/db-vendor-add-user";
        }
    }

    /**
     * Affiche le formulaire de modification d'un utilisateur
     * @param id L'ID de l'utilisateur à modifier
     * @param model Le modèle pour la vue
     * @return La page du formulaire de modification
     */
    @GetMapping("/users/edit/{id}")
    public String afficherFormulaireModification(@PathVariable Long id, Model model) {
        User user = userRepository.findById(id).orElse(null);
        if (user == null) {
            return "redirect:/SuperAdmin/db-vendor-user";
        }
        model.addAttribute("user", user);
        model.addAttribute("pays", payRepository.findAll());
        model.addAttribute("villes", villeRepository.findAll());
        model.addAttribute("roles", roleRepository.findAll());
        return "SuperAdmin/db-vendor-add-user";
    }
    
    /**
     * Traite la modification d'un utilisateur
     * @param user L'utilisateur modifié
     * @param roleIds Les IDs des rôles à attribuer
     * @param request La requête HTTP
     * @param redirectAttributes Les attributs de redirection
     * @param model Le modèle pour la vue
     * @return La redirection vers la liste des utilisateurs ou le formulaire en cas d'erreur
     */
    @PostMapping("/users/edit/{id}")
    public String modifierUtilisateur(
            User user, 
            @RequestParam("roleIds") List<Long> roleIds,
            @PathVariable Long id,
            HttpServletRequest request, 
            RedirectAttributes redirectAttributes,
            Model model) {
        try {
            // Vérifier si l'utilisateur existe
            User existingUser = userRepository.findById(id).orElse(null);
            if (existingUser == null) {
                redirectAttributes.addFlashAttribute("errorMessage", "Utilisateur non trouvé");
                return "redirect:/SuperAdmin/db-vendor-user";
            }
            
            // Vérifier si l'email est déjà utilisé par un autre utilisateur
            User userWithSameEmail = userRepository.findByEmail(user.getEmail());
            if (userWithSameEmail != null && !userWithSameEmail.getId().equals(id)) {
                redirectAttributes.addFlashAttribute("errorMessage", "Cet email est déjà utilisé par un autre compte");
                return "redirect:/SuperAdmin/users/edit/" + id;
            }
            
            // Mettre à jour les informations de l'utilisateur
            existingUser.setFirstName(user.getFirstName());
            existingUser.setLastName(user.getLastName());
            existingUser.setEmail(user.getEmail());
            existingUser.setPhone(user.getPhone());
            existingUser.setAdresse(user.getAdresse());
            existingUser.setVille(user.getVille());
            existingUser.setCodePostal(user.getCodePostal());
            existingUser.setPays(user.getPays());
            existingUser.setDateNaissance(user.getDateNaissance());
            
            // Mettre à jour le mot de passe si fourni
            if (user.getPassword() != null && !user.getPassword().isEmpty()) {
                existingUser.setPassword(passwordEncoder.encode(user.getPassword()));
            }
            
            // Mettre à jour les rôles
            Collection<Role> roles = new ArrayList<>();
            for (Long roleId : roleIds) {
                roleRepository.findById(roleId).ifPresent(roles::add);
            }
            existingUser.setRoles(roles);
            
            // Sauvegarder les modifications
            userRepository.save(existingUser);
            
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur modifié avec succès");
            return "redirect:/SuperAdmin/db-vendor-user";
        } catch (Exception e) {
            logger.error("Erreur de mise à jour", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la mise à jour: " + e.getMessage());
            e.printStackTrace();
            return "redirect:/SuperAdmin/users/edit/" + id;
        }
    }

    /**
     * Supprime un utilisateur
     * @param id L'ID de l'utilisateur à supprimer
     * @return La redirection vers la liste des utilisateurs
     */
    @PostMapping("/users/delete/{id}")
    public String supprimerUtilisateur(@PathVariable Long id, RedirectAttributes redirectAttributes) {
        try {
            userRepository.deleteById(id);
            redirectAttributes.addFlashAttribute("successMessage", "Utilisateur supprimé avec succès");
        } catch (Exception e) {
            logger.error("Erreur de suppression", e);
            redirectAttributes.addFlashAttribute("errorMessage", "Erreur lors de la suppression: " + e.getMessage());
        }
        return "redirect:/SuperAdmin/db-vendor-user";
    }
}
