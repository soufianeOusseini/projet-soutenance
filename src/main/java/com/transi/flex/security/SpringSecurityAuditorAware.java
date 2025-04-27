package com.transi.flex.security;

import java.util.Optional;

import org.apache.commons.lang3.StringUtils;
import org.springframework.data.domain.AuditorAware;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;

import com.transi.flex.account.model.AuthUser;
import com.transi.flex.account.model.User;

@Component
public class SpringSecurityAuditorAware implements AuditorAware<User> {

    @Override
    public Optional<User> getCurrentAuditor() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()
                || StringUtils.isBlank(authentication.getName())) {
            return Optional.empty();
        }
        return Optional.ofNullable(((AuthUser) authentication.getPrincipal()).getUser());
    }
}