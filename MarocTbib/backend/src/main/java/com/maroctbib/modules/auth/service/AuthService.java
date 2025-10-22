package com.maroctbib.modules.auth.service;

import com.maroctbib.modules.auth.domain.User;
import com.maroctbib.modules.auth.domain.UserRole;
import com.maroctbib.modules.auth.dto.AuthRequest;
import com.maroctbib.modules.auth.dto.UserDto;
import com.maroctbib.modules.auth.repository.UserRepository;
import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    
    public AuthService(UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @Transactional
    public UserDto registerUser(AuthRequest request) {
        log.info("Tentative d'inscription pour l'email: {}", request.getEmail());
        
        // Vérifier si l'email existe déjà
        if (userRepository.existsByEmail(request.getEmail())) {
            log.warn("Échec de l'inscription: Email déjà utilisé - {}", request.getEmail());
            throw new IllegalArgumentException("Email déjà utilisé");
        }

        try {
            User user = new User();
            user.setEmail(request.getEmail());
            user.setPassword(passwordEncoder.encode(request.getPassword()));
            user.setRole(UserRole.PATIENT);
            user.setEnabled(true);

            User saved = userRepository.save(user);
            log.info("Utilisateur créé avec succès: {}", saved.getEmail());

            return UserDto.builder()
                    .id(saved.getId())
                    .email(saved.getEmail())
                    .role(saved.getRole())
                    .enabled(saved.isEnabled())
                    .build();
                    
        } catch (Exception e) {
            log.error("Erreur lors de l'inscription: {}", e.getMessage(), e);
            throw new RuntimeException("Erreur lors de l'inscription: " + e.getMessage());
        }
    }

    public UserDto getUserById(String email) {
        User user = userRepository.findByEmail(email)
                .orElseThrow(() -> new IllegalArgumentException("User not found"));
        
        return UserDto.builder()
                .id(user.getId())
                .email(user.getEmail())
                .role(user.getRole())
                .enabled(user.isEnabled())
                .build();
    }
}
