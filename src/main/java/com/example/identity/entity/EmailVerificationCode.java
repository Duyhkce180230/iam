package com.example.identity.entity;

import java.time.LocalDateTime;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public class EmailVerificationCode {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String id;

    String email;
    String code;
    LocalDateTime expirationTime;

    public boolean isExpired() {
        return LocalDateTime.now().isAfter(expirationTime);
    }
}
