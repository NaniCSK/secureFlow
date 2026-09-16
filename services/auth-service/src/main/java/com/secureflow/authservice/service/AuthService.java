package com.secureflow.authservice.service;


import com.secureflow.authservice.dto.AuthResponse;
import com.secureflow.authservice.dto.LoginRequest;
import com.secureflow.authservice.dto.LoginResponse;
import com.secureflow.authservice.dto.RegisterRequest;
import com.secureflow.authservice.entity.RefreshToken;
import com.secureflow.authservice.entity.User;
import com.secureflow.authservice.repository.UserRepository;
import com.secureflow.authservice.security.JwtService;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtService jwtService;
    private final RefreshTokenService refreshTokenService;

    public AuthService(
            UserRepository userRepository,
            PasswordEncoder passwordEncoder,
            JwtService jwtService,
            RefreshTokenService refreshTokenService) {

        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtService = jwtService;
        this.refreshTokenService = refreshTokenService;
    }

    public AuthResponse register(RegisterRequest request){

        if(userRepository.existsByEmail(request.getEmail())){
            throw new IllegalArgumentException("Email address already in use");
        }
        User user = new User();
        user.setEmail(request.getEmail());
        user.setPasswordHash(passwordEncoder.encode(request.getPassword()));
        user.setProvider("LOCAL");
        user.setStatus("ACTIVE");
        user.setRole("USER");
        User savedUser = userRepository.save(user);

        return new AuthResponse(
                savedUser.getId(),
                savedUser.getEmail(),
                "User Registered Sucessfully"
        );

    }

    public LoginResponse login(LoginRequest request){

        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new IllegalArgumentException("Invalid email or password"));

        boolean matches = passwordEncoder.matches(request.getPassword(), user.getPasswordHash());

        if(!matches){
            throw new IllegalArgumentException("Invalid email or password");
        }

        String token = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        RefreshToken refreshToken = refreshTokenService.generateRefreshToken(user.getId());

        return new LoginResponse(
                token,
                refreshToken.getToken(),
                "Bearer",
                "Login successful"
        );

    }

    public LoginResponse refresh(String refreshTokenValue){

        RefreshToken newRefreshToken = refreshTokenService.rotateRefreshToken(refreshTokenValue);

        String accessToken = jwtService.generateToken(
                newRefreshToken.getUserId(),
                getUserEmail(newRefreshToken.getUserId()),
                getUserRole(newRefreshToken.getUserId())
        );
        return new LoginResponse(
                accessToken,
                newRefreshToken.getToken(),
                "Bearer",
                "Token successfully refreshed"
        );

    }

    private String getUserRole(UUID userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new RuntimeException("User not found"))
                .getRole();
    }

    private String getUserEmail(UUID userId) {

        return userRepository
                .findById(userId)
                .orElseThrow(() -> new RuntimeException("User Not Found"))
                .getEmail();

    }

    public void logout(String refreshTokenValue){

        refreshTokenService.rotateRefreshToken(refreshTokenValue);

    }

    public User findOrCreateOAuth2User(
            String provider,
            String email,
            String name,
            String providerId
    ) {

        return userRepository.findByEmail(email)
                .map(existingUser -> {

                    if (provider.equalsIgnoreCase(existingUser.getProvider())
                            && existingUser.getProviderId() == null) {

                        existingUser.setProviderId(providerId);

                        return userRepository.save(existingUser);
                    }

                    return existingUser;
                })
                .orElseGet(() -> {

                    User user = new User();

                    user.setEmail(email);
                    user.setProvider(provider.toUpperCase());
                    user.setProviderId(providerId);
                    user.setRole("USER");
                    user.setStatus("ACTIVE");

                    return userRepository.save(user);
                });
    }

    public LoginResponse oauth2Login(
            String provider,
            String email,
            String name,
            String providerId
    ) {

        User user = findOrCreateOAuth2User(
                provider,
                email,
                name,
                providerId
        );

        String accessToken = jwtService.generateToken(
                user.getId(),
                user.getEmail(),
                user.getRole()
        );

        RefreshToken refreshToken =
                refreshTokenService.generateRefreshToken(
                        user.getId()
                );

        return new LoginResponse(
                accessToken,
                refreshToken.getToken(),
                "Bearer",
                provider + " login successful"
        );
    }


}
