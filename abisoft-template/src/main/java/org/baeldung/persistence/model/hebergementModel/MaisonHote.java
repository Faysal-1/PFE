package org.baeldung.persistence.model.hebergementModel;

import javax.persistence.*;
import lombok.*;

/**
 * Représente une maison d'hôte.
 */
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
@ToString(callSuper = true)
@Table(name = "maison_hotes")
public class MaisonHote extends Hebergement {

    @Column(name = "petit_dejeuner_inclus", nullable = false)
    private boolean petitDejeunerInclus;

    @Column(name = "accueil_famille", nullable = false)
    private boolean accueilFamille;
}
