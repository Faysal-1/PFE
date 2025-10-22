package com.maroctbib.modules.doctors.domain;

import com.maroctbib.modules.auth.domain.User;
import com.maroctbib.modules.core.domain.BaseEntity;
import jakarta.persistence.*;
import lombok.*;

import java.util.UUID;

@Entity
@Table(name = "doctor_profiles")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class DoctorProfile extends BaseEntity {

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false, unique = true)
    private User user;

    @Column(name = "full_name", nullable = false)
    private String fullName;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "specialty_id", nullable = false)
    private Specialty specialty;

    @Column(nullable = false)
    private String city;

    private String address;
    
    private String phone;
    
    private String bio;
    
    @Column(nullable = false)
    private boolean verified = false;

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (!(o instanceof DoctorProfile)) return false;
        return id != null && id.equals(((DoctorProfile) o).getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
