package com.transi.flex.account.controller;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.HashMap;
import java.util.Map;

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

            // Vérification de la compagnie (prioritaire)
            if (user.isCompanyUser()) {
                if (user.getCompany() == null) {
                    log.error("User {} has no company assigned", user.getUsername());
                    throw new BadCredentialsException("Invalid company and user.");
                }

                if (!user.getCompany().isActive()) {
                    log.warn("User {} tried to login but company {} is inactive",
                            user.getUsername(), user.getCompany().getName());
                    throw new BadCredentialsException("Your company account is inactive. Please contact support.");
                }
            }

            // Vérification de l'agence (si l'utilisateur n'est pas super admin)
            if (!user.isSuperAdmin() && user.getAgency() != null) {
                if (!user.getAgency().isActive()) {
                    log.warn("User {} tried to login but agency {} is inactive",
                            user.getUsername(), user.getAgency().getName());
                    throw new BadCredentialsException("Your agency account is inactive. Please contact your company administrator.");
                }
            }

            return ResponseEntity.ok().body(buildAuthResponse(user));
        } else {
            throw new UsernameNotFoundException("invalid user request !");
        }
    }

    private AuthResponse buildAuthResponse(final User user) {
        Long companyId = user.isCompanyUser() ? user.getCompany().getId() : null;
        Long agencyId = null;

        if (!user.isSuperAdmin() && user.getAgency() != null) {
            agencyId = user.getAgency().getId();
        }

        String accessToken = jwtService.generateToken(user.getUsername(), companyId, agencyId);
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

        // Vérification de la compagnie (prioritaire)
        if (user.isCompanyUser()) {
            if (user.getCompany() == null) {
                log.error("User {} has no company assigned during password reset", user.getUsername());
                throw new BadCredentialsException("Invalid company and user.");
            }

            if (!user.getCompany().isActive()) {
                log.warn("User {} tried to reset password but company {} is inactive",
                        user.getUsername(), user.getCompany().getName());
                throw new BadCredentialsException("Your company account is inactive. Please contact support.");
            }
        }

        // Vérification de l'agence
        if (!user.isSuperAdmin() && user.getAgency() != null) {
            if (!user.getAgency().isActive()) {
                log.warn("User {} tried to reset password but agency {} is inactive",
                        user.getUsername(), user.getAgency().getName());
                throw new BadCredentialsException("Your agency account is inactive. Please contact your company administrator.");
            }
        }

        return buildAuthResponse(user);
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


    @GetMapping("/current-user")
    public UserDTO getCurrentUser(){
        return userService.getCurrentUser();
    }

    @PostMapping("/register")
    public UserDTO register(@RequestBody RegisterRequest registerRequest){
        return userService.register(registerRequest);
    }
}