package com.secureflow.authservice.service;

import com.secureflow.authservice.entity.RefreshToken;
import com.secureflow.authservice.repository.RefreshTokenRepository;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.UUID;

@Service
public class RefreshTokenService {

    private RefreshTokenRepository refreshTokenRepository;
    private long refreshTokenExpiration;

    public RefreshTokenService(
            RefreshTokenRepository refreshTokenRepository,
            @Value("${refresh-token.expiration}") long refreshTokenExpiration) {

        this.refreshTokenRepository = refreshTokenRepository;
        this.refreshTokenExpiration = refreshTokenExpiration;

    }

    public RefreshToken generateRefreshToken(UUID id) {

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setUserId(id);
        refreshToken.setExpiresAt(
                Instant.now().plusSeconds(refreshTokenExpiration)
        );
        refreshToken.setRevoked(false);
        return refreshTokenRepository.save(refreshToken);

    }

    public RefreshToken verifyExpiration(RefreshToken refreshToken) {

        if(refreshToken.getExpiresAt().isBefore(Instant.now())) {
            refreshToken.setRevoked(true);
            refreshTokenRepository.save(refreshToken);

            throw new RuntimeException("Refresh Token Expired");
        }

        if(refreshToken.isRevoked()){
            throw new RuntimeException("Refresh token revoked");
        }
        return refreshToken;

    }

    public RefreshToken rotateRefreshToken(String tokenValue) {

        RefreshToken oldToken = refreshTokenRepository
                .findByToken(tokenValue)
                .orElseThrow(() -> new RuntimeException("Refresh Token Not Found"));
        verifyExpiration(oldToken);
        oldToken.setRevoked(true);
        refreshTokenRepository.save(oldToken);
        return generateRefreshToken(oldToken.getUserId());

    }

    public void revokeRefreshToken(String refreshToken) {

        RefreshToken token = refreshTokenRepository.findByToken(refreshToken).orElseThrow(()-> new RuntimeException("Refresh Token Not Found"));
        token.setRevoked(true);
        refreshTokenRepository.save(token);

    }

}
