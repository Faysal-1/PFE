package org.baeldung.web.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collection;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.baeldung.persistence.dao.RoleRepository;
import org.baeldung.persistence.dao.UserRepository;
import org.baeldung.persistence.model.Role;
import org.baeldung.persistence.model.User;
import org.baeldung.persistence.model.VerificationToken;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.service.MailClient;
import org.baeldung.service.UserService;
import org.baeldung.service.NotificationService;

import org.baeldung.web.dto.MessageDto;
import org.baeldung.web.util.MailContent;
import org.baeldung.persistence.model.notification.Notification;
import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.oauth2.core.oidc.user.DefaultOidcUser;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.ModelAndView;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.http.MediaType;
import java.util.Collections;

@Controller
public class HomeController {

    @Autowired
    private UserService userService;
    
    @Autowired
    private NotificationService notificationService;

    // @GetMapping("/") public String root() { return "index"; }
    @Autowired
    private MailClient mailClient;
    @Autowired
    private MessageSource messages;
    @Autowired
    private UserRepository userRepository;

    @GetMapping({"/", "/index"})
    public String index(Model model, @RequestParam(value = "force", required = false) Boolean force, HttpSession session) {
        // Si le paramètre force=true est présent, rediriger vers la page de profil
        if (force != null && force) {
            System.out.println("Redirection forcée vers la page de profil depuis index");
            return "redirect:/profile";
        }
        
        // Vérifier si l'utilisateur est connecté
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        boolean isAuthenticated = auth != null && auth.isAuthenticated() && !"".equals(auth.getName()) && !"anonymousUser".equals(auth.getName());
        model.addAttribute("isAuthenticated", isAuthenticated);
        
        // Ajouter l'utilisateur connecté au modèle si disponible
        if (isAuthenticated) {
            Object currentUser = session.getAttribute("user");
            model.addAttribute("connectedUser", currentUser);
        }
        
        return "index";
    }

    // ta méthode wxAutoLogin, sans se soucier d'ajouter evenementType
    public ModelAndView wxAutoLogin(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();

        ret.setViewName("index");
        // Pas besoin de faire ret.addObject("evenementType", ...) ici, c'est automatique grâce à @ModelAttribute

        return ret;
    }


    @GetMapping("/login")
    public ModelAndView wxAutoLogin2(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();
        
        // Vérifier si l'utilisateur est déjà authentifié
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        if (auth != null && auth.isAuthenticated() && !"anonymousUser".equals(auth.getName())) {
            // Utilisateur déjà connecté, rediriger vers la page appropriée
            Collection<? extends GrantedAuthority> authorities = auth.getAuthorities();
            for (GrantedAuthority authority : authorities) {
                String role = authority.getAuthority();
                if (role.equals("SUPERADMIN_PRIVILEGE") || role.equals("ADMIN_PRIVILEGE") || 
                    role.equals("EVENEMENT_ADMIN_PRIVILEGE") || role.equals("HEBERGEMENT_ADMIN_PRIVILEGE") || 
                    role.equals("PROPRIETAIRE_PRIVILEGE")) {
                    ret.setViewName("redirect:/admin/index");
                    return ret;
                } else if (role.equals("CLIENT_PRIVILEGE")) {
                    ret.setViewName("redirect:/index");
                    return ret;
                }
            }
            // Si aucun rôle spécifique n'est trouvé mais l'utilisateur est authentifié
            ret.setViewName("redirect:/profile");
            return ret;
        }
        
        ret.setViewName("login");
        return ret;
    }

    @GetMapping("register")
    public ModelAndView wxAutoLogin1(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();

        ret.setViewName("register");

        return ret;
    }

    @GetMapping("/Admin/index")
    public String adminIndex(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();

        ret.setViewName("SuperAdmin/index");

        return "SuperAdmin/index";
    }       

    @ModelAttribute("evenementType")
    public EvenmentType[] populateEvenementTypes() {
        return EvenmentType.values();
    }
    
    @ModelAttribute("hebergementType")
    public HebergementType[] populateHebergementTypes() {
        return HebergementType.values();
    }       
}