package com.example.identity.service;

import java.time.Instant;
import java.util.Date;
import java.util.Random;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import com.example.identity.entity.PasswordResetToken;
import com.example.identity.repository.InvalidatedTokenRepository;
import com.example.identity.repository.PasswordResetTokenRepository;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
public class TokenService {

    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordResetTokenRepository tokenRepository;

    @Value("${jwt.signerKey}")
    @NonFinal
    String signerKey;

    public String generateResetToken(String email) {
        String token = String.valueOf(100000 + new Random().nextInt(900000)); // 6 số từ 100000-999999

        PasswordResetToken resetToken = PasswordResetToken.builder()
                .email(email)
                .token(token)
                .expiredAt(Instant.now().plusSeconds(300)) // 5 phút
                .used(false)
                .build();

        tokenRepository.save(resetToken);
        return token;
    }

    public String validateResetToken(String token) {
        PasswordResetToken resetToken =
                tokenRepository.findByToken(token).orElseThrow(() -> new IllegalArgumentException("Invalid token"));

        if (resetToken.isUsed()) {
            throw new IllegalArgumentException("Token already used");
        }

        if (Instant.now().isAfter(resetToken.getExpiredAt())) {
            throw new IllegalArgumentException("Token expired");
        }

        return resetToken.getEmail();
    }

    public void invalidateToken(String token) {
        tokenRepository.findByToken(token).ifPresent(t -> {
            t.setUsed(true);
            tokenRepository.save(t);
        });
    }

    public boolean validateToken(String token) {
        try {
            SignedJWT jwt = SignedJWT.parse(token);
            var verifier = new MACVerifier(signerKey);

            boolean verified = jwt.verify(verifier);
            Date expirationTime = jwt.getJWTClaimsSet().getExpirationTime();

            return verified
                    && expirationTime.after(new Date())
                    && !invalidatedTokenRepository.existsById(
                            jwt.getJWTClaimsSet().getJWTID());
        } catch (Exception e) {
            return false;
        }
    }
}
