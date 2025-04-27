package com.transi.flex.account.service;

import java.time.Instant;
import java.util.Optional;
import java.util.Random;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.thymeleaf.context.Context;
import lombok.RequiredArgsConstructor;
import com.transi.flex.account.enums.PasswordAction;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.mailing.dto.EmailRequest;
import com.transi.flex.mailing.service.EmailService;

@Service
@RequiredArgsConstructor
public class PasswordService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final EmailService emailService;

    @Value("${mail.noreplay.from}")
    private String fromEmail;

    @Value("${mail.noreplay.sender}")
    private String senderEmail;

    private static final long RESET_CODE_EXPIRATION = 1000 * 60 * 15;

    @Transactional
    public void updatePassword(Long userId, String password) {
        Optional<User> user = userRepository.findById(userId);
        if (user.isEmpty()) {
            return;
        }
        this.changePassword(user.get(), password, PasswordAction.UPDATE_PASSWORD);
    }

    @Transactional
    public User resetPassword(String password) {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        Optional<User> user = userRepository.findByUsername(authentication.getName());
        if (user.isPresent()) {
            changePassword(user.get(), password, PasswordAction.RESET_PASSWORD);
        }
        return user.get();
    }

    @Transactional
    public void changePassword(User user, String password, PasswordAction action) {
        if (StringUtils.isBlank(password)) {
            return;
        }
        switch (action) {
            case UPDATE_PASSWORD:
                user.setPasswordReseted(false);
                break;
            case RESET_PASSWORD:
                user.setPasswordReseted(true);
                break;
        }
        user.setPassword(passwordEncoder.encode(password));
    }

    public boolean sendResetCode(String email) {
        Optional<User> user = userRepository.findByEmail(email);
        if (user.isEmpty()) {
            return false;
        }
        String subject = "Récupération de votre compte";
        int code = new Random().nextInt(900000) + 100000;

        Context context = new Context();
        context.setVariable("code", code);
        context.setVariable("fullName", user.get().getFullName());

        var emailRequest = EmailRequest.builder().from(fromEmail).senderName(senderEmail).to(new String[] { email })
                .subject(subject).build();

        emailService.send(context, "reset-password-code", emailRequest);
        user.get().setPasswordResetCode(code);
        user.get().setPasswordResetCodeExperyDate(Instant.now().plusMillis(RESET_CODE_EXPIRATION));
        userRepository.save(user.get());
        return true;
    }

    public Long verifyResetCode(Integer code) {
        Optional<User> user = userRepository.findByPasswordResetCode(code);
        if (user.isEmpty()) {
            return -1L;
        }
        if (codeIsExpired(user.get())) {
            return -2L;
        }
        return user.get().getId();
    }

    public boolean codeIsExpired(User user) {
        if (user.getPasswordResetCodeExperyDate().compareTo(Instant.now()) < 0) {
            return true;
        }
        return false;
    }
}