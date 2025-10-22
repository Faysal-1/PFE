package com.maroctbib.modules.auth.dto;

import com.maroctbib.modules.auth.domain.UserRole;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class AuthRequest {
    
    @NotBlank(message = "Email is required")
    @Email(message = "Invalid email format")
    private String email;
    
    @NotBlank(message = "Password is required")
    private String password;
    
    // Champs optionnels pour l'inscription
    private String fullName;
    private String phone;
    private UserRole role; // Optionnel, défaut = PATIENT
}
