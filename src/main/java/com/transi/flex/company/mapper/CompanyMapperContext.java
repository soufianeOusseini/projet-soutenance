package com.transi.flex.company.mapper;

import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.repository.CompanyRepository;
import jakarta.annotation.PostConstruct;
import lombok.Getter;
import org.mapstruct.AfterMapping;
import org.mapstruct.MappingTarget;
import org.springframework.context.annotation.Lazy;
import org.springframework.stereotype.Component;

import java.util.Optional;

@Component
public class CompanyMapperContext {

    @Getter
    private static CompanyMapperContext instance;

    private final CompanyRepository companyRepository;
    private final UserRepository userRepository;

    private final UserMapper userMapper;

    public CompanyMapperContext(
            CompanyRepository companyRepository,
            UserRepository userRepository,
            @Lazy UserMapper userMapper) {
        this.companyRepository = companyRepository;
        this.userRepository = userRepository;
        this.userMapper = userMapper;
    }

    @PostConstruct
    public void init() {
        instance = this;
    }

    @AfterMapping
    public void afterMapping(@MappingTarget CompanyDTO dto, Company model) {
        Optional<User> userOptional = userRepository.findByEmail(model.getAdminEmail());
        if (userOptional.isPresent()) {
            UserSummary userSummary = userMapper.toSummary(userOptional.get());
            dto.setAdmin(userSummary);
        }
    }
}