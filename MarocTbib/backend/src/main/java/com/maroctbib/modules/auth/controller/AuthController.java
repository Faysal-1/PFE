package com.maroctbib.modules.auth.controller;

import com.maroctbib.modules.auth.dto.AuthRequest;
import com.maroctbib.modules.auth.dto.AuthResponse;
import com.maroctbib.modules.auth.dto.UserDto;
import com.maroctbib.modules.auth.mapper.UserMapper;
import com.maroctbib.modules.auth.security.JwtTokenProvider;
import com.maroctbib.modules.auth.service.AuthService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider tokenProvider;
    private final AuthService authService;
    private final UserMapper userMapper;
    
    public AuthController(AuthenticationManager authenticationManager, 
                         JwtTokenProvider tokenProvider,
                         AuthService authService,
                         UserMapper userMapper) {
        this.authenticationManager = authenticationManager;
        this.tokenProvider = tokenProvider;
        this.authService = authService;
        this.userMapper = userMapper;
    }

    @PostMapping("/login")
    public ResponseEntity<AuthResponse> authenticateUser(@Valid @RequestBody AuthRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                loginRequest.getEmail(),
                loginRequest.getPassword()
            )
        );

        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        UserDto userDto = userMapper.toDto((com.maroctbib.modules.auth.domain.User) authentication.getPrincipal());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(tokenProvider.getJwtExpirationMs() / 1000) // Convert to seconds
                .refreshToken(refreshToken)
                .refreshExpiresIn(tokenProvider.getJwtRefreshExpirationMs() / 1000) // Convert to seconds
                .user(userDto)
                .build());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthResponse> registerUser(@Valid @RequestBody AuthRequest registrationRequest) {
        UserDto userDto = authService.registerUser(registrationRequest);
        
        // Auto login after registration
        Authentication authentication = authenticationManager.authenticate(
            new UsernamePasswordAuthenticationToken(
                registrationRequest.getEmail(),
                registrationRequest.getPassword()
            )
        );
        
        SecurityContextHolder.getContext().setAuthentication(authentication);
        
        String accessToken = tokenProvider.generateAccessToken(authentication);
        String refreshToken = tokenProvider.generateRefreshToken(authentication);
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(accessToken)
                .expiresIn(tokenProvider.getJwtExpirationMs() / 1000)
                .refreshToken(refreshToken)
                .refreshExpiresIn(tokenProvider.getJwtRefreshExpirationMs() / 1000)
                .user(userDto)
                .build());
    }

    @PostMapping("/refresh")
    public ResponseEntity<AuthResponse> refreshToken(@RequestHeader("Authorization") String refreshToken) {
        String token = refreshToken.substring(7); // Remove "Bearer " prefix
        
        if (!tokenProvider.validateToken(token)) {
            return ResponseEntity.badRequest().build();
        }
        
        String userId = tokenProvider.getUserIdFromJWT(token);
        UserDto userDto = authService.getUserById(userId);
        
        // Generate new tokens
        String newAccessToken = tokenProvider.generateAccessToken(SecurityContextHolder.getContext().getAuthentication());
        String newRefreshToken = tokenProvider.generateRefreshToken(SecurityContextHolder.getContext().getAuthentication());
        
        return ResponseEntity.ok(AuthResponse.builder()
                .accessToken(newAccessToken)
                .expiresIn(tokenProvider.getJwtExpirationMs() / 1000)
                .refreshToken(newRefreshToken)
                .refreshExpiresIn(tokenProvider.getJwtRefreshExpirationMs() / 1000)
                .user(userDto)
                .build());
    }

    @PostMapping("/logout")
    public ResponseEntity<Void> logout() {
        // In a real app, you might want to invalidate the token
        SecurityContextHolder.clearContext();
        return ResponseEntity.ok().build();
    }
}
