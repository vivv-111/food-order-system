package com.foodorder.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.foodorder.dto.AuthResponse;
import com.foodorder.dto.LoginRequest;
import com.foodorder.dto.RegisterRequest;
import com.foodorder.entity.User;
import com.foodorder.exception.BusinessException;
import com.foodorder.repository.UserRepository;

import jakarta.annotation.PostConstruct;

@Service
public class AuthService {

    private static final String ROLE_ADMIN = "ADMIN";
    private static final String ROLE_USER = "USER";

    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final UserRepository userRepository;
    private final String adminPassword;

    public AuthService(
        PasswordEncoder passwordEncoder,
        JwtService jwtService,
        UserRepository userRepository,
        @Value("${app.admin.default-password:Admin@123}") String adminPassword
    ) {
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.userRepository = userRepository;
        this.adminPassword = adminPassword;
    }

    @PostConstruct
    void initDefaultAdmin() {
        if (userRepository.existsByUserId("1104")) {
            return;
        }

        User admin = new User();
        admin.setUserId("1104");
        admin.setUserName("Admin");
        admin.setEmail("admin@example.com");
        admin.setPasswordHash(passwordEncoder.encode(adminPassword));
        admin.setRole(ROLE_ADMIN);
        userRepository.save(admin);
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByUserId(request.userId())
            .orElseThrow(() -> new BusinessException("Invalid user ID or password"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new BusinessException("Invalid user ID or password");
        }

        String token = jwtService.generateToken(user.getUserId(), user.getRole());
        return new AuthResponse(token, user.getUserId(), user.getRole());
    }

    public AuthResponse register(RegisterRequest request) {
        if (userRepository.existsByUserId(request.userId())) {
            throw new BusinessException("User ID already exists");
        }
        if (userRepository.existsByEmail(request.email())) {
            throw new BusinessException("Email already exists");
        }

        User user = new User();
        user.setUserId(request.userId());
        user.setUserName(request.userName());
        user.setEmail(request.email());
        user.setPasswordHash(passwordEncoder.encode(request.password()));
        user.setRole(ROLE_USER);
        userRepository.save(user);

        String token = jwtService.generateToken(request.userId(), ROLE_USER);
        return new AuthResponse(token, request.userId(), ROLE_USER);
    }
}
