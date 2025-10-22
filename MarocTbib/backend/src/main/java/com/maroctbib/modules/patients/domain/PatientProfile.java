package com.maroctbib.modules.patients.domain;

import com.maroctbib.modules.auth.domain.User;
import com.maroctbib.modules.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;

@Entity
@Table(name = "patient_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PatientProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @Column(name = "birth_date")
    private LocalDate birthDate;
    
    private String phone;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof PatientProfile)) return false;
        return id != null && id.equals(((PatientProfile) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
