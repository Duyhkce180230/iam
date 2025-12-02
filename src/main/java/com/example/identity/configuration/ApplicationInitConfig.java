package com.example.identity.configuration;

import java.time.Instant;
import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;

import org.springframework.boot.ApplicationRunner;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.crypto.password.PasswordEncoder;

import com.example.identity.entity.Role;
import com.example.identity.entity.User;
import com.example.identity.enums.Permissions;
import com.example.identity.repository.RoleRepository;
import com.example.identity.repository.UserRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Configuration
@RequiredArgsConstructor
@Slf4j
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
public class ApplicationInitConfig {

    private PasswordEncoder passwordEncoder;
    String admin = "admin";
    RoleRepository roleRepository;

    @Bean
    ApplicationRunner applicationRunner(UserRepository userRepository) {
        return args -> {
            if (userRepository.findByUsername(admin).isEmpty()) {

                Set<Permissions> allPermissions = new HashSet<>(Arrays.asList(Permissions.values()));

                Role role = Role.builder()
                        .roleName("Admin")
                        .roleDescription("Admin role")
                        .roleCode("Admin")
                        .permissions(allPermissions)
                        .build();
                roleRepository.save(role);

                User user = User.builder()
                        .username(admin)
                        .password(passwordEncoder.encode(admin))
                        .email("admin@gmail.com")
                        .passwordLastChangedAt(Instant.now())
                        .role(role)
                        .failedAttempts(0)
                        .isFirstLogin(true)
                        .build();
                userRepository.save(user);
                log.warn("⚠\uFE0FAdmin has been created with password: admin, please change it!");
            }
        };
    }
}
