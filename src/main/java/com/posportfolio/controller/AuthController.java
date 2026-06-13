package com.posportfolio.controller;

import com.posportfolio.dto.AuthRequest;
import com.posportfolio.dto.AuthResponse;
import com.posportfolio.dto.SignupRequest;
import com.posportfolio.model.Role;
import com.posportfolio.model.UserEntity;
import com.posportfolio.repository.UserRepository;
import com.posportfolio.security.JwtUtils;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtUtils jwtUtils;

    public AuthController(AuthenticationManager authenticationManager,
                          UserRepository userRepository,
                          PasswordEncoder passwordEncoder,
                          JwtUtils jwtUtils) {
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtUtils = jwtUtils;
    }

    @PostMapping("/signin")
    public ResponseEntity<AuthResponse> authenticate(@RequestBody AuthRequest request) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.username(), request.password())
        );

        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid username"));

        String token = jwtUtils.generateToken(user.getUsername(), user.getRole().name());
        return ResponseEntity.ok(new AuthResponse(token, "Bearer", user.getRole().name()));
    }

    @PreAuthorize("hasAuthority('ROLE_ADMIN')")
    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody SignupRequest request) {
        if (request.username() == null || request.username().trim().isEmpty()) {
            return ResponseEntity.badRequest().body("Username is required");
        }

        if (request.password() == null || request.password().isEmpty()) {
            return ResponseEntity.badRequest().body("Password is required");
        }

        if (request.password().equals(request.username())) {
            return ResponseEntity.badRequest().body("Password must not be the same as username");
        }

        if (!request.password().matches(".*[A-Z].*") || !request.password().matches(".*\\d.*")) {
            return ResponseEntity.badRequest().body("Password must contain at least one uppercase letter and one number");
        }

        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        Role role = request.role() != null ? request.role() : Role.ROLE_USER;
        UserEntity user = new UserEntity(
                request.username(),
                passwordEncoder.encode(request.password()),
                role
        );
        userRepository.save(user);
        return ResponseEntity.ok("User created successfully");
    }
}
