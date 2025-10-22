package org.baeldung.persistence.model.hebergementModel;

import javax.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;

@Entity
@Data
@NoArgsConstructor
@Table(name = "hebergement_image")
public class HebergementImage {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, length = 1000)
    private String url;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "hebergement_id", nullable = false)
    private Hebergement hebergement;

    public HebergementImage(String url, Hebergement hebergement) {
        this.url = url;
        this.hebergement = hebergement;
    }
}
