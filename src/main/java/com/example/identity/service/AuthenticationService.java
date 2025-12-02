package com.example.identity.service;

import java.text.ParseException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.temporal.ChronoUnit;
import java.util.*;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;

import com.example.identity.dto.reponse.AuthenticationResponse;
import com.example.identity.dto.reponse.IntrospectResponse;
import com.example.identity.dto.request.*;
import com.example.identity.entity.InvalidatedToken;
import com.example.identity.entity.Role;
import com.example.identity.entity.User;
import com.example.identity.entity.UserSession;
import com.example.identity.exception.AppException;
import com.example.identity.exception.ErrorCode;
import com.example.identity.repository.InvalidatedTokenRepository;
import com.example.identity.repository.UserRepository;
import com.example.identity.repository.UserSessionRepository;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.MACSigner;
import com.nimbusds.jose.crypto.MACVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import com.nimbusds.jwt.SignedJWT;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.experimental.NonFinal;
import lombok.extern.slf4j.Slf4j;

@Service
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@RequiredArgsConstructor
@Slf4j
public class AuthenticationService {

    UserRepository userRepository;
    InvalidatedTokenRepository invalidatedTokenRepository;
    PasswordEncoder passwordEncoder;
    UserSessionRepository userSessionRepository;

    EmailService emailService;
    TokenService tokenService;

    private static final int MAX_FAILED_ATTEMPTS = 5;
    private static final long LOCK_TIME_DURATION_MINUTES = 3;
    private static final long PASSWORD_EXPIRATION_DAYS = 90;

    @NonFinal
    @Value("${jwt.signerKey}")
    protected String signerKey;

    @NonFinal
    @Value("${jwt.valid-duration}")
    protected long validDuration;

    @NonFinal
    @Value("${jwt.refreshable-duration}")
    protected long refreshableDuration;

    @NonFinal
    @Value("${jwt.sliding-expiration-threshold}")
    protected long slidingExpirationThreshold;

