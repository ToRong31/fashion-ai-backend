package com.torome.store.user;

import com.torome.store.common.exception.ResourceNotFoundException;
import com.torome.store.config.JwtService;
import com.torome.store.user.dto.*;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.HashMap;
import java.util.Map;

@Service
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;

    public UserService(UserRepository userRepository, PasswordEncoder passwordEncoder, JwtService jwtService) {
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
    }

    @Transactional
    public LoginResponse register(RegisterRequest request) {
        if (userRepository.findByUsername(request.username()).isPresent()) {
            throw new IllegalArgumentException("Username already exists");
        }

        UserEntity user = new UserEntity(
                request.username(),
                passwordEncoder.encode(request.password())
        );
        user.setCreatedAt(java.time.Instant.now());
        user.setPreferences(new HashMap<>());
        userRepository.save(user);

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(user.getId(), user.getUsername(), token);
    }

    public LoginResponse login(LoginRequest request) {
        UserEntity user = userRepository.findByUsername(request.username())
                .orElseThrow(() -> new IllegalArgumentException("Invalid credentials"));

        if (!passwordEncoder.matches(request.password(), user.getPasswordHash())) {
            throw new IllegalArgumentException("Invalid credentials");
        }

        String token = jwtService.generateToken(user.getId(), user.getUsername());
        return new LoginResponse(user.getId(), user.getUsername(), token);
    }

    @Transactional(readOnly = true)
    public UserResponse getUser(Long userId) {
        UserEntity user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));
        return new UserResponse(user.getId(), user.getUsername(), user.getPreferences());
    }

    @Transactional
    public UserResponse updateProfile(UpdateProfileRequest request) {
        UserEntity user = userRepository.findById(request.userId())
                .orElseThrow(() -> new ResourceNotFoundException("User not found"));

        Map<String, Object> merged = new HashMap<>(user.getPreferences());
        merged.putAll(request.preferences());
        user.setPreferences(merged);
        userRepository.save(user);

        return new UserResponse(user.getId(), user.getUsername(), user.getPreferences());
    }
}
