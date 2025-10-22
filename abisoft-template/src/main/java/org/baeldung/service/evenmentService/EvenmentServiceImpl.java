package org.baeldung.service.evenmentService;

import java.util.*;
import java.util.stream.Collectors;
import java.time.DayOfWeek;
import java.time.LocalDate;

import org.baeldung.persistence.model.evenmentModel.Evenment;
import org.baeldung.persistence.model.evenmentModelTarifType.EvenementTarifType;
import org.baeldung.persistence.model.evenmentModelType.EvenmentType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;
import org.baeldung.persistence.repository.EvenmentRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class EvenmentServiceImpl implements EvenmentService {

    @Autowired
    private EvenmentRepository evenmentRepository;

    @Override
    public List<Evenment> getAllEvenements() {
            return evenmentRepository.findAll();
    }

    @Override
    public List<Evenment> getEvenementsByType(EvenmentType type) {
        return evenmentRepository.findByType(type);
    }

    @Override
    public List<EvenmentType> getAllTypes() {
        return Arrays.asList(EvenmentType.values());
    }

    @Override
    public Evenment getEvenmentById(Long id) {
        return evenmentRepository.findById(id).orElse(null);
    }

    @Override
    public List<Evenment> getEvenementsByVille(Ville ville) {
        return evenmentRepository.findByVille(ville);
    }

    @Override
    public List<Evenment> getEvenementsByDate(LocalDate date) {
        return evenmentRepository.findByDateDebut(date);
    }

    @Override
    public List<Evenment> getEvenementsByOrganisateur(String organisateur) {
        return evenmentRepository.findByOrganisateur(organisateur);
    }

    @Override
    public List<Evenment> getEvenementsPopulaires() {
        return evenmentRepository.findByNbEtoilesGreaterThanEqual(4.0); // seuil de popularité
    }

    @Override
    public List<Evenment> getEvenementsActifs(boolean actif) {
        return evenmentRepository.findByActif(actif);
    }

    @Override
    public List<Evenment> getEvenementsParNoteMin(Double noteMin) {
        return evenmentRepository.findByNbEtoilesGreaterThanEqual(noteMin);
    }

    @Override
    public List<Evenment> getEvenementsPayants() {
        return evenmentRepository.findByTarifType(EvenementTarifType.PAYANT);
    }

    @Override
    public List<Evenment> getEvenementsByTarifType(EvenementTarifType tarifType) {
        return evenmentRepository.findByTarifType(tarifType);
    }

    @Override
    public List<Evenment> getEvenementsByDateDebut(LocalDate dateDebut) {
        return evenmentRepository.findByDateDebut(dateDebut);
    }

    @Override
    public List<Evenment> getEvenementsByDateFin(LocalDate dateFin) {
        return evenmentRepository.findByDateFin(dateFin);
    }

    @Override
    public List<Evenment> getEvenementsByDateDebutAndFin(LocalDate dateDebut, LocalDate dateFin) {
        return evenmentRepository.findAll().stream()
                .filter(e -> e.getDateDebut() != null && e.getDateFin() != null
                        && !e.getDateDebut().isBefore(dateDebut)
                        && !e.getDateFin().isAfter(dateFin))
                .collect(Collectors.toList());
    }

    @Override
    public List<Evenment> getEvenementsByCodePostal(String codePostal) {
        return evenmentRepository.findByCodePostal(codePostal);
    }

    @Override
    public Evenment saveEvenment(Evenment evenment) {
        return evenmentRepository.save(evenment);
    }

    @Override
    public void deleteEvenment(Long id) {
        evenmentRepository.deleteById(id);
    }

    @Override
    public List<Evenment> filtrerEvenements(List<String> types, List<String> dates, List<String> prices,
                                            List<Ville> villes, Double minPrice, Double maxPrice) {
        List<Evenment> evenements = getAllEvenements();

        if (types != null && !types.isEmpty()) {
            evenements = evenements.stream()
                    .filter(e -> types.contains(e.getType() != null ? e.getType().name() : null))
                    .collect(Collectors.toList());
        }

        if (dates != null && !dates.isEmpty()) {
            evenements = evenements.stream()
                    .filter(e -> {
                        LocalDate date = e.getDateDebut();
                        LocalDate today = LocalDate.now();

                        if (date == null) return false;
                        if (dates.contains("today") && date.isEqual(today)) return true;
                        if (dates.contains("weekend") && isThisWeekend(date)) return true;
                        if (dates.contains("month") && date.getMonth().equals(today.getMonth())) return true;

                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (prices != null && !prices.isEmpty()) {
            evenements = evenements.stream()
                    .filter(e -> {
                        if (e.getTarifType() == null) return false;
                        if (prices.contains("free") && e.getTarifType() == EvenementTarifType.GRATUIT) return true;
                        if (prices.contains("paid") && e.getTarifType() == EvenementTarifType.PAYANT) return true;
                        return false;
                    })
                    .collect(Collectors.toList());
        }

        if (villes != null && !villes.isEmpty()) {
            evenements = evenements.stream()
                    .filter(e -> e.getVille() != null && villes.stream()
                            .anyMatch(v -> v.getNom().equalsIgnoreCase(e.getVille().getNom())))
                    .collect(Collectors.toList());
        }

        if (minPrice != null) {
            evenements = evenements.stream()
                    .filter(e -> e.getPrix() != null && e.getPrix() >= minPrice)
                    .collect(Collectors.toList());
        }

        if (maxPrice != null) {
            evenements = evenements.stream()
                    .filter(e -> e.getPrix() != null && e.getPrix() <= maxPrice)
                    .collect(Collectors.toList());
        }

        return evenements;
    }

    private boolean isThisWeekend(LocalDate date) {
        DayOfWeek day = date.getDayOfWeek();
        return day == DayOfWeek.SATURDAY || day == DayOfWeek.SUNDAY;
    }

    @Override
    public Ville getVilleByNom(String nom) {
        if (nom == null) return null;
        return evenmentRepository.findAll().stream()
                .map(Evenment::getVille)
                .filter(v -> v != null && v.getNom() != null && v.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Ville> convertToVilles(List<String> nomsVilles) {
        if (nomsVilles == null || nomsVilles.isEmpty()) return List.of();
        return nomsVilles.stream()
                .map(nom -> nom != null ? getVilleByNom(nom.trim()) : null)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public Pay getPayByNom(String nom) {
        if (nom == null) return null;
        return getAllPays().stream()
                .filter(p -> p.getNom() != null && p.getNom().equalsIgnoreCase(nom))
                .findFirst()
                .orElse(null);
    }

    @Override
    public List<Pay> convertToPays(List<String> nomsPays) {
        if (nomsPays == null || nomsPays.isEmpty()) return List.of();
        return nomsPays.stream()
                .map(nom -> nom != null ? getPayByNom(nom.trim()) : null)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    // Tu peux implémenter cette méthode si tu as une table des pays
    @Override
    public List<Pay> getAllPays() {
        return evenmentRepository.findAll().stream()
                .map(Evenment::getPays)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    @Override
    public List<Ville> getAllVilles() {
        return evenmentRepository.findAll().stream()
                .map(Evenment::getVille)
                .filter(Objects::nonNull)
                .distinct()
                .collect(Collectors.toList());
    }

    /*@ModelAttribute("evenementType")
    public EvenmentType[] populateevenementType() {
        return EvenmentType.values();
    }*/
}