    public void sendVerificationCode(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new AppException(ErrorCode.EMAIL_EXISTED);
        }
        emailService.sendVerificationEmail(email);
    }

    public void forgotPassword(EmailSendRequest request) {
        User user = userRepository
                .findByEmail(request.getEmail())
                .orElseThrow(() -> new AppException(ErrorCode.EMAIL_NOT_FOUND));

        String token = tokenService.generateResetToken(user.getEmail());
        emailService.sendResetPasswordEmail(user.getEmail(), token);
    }

    public void resetPassword(ResetPasswordRequest request) {

        if (!request.getNewPassword().equals(request.getConfirmNewPassword())) {
            throw new AppException(ErrorCode.PASSWORD_NOT_MATCH);
        }

        String email = tokenService.validateResetToken(request.getVerificationCode());

        User user = userRepository.findByEmail(email).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        user.setPassword(passwordEncoder.encode(request.getNewPassword()));
        userRepository.save(user);

        tokenService.invalidateToken(request.getVerificationCode());
    }

    public IntrospectResponse introspect(IntrospectRequest introspectRequest)
            throws AppException, JOSEException, ParseException {
        var token = introspectRequest.getToken();
        boolean isValid = true;
        String newToken = null;
        try {
            verifyToken(token, false);
            var maybeNew = maybeRefreshToken(token);
            if (maybeNew.isPresent()) {
                newToken = maybeNew.get();
            }
        } catch (AppException e) {
            isValid = false;
        }
        return IntrospectResponse.builder().valid(isValid).newToken(newToken).build();
    }

    public AuthenticationResponse authenticate(AuthenticationRequest authenticationRequest) {
        String input = authenticationRequest.getUsername();

        // ✅ Tìm theo email hoặc username
        User user = userRepository
                .findByEmailIgnoreCase(input)
                .or(() -> userRepository.findByUsernameIgnoreCase(input))
                .orElseThrow(() -> new AppException(ErrorCode.INCORRECT_USER_OR_PASS));

        // Nếu tài khoản đang bị khóa
        if (user.getFailedAttempts() >= 5) {
            // Nếu hết thời gian khóa thì mở lại
            if (isLockTimeExpired(user)) {
                unlockAccount(user);
            } else {
                throw new AppException(ErrorCode.ACCOUNT_LOCKED);
            }
        }
        boolean authenticated = passwordEncoder.matches(authenticationRequest.getPassword(), user.getPassword());

        if (!authenticated) {
            increaseFailedAttempts(user);
            throw new AppException(ErrorCode.INCORRECT_USER_OR_PASS);
        }

        Instant lastChanged = user.getPasswordLastChangedAt();
        if (lastChanged != null) {
            long daysSinceChange = ChronoUnit.DAYS.between(lastChanged, Instant.now());
            if (daysSinceChange > PASSWORD_EXPIRATION_DAYS) {
                throw new AppException(ErrorCode.PASSWORD_EXPIRED);
            }
        }

        resetFailedAttempts(user);

        var token = generateToken(user);

        // ✅ Lưu lại phiên đăng nhập (session)
        UserSession session = UserSession.builder()
                .user(user)
                .loginAt(Instant.now().atZone(java.time.ZoneId.systemDefault()).toLocalDateTime())
                .isActive(true)
                .build();
        userSessionRepository.save(session);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .userId(user.getUserId())
                .isFirstLogin(user.getIsFirstLogin())
                .build();
    }

    private void increaseFailedAttempts(User user) {
        if (user.getFailedAttempts() == null) {
            user.setFailedAttempts(0);
        }
        int newFailAttempts = user.getFailedAttempts() + 1;
        user.setFailedAttempts(newFailAttempts);

        if (newFailAttempts >= MAX_FAILED_ATTEMPTS) {
            lockAccount(user);
        }

        userRepository.save(user);
    }

    private void resetFailedAttempts(User user) {
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    private void lockAccount(User user) {
        user.setLockTime(Instant.now());
        userRepository.save(user);
    }

    private void unlockAccount(User user) {
        user.setLockTime(null);
        user.setFailedAttempts(0);
        userRepository.save(user);
    }

    private boolean isLockTimeExpired(User user) {
        if (user.getLockTime() == null) return false;
        Instant lockExpiration = user.getLockTime().plus(LOCK_TIME_DURATION_MINUTES, ChronoUnit.MINUTES);
        return Instant.now().isAfter(lockExpiration);
    }

    public void logout(LogoutRequest request) throws ParseException, JOSEException {
        try {

            var signToken = verifyToken(request.getToken(), true);
            log.info("All claims in token: {}", signToken.getJWTClaimsSet().getClaims());

            String userId = signToken.getJWTClaimsSet().getStringClaim("user_id");
            log.info("id trong token: {}", userId);

            userSessionRepository.findFirstByUser_UserIdAndIsActiveTrue(userId).ifPresent(session -> {
                session.setIsActive(false);
                session.setLogoutAt(LocalDateTime.now());
                userSessionRepository.save(session);
            });

            String jit = signToken.getJWTClaimsSet().getJWTID();
            Date expiryDate = signToken.getJWTClaimsSet().getExpirationTime();

            InvalidatedToken invalidatedToken =
                    InvalidatedToken.builder().id(jit).expiryTime(expiryDate).build();

            invalidatedTokenRepository.save(invalidatedToken);

        } catch (AppException e) {
            log.info("Token already expired");
        }
    }

    public AuthenticationResponse refreshToken(RefreshRequest request) throws ParseException, JOSEException {
        var signedJWT = verifyToken(request.getToken(), true);

        var jit = signedJWT.getJWTClaimsSet().getJWTID();
        Date expiryTime = signedJWT.getJWTClaimsSet().getExpirationTime();

        InvalidatedToken invalidatedToken =
                InvalidatedToken.builder().id(jit).expiryTime(expiryTime).build();
        invalidatedTokenRepository.save(invalidatedToken);

        var username = signedJWT.getJWTClaimsSet().getSubject();
        var user =
                userRepository.findByUsername(username).orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));

        var token = generateToken(user);

        return AuthenticationResponse.builder()
                .token(token)
                .authenticated(true)
                .userId(user.getUserId())
                .build();
    }

    private SignedJWT verifyToken(String token, boolean isRefresh) throws JOSEException, ParseException {
        JWSVerifier verifier = new MACVerifier(signerKey);
        SignedJWT jwt = SignedJWT.parse(token);

        boolean verified = jwt.verify(verifier);
        if (!verified) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        // Nếu token đã bị logout (có trong danh sách invalidated)
        if (invalidatedTokenRepository.existsById(jwt.getJWTClaimsSet().getJWTID())) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        Date now = new Date();
        Date expirationDate;

        if (isRefresh) {
            // Token refresh: cho phép làm mới trong khoảng refreshableDuration (theo ngày)
            expirationDate = new Date(jwt.getJWTClaimsSet()
                    .getIssueTime()
                    .toInstant()
                    .plus(refreshableDuration, ChronoUnit.MINUTES)
                    .toEpochMilli());
        } else {
            // Token truy cập thông thường
            expirationDate = jwt.getJWTClaimsSet().getExpirationTime();
        }

        // Nếu đã quá hạn -> báo lỗi
        if (expirationDate.before(now)) {
            throw new AppException(ErrorCode.UNAUTHENTICATED);
        }

        return jwt;
    }

    public Optional<String> maybeRefreshToken(String token) throws ParseException {
        SignedJWT jwt = SignedJWT.parse(token);
        Date expiration = jwt.getJWTClaimsSet().getExpirationTime();

        long minutesLeft = ChronoUnit.MINUTES.between(Instant.now(), expiration.toInstant());
        if (minutesLeft <= slidingExpirationThreshold) {

            String username = jwt.getJWTClaimsSet().getSubject();
            var user = userRepository
                    .findByUsername(username)
                    .orElseThrow(() -> new AppException(ErrorCode.USER_NOT_FOUND));
            String newToken = generateToken(user);
            return Optional.of(newToken);
        }
        return Optional.empty();
    }

    private String generateToken(User user) {
        JWSHeader header = new JWSHeader(JWSAlgorithm.HS512);
        JWTClaimsSet jwtClaimsSet = new JWTClaimsSet.Builder()
                .subject(user.getUserId())
                .claim("user_id", user.getUserId())
                .issuer("duy.com")
                .issueTime(new Date())
                .expirationTime(new Date(
                        Instant.now().plus(validDuration, ChronoUnit.MINUTES).toEpochMilli()))
                .jwtID(UUID.randomUUID().toString())
                .claim("scope", buildScope(user))
                .build();
        Payload payload = new Payload(jwtClaimsSet.toJSONObject());
        JWSObject jwsObject = new JWSObject(header, payload);
        try {
            jwsObject.sign(new MACSigner(signerKey.getBytes()));
            return jwsObject.serialize();
        } catch (JOSEException e) {

            throw new IllegalArgumentException(e);
        }
    }

    private String buildScope(User user) {
        StringJoiner stringJoiner = new StringJoiner(" ");

        Role role = user.getRole();
        if (role != null) {
            stringJoiner.add("ROLE_" + role.getRoleName().toUpperCase());

            if (!CollectionUtils.isEmpty(role.getPermissions())) {
                role.getPermissions().forEach(permission -> stringJoiner.add(permission.name()));
            }
        }

        return stringJoiner.toString();
    }
}
