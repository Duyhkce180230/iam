package com.example.identity.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.example.identity.entity.UserSession;

public interface UserSessionRepository extends JpaRepository<UserSession, String> {
    Optional<UserSession> findFirstByUser_UserIdAndIsActiveTrue(String userId);

    List<UserSession> findByIsActiveTrue();

    void deleteByUserUserId(String userId);
}
