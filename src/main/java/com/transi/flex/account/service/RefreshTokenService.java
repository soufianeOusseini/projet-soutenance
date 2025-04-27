package com.transi.flex.account.service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.transi.flex.account.model.RefreshToken;
import com.transi.flex.account.repository.RefreshTokenRepository;
import com.transi.flex.account.repository.UserRepository;

@Service
public class RefreshTokenService {

    @Autowired
    RefreshTokenRepository refreshTokenRepository;

    @Autowired
    UserRepository userRepository;

    private static final long EXPIRATION = 1000 * 60 * 60;

    public RefreshToken createRefreshToken(String username) {
        var user = userRepository.findByUsername(username).get();
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(user.getId()).orElse(null);
        if (refreshToken == null) {
            refreshToken = RefreshToken.builder().user(user).token(UUID.randomUUID().toString()).build();
        }
        refreshToken.setExpiryDate(Instant.now().plusMillis(EXPIRATION));
        return refreshTokenRepository.save(refreshToken);
    }

    public Optional<RefreshToken> findByToken(String token) {
        return refreshTokenRepository.findByToken(token);
    }

    public RefreshToken verifyExpiration(RefreshToken token) {
        if (token.getExpiryDate().compareTo(Instant.now()) < 0) {
            refreshTokenRepository.delete(token);
            throw new RuntimeException(token.getToken() + " Refresh token is expired. Please make a new login..!");
        }
        return token;
    }

    @Transactional
    public void deleteByUserId(Long userId) {
        refreshTokenRepository.deleteByUserId(userId);
    }
}