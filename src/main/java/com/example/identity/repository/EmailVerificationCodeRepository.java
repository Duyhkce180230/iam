package com.example.identity.repository;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identity.entity.EmailVerificationCode;

public interface EmailVerificationCodeRepository extends JpaRepository<EmailVerificationCode, String> {
    Optional<EmailVerificationCode> findByEmail(String email);
}
