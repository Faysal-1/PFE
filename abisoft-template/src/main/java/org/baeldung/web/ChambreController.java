package org.baeldung.web;

import org.baeldung.service.ChambreService;
import org.baeldung.persistence.model.hebergementModel.Chambre;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import java.util.List;

@Controller
@RequestMapping("/chambres")
public class ChambreController {

    @Autowired
    private ChambreService chambreService;

    @GetMapping("/liste")
    public String afficherChambres(Model model) {
        List<Chambre> chambres = chambreService.getToutesLesChambres();
        model.addAttribute("chambres", chambres);
        return "liste_chambres";
    }

    @GetMapping("/hebergement/{id}")
    public String afficherChambresParHebergement(@PathVariable("id") Long hebergementId, Model model) {
        List<Chambre> chambres = chambreService.getChambresParHebergement(hebergementId);
        model.addAttribute("chambres", chambres);
        return "simple_hebergement";
    }
}
