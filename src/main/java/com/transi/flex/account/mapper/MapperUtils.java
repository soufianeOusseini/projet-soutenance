package com.transi.flex.account.mapper;

import org.mapstruct.Named;
import org.springframework.stereotype.Component;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.model.User;

@Component
public class MapperUtils {

    @Named("userToSummary")
    public UserSummary userToSummary(User user) {
        if (user == null) {
            return null;
        }

        UserSummary summary = new UserSummary();
        summary.setId(user.getId());
        summary.setFirstName(user.getFirstName());
        summary.setLastName(user.getLastName());
        summary.setEmail(user.getEmail());
        summary.setPhone(user.getPhone());
        summary.setDefaultLanguage(user.getDefaultLanguage());
        return summary;
    }
}