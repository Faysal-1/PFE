package com.maroctbib.modules.doctors.domain;

import com.maroctbib.modules.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "specialties", uniqueConstraints = {
    @UniqueConstraint(columnNames = "name")
})
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Specialty extends BaseEntity {
    
    @Column(nullable = false, unique = true)
    private String name;
    
    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof Specialty)) return false;
        return id != null && id.equals(((Specialty) o).getId());
    }
    
    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
