package com.transi.flex;

import java.util.Optional;

import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.RoleRepository;
import com.transi.flex.account.repository.UserRepository;

@Component
@RequiredArgsConstructor
public class ApplicationStartUpRunner implements ApplicationRunner {

    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(ApplicationArguments args) throws Exception {
        Optional<User> existingUser = userRepository.findByUsername("admin");
        if (existingUser.isPresent()) {
            return;
        }

        var role = roleRepository.findByName("ROLE_SUPER_ADMIN").orElse(null);
        if (role == null) {
            return;
        }

        User admin = new User();
        admin.setFirstName("System");
        admin.setLastName("Admin");
        admin.setUsername("admin");
        admin.setEmail("admin@transi-flex.com");
        admin.setPassword(passwordEncoder.encode("admin@1234"));
        admin.getRoles().add(role);
        admin.addProfile(UserProfile.ADMIN_SYSTEM);
        admin.setAgency(null);
        userRepository.save(admin);
        System.out.println("Admin user created successfully!");
    }
}