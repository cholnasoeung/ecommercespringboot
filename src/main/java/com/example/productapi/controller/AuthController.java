package com.example.productapi.controller;

import com.example.productapi.dto.AuthRequest;
import com.example.productapi.dto.AuthResponse;
import com.example.productapi.entity.Role;
import com.example.productapi.entity.User;
import com.example.productapi.repository.UserRepository;
import com.example.productapi.security.JwtService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    @PostMapping("/register")
    public String register(@RequestBody AuthRequest request) {
        User user = User.builder()
                .email(request.email())
                .password(passwordEncoder.encode(request.password()))
                .role(Role.ROLE_USER)
                .build();
        userRepository.save(user);
        return "User registered successfully";
    }

    @PostMapping("/login")
    public AuthResponse login(@RequestBody AuthRequest request) {
        User user = userRepository.findByEmail(request.email())
                .orElseThrow(() -> new RuntimeException("User not found"));

        if (passwordEncoder.matches(request.password(), user.getPassword())) {
            String token = jwtService.generateToken(user.getEmail(), user.getRole().name());
            return new AuthResponse(token);
        } else {
            throw new RuntimeException("Invalid password");
        }
    }
}