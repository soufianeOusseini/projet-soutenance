package com.transi.flex.account.controller;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.transi.flex.account.dto.*;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.AuthUser;
import com.transi.flex.account.model.RefreshToken;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.account.service.PasswordService;
import com.transi.flex.account.service.RefreshTokenService;
import com.transi.flex.account.service.UserService;
import com.transi.flex.security.JwtService;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.service.CompanyService;

@RestController
@RequestMapping("/authentication")
@RequiredArgsConstructor
@Slf4j
public class AuthenticationController {

    private final AuthenticationManager authenticationManager;
    private final JwtService jwtService;
    private final UserMapper userMapper;
    private final RefreshTokenService refreshTokenService;
    private final CompanyService companyService;
    private final PasswordService passwordService;
    private final UserRepository userRepository;
    private final UserService userService;

    @PostMapping("/login")
    public ResponseEntity<?> authenticate(@RequestBody AuthRequest authRequest,
                                          final HttpServletRequest request, final HttpServletResponse response) {
        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(authRequest.getUsername(), authRequest.getPassword()));
        if (authentication.isAuthenticated()) {
            var user = ((AuthUser) authentication.getPrincipal()).getUser();
            if (user.isCompanyUser() && user.getCompany() == null) {
                log.error("An error during getting company name");
                throw new BadCredentialsException("Invalid company and user.");
            }
            return ResponseEntity.ok().body(buildAuthResponse(user));
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    private AuthResponse buildAuthResponse(final User user) {
        Long companyId = user.isCompanyUser() ? user.getCompany().getId() : null;
        String accessToken = jwtService.generateToken(user.getUsername(), companyId);
        CompanyDTO company = companyId != null ? companyService.getById(companyId) : null;
        String companyName = company == null ? null : company.getName();
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
        userService.updateLastConnectionTime(user.getId());
        return AuthResponse.builder()
                .companyId(companyId)
                .user(userMapper.toDto(user))
                .accessToken(accessToken)
                .refreshToken(refreshToken.getToken())
                .company(companyName)
                .build();
    }

    @PostMapping(value = "/reset-password")
    public AuthResponse authenticate(@RequestBody ResetPasswordRequest request) throws Exception {
        final User user = passwordService.resetPassword(request.password());
        if (user.isCompanyUser() && user.getCompany() == null) {
            log.error("An error during getting company name");
            throw new BadCredentialsException("Invalid company and user.");
        }
        return buildAuthResponse(user);
    }

    @PostMapping("/refreshToken")
    public AuthResponse refreshToken(@RequestBody RefreshTokenRequest request) {
        CompanyDTO company = request.getCompanyId() != null
                ? companyService.getById(request.getCompanyId())
                : null;
        String companyName = company == null ? null : company.getName();
        return refreshTokenService.findByToken(request.getToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> {
                    Long companyId = user.isCompanyUser() ? user.getCompany().getId() : null;
                    String accessToken = jwtService.generateToken(user.getUsername(), request.getCompanyId());
                    RefreshToken refreshToken = refreshTokenService.createRefreshToken(user.getUsername());
                    userService.updateLastConnectionTime(user.getId());
                    return AuthResponse.builder()
                            .companyId(companyId)
                            .accessToken(accessToken)
                            .refreshToken(refreshToken.getToken())
                            .company(companyName)
                            .build();
                })
                .orElseThrow(() -> new RuntimeException("Refresh Token is not in DB..!!"));
    }

    @PostMapping(value = "/send-reset-password-code")
    public ResponseEntity<?> sendResetPasswordCode(@RequestBody ResendCodeRequest resendCodeRequest) {
        boolean sent = passwordService.sendResetCode(resendCodeRequest.email());
        if (sent) {
            return ResponseEntity.ok().build();
        }
        return ResponseEntity.status(205).build();
    }

    @GetMapping(value = "/verify-reset-password-code/{code}")
    public ResponseEntity<?> verifyResetPasswordCode(@PathVariable("code") Integer code) {
        Long result = passwordService.verifyResetCode(code);
        if (result == -1) {
            return ResponseEntity.status(205).build();
        }
        if (result == -2) {
            return ResponseEntity.status(206).build();
        }
        User user = userRepository.findById(result).orElse(null);
        String accessToken = jwtService.generateTmpToken(user.getUsername());
        AuthResponse authResponse = AuthResponse.builder().accessToken(accessToken).build();
        return ResponseEntity.ok().body(authResponse);
    }
}