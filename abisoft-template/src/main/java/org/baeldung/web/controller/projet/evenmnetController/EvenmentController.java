package org.baeldung.web.controller.projet.evenmnetController;

import org.baeldung.persistence.model.evenmentModel.Evenment;
import org.baeldung.persistence.model.evenmentModelTarifType.EvenementTarifType;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.service.evenmentService.EvenmentService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@Controller
@RequestMapping("/evenements")
public class EvenmentController {

    @Autowired
    private EvenmentService evenmentService;

    @ModelAttribute("evenementTypes")
    public EvenmentType[] populateEvenementTypes() {
        // Conversion explicite au cas où l'appel viendrait du service
        return evenmentService.getAllTypes().toArray(new EvenmentType[0]);
    }

    @GetMapping({ "", "/", "/{type}/list" })
    public String listerEvenementsParType(
            @PathVariable(required = false) EvenmentType type,
            Model model) {

        List<Evenment> evenements = (type != null)
                ? evenmentService.getEvenementsByType(type)
                : evenmentService.getAllEvenements();

        model.addAttribute("evenements", evenements);
        model.addAttribute("evenementTypes", evenmentService.getAllTypes());

        return "evenment/all_evenment_list";
    }

    // Filtrer par villes
    @GetMapping("/location/{ville}")
    public String listerEvenementsParVille(
            @PathVariable String ville,
            Model model) {

        Ville villeObj = evenmentService.getVilleByNom(ville);
        List<Evenment> evenements = evenmentService.getEvenementsByVille(villeObj);

        model.addAttribute("evenements", evenements);
        model.addAttribute("evenementTypes", evenmentService.getAllTypes());
        model.addAttribute("filtre", "Ville : " + ville);

        return "evenment/all_evenment_list";
    }

    // Filtrer par type de tarif
    @GetMapping("/gratuits")
    public String listerEvenementsGratuits(Model model) {
        List<Evenment> evenements = evenmentService.getEvenementsByTarifType(EvenementTarifType.GRATUIT);
        model.addAttribute("evenements", evenements);
        model.addAttribute("evenementTypes", evenmentService.getAllTypes());
        model.addAttribute("filtre", "Événements gratuits");
        return "evenment/all_evenment_list";
    }

    @GetMapping("/payants")
    public String listerEvenementsPayants(Model model) {
        List<Evenment> evenements = evenmentService.getEvenementsByTarifType(EvenementTarifType.PAYANT);
        model.addAttribute("evenements", evenements);
        model.addAttribute("evenementTypes", evenmentService.getAllTypes());
        model.addAttribute("filtre", "Événements payants");
        return "evenment/all_evenment_list";
    }

    // Filtrage combiné avec formulaire HTML (type, date, prix, villes)
    @GetMapping("/filter")
    public String filtrerEvenements(
            @RequestParam(required = false) List<String> type,
            @RequestParam(required = false) List<String> date,
            @RequestParam(required = false) List<String> price,
            @RequestParam(required = false) List<String> location,
            @RequestParam(required = false) Integer minPrice,
            @RequestParam(required = false) Integer maxPrice,
            Model model) {

        // Conversion des noms de villes en objets Ville
        List<Ville> villesFiltrees = (location != null) ? evenmentService.convertToVilles(location) : null;

        List<Evenment> evenementsFiltres = evenmentService.filtrerEvenements(
            type, date, price,
            villesFiltrees,
            minPrice != null ? minPrice.doubleValue() : null,
            maxPrice != null ? maxPrice.doubleValue() : null
        );

        model.addAttribute("evenements", evenementsFiltres);
        model.addAttribute("evenementTypes", evenmentService.getAllTypes());
        model.addAttribute("villes", evenmentService.getAllVilles());
        model.addAttribute("selectedTypes", type);
        model.addAttribute("selectedDates", date);
        model.addAttribute("selectedPrices", price);
        model.addAttribute("selectedLocations", location);
        model.addAttribute("minPrice", minPrice);
        model.addAttribute("maxPrice", maxPrice);

        return "evenment/all_evenment_list";
    }
}
