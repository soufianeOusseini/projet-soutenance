package com.transi.flex;

import java.util.TimeZone;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.domain.AuditorAware;
import org.springframework.data.jpa.repository.config.EnableJpaAuditing;
import org.springframework.scheduling.annotation.EnableScheduling;

import com.transi.flex.security.SpringSecurityAuditorAware;

import jakarta.annotation.PostConstruct;

@EnableJpaAuditing(auditorAwareRef = "auditorAware")
@EnableScheduling
@SpringBootApplication
public class TransiFlexApplication {

    public static void main(String[] args) {
        SpringApplication.run(TransiFlexApplication.class, args);
    }

    @PostConstruct
    public void init() {
        TimeZone.setDefault(TimeZone.getTimeZone("UTC"));
    }

    @Bean
    public AuditorAware<com.transi.flex.account.model.User> auditorAware() {
        return new SpringSecurityAuditorAware();
    }
}