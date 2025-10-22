package org.baeldung.persistence.model.hebergementModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "appartement")
public class Appartement extends Hebergement {

    private Boolean wifi;
    private Boolean climatisation;
    private Boolean cuisineEquipee;
    private String proprietaireContact;
}
