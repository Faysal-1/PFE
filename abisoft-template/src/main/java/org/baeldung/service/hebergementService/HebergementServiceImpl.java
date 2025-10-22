package org.baeldung.service.hebergementService;

import org.baeldung.persistence.model.hebergementModel.Hebergement;
import org.baeldung.persistence.model.hebergementModelTarifType.HebergementTarifType;
import org.baeldung.persistence.model.hebergementModelType.HebergementType;
import org.baeldung.persistence.model.villeModel.Ville;
import org.baeldung.persistence.model.payModel.Pay;

import org.baeldung.persistence.repository.HebergementRepository;
import org.baeldung.persistence.repository.VilleRepository;
import org.baeldung.persistence.repository.PayRepository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.DayOfWeek;
import java.time.LocalDate;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional
public class HebergementServiceImpl implements HebergementService {

    @Autowired
    private HebergementRepository hebergementRepository;

    @Autowired
    private VilleRepository villeRepository;

    @Autowired
    private PayRepository payRepository;

    @Override
    public List<Hebergement> getAllHebergement() {
        return hebergementRepository.findAll();
    }

    @Override
    public Hebergement getHebergementById(Long id) {
        return hebergementRepository.findById(id).orElse(null);
    }

    @Override
    public Hebergement saveHebergement(Hebergement hebergement) {
        return hebergementRepository.save(hebergement);
    }

    @Override
    public void deleteHebergement(Long id) {
        if (hebergementRepository.existsById(id)) {
            hebergementRepository.deleteById(id);
        }
        // Sinon, on pourrait lancer une exception si besoin
    }

    @Override
    public List<Hebergement> getHebergementByType(HebergementType type) {
        return hebergementRepository.findByHebergementType(type);
    }

    @Override
    public List<HebergementType> getAllTypes() {
        return java.util.Arrays.asList(HebergementType.values());
    }

    @Override
    public List<Hebergement> getHebergementByVille(Ville ville) {
        if (ville == null) return List.of();
        return hebergementRepository.findByVille(ville);
    }

    @Override
    public List<Hebergement> getHebergementByDate(LocalDate date) {
        if (date == null) return List.of();
        return hebergementRepository.findByDateDebutLessThanEqualAndDateFinGreaterThanEqual(date, date);
    }

    @Override
    public List<Hebergement> getHebergementByOrganisateur(String organisateur) {
        if (organisateur == null || organisateur.isEmpty()) return List.of();
        // Utiliser la méthode du repository qui recherche par email du propriétaire
        return hebergementRepository.findByProprietaireEmailContainingIgnoreCase(organisateur);
    }

    @Override
    public List<Hebergement> getHebergementPopulaires() {
        // Utiliser une approche alternative pour trouver les hébergements populaires
        return hebergementRepository.findAll().stream()
            .filter(h -> h.getPopulaire() != null && h.getPopulaire())
            .collect(Collectors.toList());
    }

    @Override
    public List<Hebergement> getHebergementActifs(boolean actif) {
        return hebergementRepository.findByActif(actif);
    }

    @Override
    public List<Hebergement> getHebergementParNoteMin(Double noteMin) {
        if (noteMin == null) return List.of();
        return hebergementRepository.findAll().stream()
            .filter(h -> h.getNbEtoiles() != null && h.getNbEtoiles() >= noteMin)
            .collect(Collectors.toList());
    }


    @Override
    public List<Hebergement> getHebergementPayants() {
        return hebergementRepository.findAll().stream().filter(h -> h.getPrix() != null && h.getPrix() > 0).collect(Collectors.toList());
    }

    @Override
    public List<Hebergement> getHebergementByTarifType(HebergementTarifType tarifType) {
        if (tarifType == null) return List.of();
        return hebergementRepository.findByTarifType(tarifType);
    }

    @Override
    public List<Hebergement> getHebergementByDateDebut(LocalDate dateDebut) {
        if (dateDebut == null) return List.of();
        return hebergementRepository.findByDateDebut(dateDebut);
    }

    @Override
    public List<Hebergement> getHebergementByDateFin(LocalDate dateFin) {
        if (dateFin == null) return List.of();
        return hebergementRepository.findByDateFin(dateFin);
    }

    @Override
    public List<Hebergement> getHebergementByDateDebutAndFin(LocalDate dateDebut, LocalDate dateFin) {
        if (dateDebut == null || dateFin == null) return List.of();
        return hebergementRepository.findByDateDebutGreaterThanEqualAndDateFinLessThanEqual(dateDebut, dateFin);
    }

    @Override
    public List<Hebergement> getHebergementByCodePostal(String codePostal) {
        if (codePostal == null || codePostal.isEmpty()) return List.of();
        return hebergementRepository.findByCodePostal(codePostal);
    }


    // --- Gestion Villes ---

    @Override
    public List<Ville> convertToVilles(List<String> nomsVilles) {
        if (nomsVilles == null || nomsVilles.isEmpty()) return java.util.List.of();
        return nomsVilles.stream()
            .map(nom -> nom != null ? getVilleByNom(nom.trim()) : null)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Ville> getAllVilles() {
        return villeRepository.findAll();
    }

    @Override
    public Ville getVilleByNom(String nom) {
        if (nom == null || nom.isEmpty()) return null;
        return villeRepository.findByNomIgnoreCase(nom).orElse(null);
    }

    // --- Gestion Pays ---

    @Override
    public List<Pay> convertToPays(List<String> nomsPays) {
        if (nomsPays == null || nomsPays.isEmpty()) return java.util.List.of();
        return nomsPays.stream()
            .map(nom -> nom != null ? getPayByNom(nom.trim()) : null)
            .filter(java.util.Objects::nonNull)
            .distinct()
            .collect(java.util.stream.Collectors.toList());
    }

    @Override
    public List<Pay> getAllPays() {
        return payRepository.findAll();
    }

    @Override
    public Pay getPayByNom(String nom) {
        if (nom == null || nom.isEmpty()) return null;
        return payRepository.findByNomIgnoreCase(nom).orElse(null);
    }
}
