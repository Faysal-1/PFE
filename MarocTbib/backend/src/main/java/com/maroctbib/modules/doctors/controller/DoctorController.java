package com.maroctbib.modules.doctors.controller;

import com.maroctbib.modules.doctors.domain.DoctorProfile;
import com.maroctbib.modules.doctors.repository.DoctorProfileRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@RestController
@RequestMapping("/doctors")
@RequiredArgsConstructor
@CrossOrigin(origins = "*")
public class DoctorController {

    private final DoctorProfileRepository doctorProfileRepository;

    @GetMapping
    public ResponseEntity<List<Map<String, Object>>> getAllDoctors() {
        List<DoctorProfile> doctors = doctorProfileRepository.findAllVerifiedWithSpecialty();
        
        List<Map<String, Object>> doctorDtos = doctors.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(doctorDtos);
    }
    
    @GetMapping("/search")
    public ResponseEntity<List<Map<String, Object>>> searchDoctors(
            @RequestParam(required = false) String specialty,
            @RequestParam(required = false) String city) {
        
        List<DoctorProfile> doctors;
        
        if (specialty != null && !specialty.isEmpty()) {
            doctors = doctorProfileRepository.findBySpecialtyName(specialty);
        } else if (city != null && !city.isEmpty()) {
            doctors = doctorProfileRepository.findByCity(city);
        } else {
            doctors = doctorProfileRepository.findAllVerifiedWithSpecialty();
        }
        
        List<Map<String, Object>> doctorDtos = doctors.stream()
            .map(this::convertToDto)
            .collect(Collectors.toList());
            
        return ResponseEntity.ok(doctorDtos);
    }
    
    private Map<String, Object> convertToDto(DoctorProfile doctor) {
        return Map.of(
            "id", doctor.getId().toString(),
            "fullName", doctor.getFullName(),
            "name", doctor.getFullName(),
            "specialty", doctor.getSpecialty().getName(),
            "city", doctor.getCity(),
            "address", doctor.getAddress() != null ? doctor.getAddress() : "",
            "phone", doctor.getPhone() != null ? doctor.getPhone() : "",
            "bio", doctor.getBio() != null ? doctor.getBio() : "",
            "verified", doctor.isVerified(),
            "availableSlots", List.of("09:00", "10:00", "14:00", "15:00") // Slots par défaut
        );
    }
}
