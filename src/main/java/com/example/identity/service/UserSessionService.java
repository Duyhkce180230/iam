package com.example.identity.service;

import java.time.LocalDateTime;

import org.springframework.stereotype.Service;

import com.example.identity.entity.User;
import com.example.identity.entity.UserSession;
import com.example.identity.repository.UserSessionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserSessionService {

    UserSessionRepository userSessionRepository;

    public UserSession createSession(User user, String ip, String agent) {
        UserSession session = UserSession.builder()
                .user(user)
                .loginAt(LocalDateTime.now())
                .isActive(true)
                .build();
        return userSessionRepository.save(session);
    }

    public void endSession(String sessionId) {
        userSessionRepository.findById(sessionId).ifPresent(session -> {
            session.setLogoutAt(LocalDateTime.now());
            session.setIsActive(false);
            userSessionRepository.save(session);
        });
    }
}
