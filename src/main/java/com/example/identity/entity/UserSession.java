package com.example.identity.entity;

import java.time.LocalDateTime;

import jakarta.persistence.*;

import lombok.*;
import lombok.experimental.FieldDefaults;

@Getter
@Setter
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@FieldDefaults(level = AccessLevel.PRIVATE)
public class UserSession {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    Long sessionId;

    LocalDateTime loginAt;

    LocalDateTime logoutAt;

    Boolean isActive;

    @ManyToOne
    User user;
}
