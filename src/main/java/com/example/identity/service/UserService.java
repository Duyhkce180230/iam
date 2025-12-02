package com.example.identity.service;

import java.security.SecureRandom;
import java.text.Normalizer;
import java.time.Instant;
import java.time.LocalDate;
import java.time.Period;

import jakarta.transaction.Transactional;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import com.example.identity.dto.reponse.UserResponse;
import com.example.identity.dto.request.*;
import com.example.identity.entity.Role;
import com.example.identity.entity.User;
import com.example.identity.exception.AppException;
import com.example.identity.exception.ErrorCode;
import com.example.identity.mapper.UserMapper;
import com.example.identity.repository.RoleRepository;
import com.example.identity.repository.UserRepository;
import com.example.identity.repository.UserSessionRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class UserService {

    UserRepository userRepository;
    UserMapper userMapper;
    PasswordEncoder passwordEncoder;
    EmailService emailService;
    RoleRepository roleRepository;
    UserSessionRepository userSessionRepository;

    @PreAuthorize("hasAuthority('VIEW_USER')")
    //    @PreAuthorize("hasRole('ADMIN')")
    public Page<UserResponse> getUsers(int page, int size, String search) {
        Pageable pageable = PageRequest.of(page, size, Sort.by("username").ascending());

        if (search == null || search.isEmpty()) {
            return userRepository.findAll(pageable).map(userMapper::toUserResponse);
        } else {
            return userRepository
                    .findByUsernameContainingIgnoreCaseOrEmailContainingIgnoreCase(search, search, pageable)
                    .map(userMapper::toUserResponse);
        }
    }

    //    @PostAuthorize("returnObject.username == authentication.name")
    public UserResponse getUserByID(String id) {
        return userMapper.toUserResponse(
                userRepository.findById(id).orElseThrow(() -> new RuntimeException("User not found")));
    }

    public UserResponse getMyInfo() {
        var context = SecurityContextHolder.getContext();
        log.info("Context: {}", context);
        String id = context.getAuthentication().getName();
        log.info("Id: {}", id);
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var response = userMapper.toUserResponse(user);

        return response;
    }

    @PreAuthorize("hasAuthority('MODIFY_USER')")
    public UserResponse updateUserForAdminByID(String id, UserUpdationRequest request) {
        User user = userRepository.findById(id).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (request.getEmail() != null
                && !request.getEmail().equalsIgnoreCase(user.getEmail())
                && userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        userMapper.updateUserAdmin(user, request);

        if (user.getDob() != null) {
            int age = Period.between(user.getDob(), LocalDate.now()).getYears();
            user.setAge(age);
        }

        // Chỉ lấy 1 role duy nhất
        if (request.getRole() != null) {
            Role role = roleRepository
                    .findById(request.getRole())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
        }

        return userMapper.toUserResponse(userRepository.save(user));
    }

    @PreAuthorize("hasAuthority('DELETE_USER')")
    @Transactional
    public void deleteUserByID(String id) {
        userSessionRepository.deleteByUserUserId(id);
        userRepository.deleteById(id);
    }

    @PreAuthorize("hasAuthority('CREATE_USER')")
    public UserResponse createNewUser(UserCreationRequest request) {

        if (userRepository.existsByEmail(request.getEmail())) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }

        User user = userMapper.toUser(request);

        String rawPassword = generateRandomPassword(10);
        user.setPassword(passwordEncoder.encode(rawPassword));
        user.setPasswordLastChangedAt(Instant.now());

        String username = generateUsername(user.getFullName());
        user.setUsername(username);

        if (request.getRole() != null) {
            Role role = roleRepository
                    .findById(request.getRole())
                    .orElseThrow(() -> new AppException(ErrorCode.ROLE_NOT_FOUND));
            user.setRole(role);
        }

        user.setIsFirstLogin(true);

        if (user.getDob() != null) {
            int age = Period.between(user.getDob(), LocalDate.now()).getYears();
            user.setAge(age);
        }

        if (user.getFailedAttempts() == null) {
            user.setFailedAttempts(0);
        }

        User savedUser = userRepository.save(user);

        emailService.sendAccountInfoEmail(savedUser.getEmail(), savedUser.getUsername(), rawPassword);

        return userMapper.toUserResponse(savedUser);
    }

    private String generateRandomPassword(int length) {
        String chars = "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789";
        SecureRandom random = new SecureRandom();
        StringBuilder sb = new StringBuilder();
        for (int i = 0; i < length; i++) {
            sb.append(chars.charAt(random.nextInt(chars.length())));
        }
        return sb.toString();
    }

    private String generateUsername(String fullName) {
        if (fullName == null || fullName.isBlank()) {
            throw new AppException(ErrorCode.FULLNAME_INVALID);
        }

        String[] parts = fullName.trim().split("\\s+");
        String lastName = parts[parts.length - 1];
        String initials = "";

        for (int i = 0; i < parts.length - 1; i++) {
            initials += parts[i].substring(0, 1).toUpperCase();
        }

        String baseUsername = removeVietnameseAccents(lastName + initials);

        String username = baseUsername + "1";
        int counter = 1;

        while (userRepository.existsByUsername(username)) {
            counter++;
            username = baseUsername + counter;
        }

        return username;
    }

    private String removeVietnameseAccents(String input) {
        String normalized = Normalizer.normalize(input, Normalizer.Form.NFD);
        return normalized.replaceAll("\\p{InCombiningDiacriticalMarks}+", "");
    }

    public void changePassword(String userId, ChangePasswordRequest request) {
        User user = userRepository.findById(userId).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        if (!passwordEncoder.matches(request.getOldPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.INVALID_OLD_PASSWORD);
        }

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        if (passwordEncoder.matches(request.getNewPassword(), user.getPassword())) {
            throw new AppException(ErrorCode.DUPLICATE_PASSWORD);
        }

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        user.setIsFirstLogin(false);
        userRepository.save(user);
    }
}
