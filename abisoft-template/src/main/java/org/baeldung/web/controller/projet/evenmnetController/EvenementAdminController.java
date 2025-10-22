package org.baeldung.web.controller.projet.evenmnetController;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import javax.transaction.Transactional;

import org.baeldung.persistence.model.User;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.servlet.ModelAndView;

@Controller
@RequestMapping("/EvenementAdmin")
public class EvenementAdminController {
    
    @Transactional
    @GetMapping(value = "/index")
    public ModelAndView index(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user1");

        if (user != null) {
            ret.addObject("user", user);
        }

        ret.setViewName("EvenementAdmin/index");
        return ret;
    }
    
    @Transactional
    @GetMapping(value = "/db-vendor-evenment")
    public ModelAndView listEvents(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user1");

        if (user != null) {
            ret.addObject("user", user);
        }

        // Ici, vous pourriez ajouter la logique pour récupérer les événements depuis la base de données
        // ret.addObject("evenements", evenementService.getAllEvenements());

        ret.setViewName("EvenementAdmin/db-vendor-evenment");
        return ret;
    }
    
    @Transactional
    @GetMapping(value = "/db-vendor-add-evenment")
    public ModelAndView addEvent(HttpServletRequest request, HttpServletResponse response) throws Exception {
        ModelAndView ret = new ModelAndView();
        HttpSession session = request.getSession();
        User user = (User) session.getAttribute("user1");

        if (user != null) {
            ret.addObject("user", user);
        }

        // Ici, vous pourriez ajouter la logique pour préparer le formulaire d'ajout d'événement
        // ret.addObject("villes", villeService.getAllVilles());

        ret.setViewName("EvenementAdmin/db-vendor-add-evenment");
        return ret;
    }
}
