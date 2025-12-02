package com.example.identity.entity;

import java.time.Instant;
import java.time.LocalDate;
import java.util.List;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Entity
@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
@Table(name = "users")
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    String userId;

    String username;

    @Column(nullable = false)
    String password;

    String email;

    String phone;

    String fullName;

    String identifyNumber;

    Boolean gender;

    Integer age;

    String address;

    @ManyToOne
    @JoinColumn(name = "role_id")
    Role role;

    LocalDate dob;

    @Column(name = "failed_attempts")
    Integer failedAttempts = 0;

    @Column(name = "lock_time")
    Instant lockTime;

    @Column(name = "password_last_changed_at")
    Instant passwordLastChangedAt;

    Boolean isVerified;

    @OneToMany(mappedBy = "user")
    List<UserSession> sessions;

    Boolean isFirstLogin;
}
