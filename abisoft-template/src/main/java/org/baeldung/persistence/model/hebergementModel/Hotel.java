package org.baeldung.persistence.model.hebergementModel;

import javax.persistence.Entity;
import javax.persistence.Table;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Entity
@NoArgsConstructor
@Table(name = "hotel")
public class Hotel extends Hebergement {
    private Integer nombreEtoiles;
    private Boolean piscine;
    private Boolean wifi;
}
