package com.posportfolio.controller;

import com.posportfolio.dto.AuthRequest;
import com.posportfolio.dto.AuthResponse;
import com.posportfolio.dto.SignupRequest;
import com.posportfolio.model.Role;
import com.posportfolio.model.UserEntity;
import com.posportfolio.repository.UserRepository;
import com.posportfolio.security.JwtUtils;
import org.springframework.http.ResponseEntity;
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

        String token = jwtUtils.generateToken(authentication.getName());
        return ResponseEntity.ok(new AuthResponse(token, "Bearer"));
    }

    @PostMapping("/signup")
    public ResponseEntity<String> register(@RequestBody SignupRequest request) {
        if (userRepository.existsByUsername(request.username())) {
            return ResponseEntity.badRequest().body("Username is already taken");
        }

        UserEntity user = new UserEntity(
                request.username(),
                passwordEncoder.encode(request.password()),
                Role.ROLE_USER
        );
        userRepository.save(user);
        return ResponseEntity.ok("User registered successfully");
    }
}
