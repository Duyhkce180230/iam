package com.example.identity.service;

import java.time.LocalDateTime;
import java.util.Optional;
import java.util.Random;

import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import com.example.identity.entity.EmailVerificationCode;
import com.example.identity.repository.EmailVerificationCodeRepository;

import lombok.AccessLevel;
import lombok.RequiredArgsConstructor;
import lombok.experimental.FieldDefaults;
import lombok.extern.slf4j.Slf4j;

@Service
@RequiredArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE, makeFinal = true)
@Slf4j
public class EmailService {

    private Random random = new Random();
    JavaMailSender mailSender;
    EmailVerificationCodeRepository repository;

    public void sendResetPasswordEmail(String toEmail, String token) {
        String subject = "Reset your password";
        String text = "Dear user,\n\n"
                + "We received a request to reset your password. "
                + "Click the link below to reset it:\n\n"
                + token + "\n\n"
                + "If you didn’t request this, you can safely ignore this email.\n\n"
                + "Best regards,\nYour Identity Service";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendAccountInfoEmail(String toEmail, String username, String password) {
        String subject = "Tài khoản của bạn đã được tạo thành công";
        String text = "Xin chào,\n\n"
                + "Tài khoản của bạn đã được tạo thành công trên hệ thống.\n\n"
                + "Thông tin đăng nhập:\n"
                + "👉 Username: " + username + "\n"
                + "👉 Password: " + password + "\n\n"
                + "Vui lòng đăng nhập và đổi mật khẩu sau lần đăng nhập đầu tiên để bảo mật tài khoản.\n\n"
                + "Trân trọng,\nĐội ngũ hỗ trợ.";

        SimpleMailMessage message = new SimpleMailMessage();
        message.setTo(toEmail);
        message.setSubject(subject);
        message.setText(text);

        mailSender.send(message);
    }

    public void sendVerificationEmail(String email) {
        String code = String.format("%06d", random.nextInt(999999));

        EmailVerificationCode evc = repository.findByEmail(email).orElse(new EmailVerificationCode());
        evc.setEmail(email);
        evc.setCode(code);
        evc.setExpirationTime(LocalDateTime.now().plusMinutes(5));

        repository.save(evc);

        SimpleMailMessage msg = new SimpleMailMessage();
        msg.setTo(email);
        msg.setSubject("Your Verification Code");
        msg.setText("Your 6-digit verification code is: " + code);
        mailSender.send(msg);
    }

    public boolean verifyCode(String email, String code) {
        Optional<EmailVerificationCode> optional = repository.findByEmail(email);
        if (optional.isEmpty()) return false;

        EmailVerificationCode evc = optional.get();
        return evc.getCode().equals(code) && !evc.isExpired();
    }
}
