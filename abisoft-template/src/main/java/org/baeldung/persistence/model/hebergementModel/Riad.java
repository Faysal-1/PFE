package org.baeldung.persistence.model.hebergementModel;


import javax.persistence.Entity;
import lombok.*;

@Data
@Entity
@NoArgsConstructor
public class Riad extends Hebergement {

    private boolean patio;
    private boolean hammam;
}
