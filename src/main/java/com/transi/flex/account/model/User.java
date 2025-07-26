// Classe User
package com.transi.flex.account.model;

import java.time.Instant;
import java.time.LocalDate;
import java.time.LocalDateTime;
import java.util.HashSet;
import java.util.Set;
import java.util.stream.Collectors;

import com.transi.flex.company.model.Company;
import org.apache.commons.collections4.CollectionUtils;
import org.apache.commons.lang3.StringUtils;

import com.fasterxml.jackson.annotation.JsonIgnore;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;
import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.repository.ProfileRepository;
import com.transi.flex.account.repository.RoleRepository;
import com.transi.flex.common.utils.SpringContext;
import com.transi.flex.setting.enums.Language;

@Getter
@Setter
@Entity
@Table(name = "T_USER")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    @Column(name = "ID")
    private Long id;

    @Column(name = "FIRST_NAME")
    private String firstName;

    @Column(name = "LAST_NAME")
    private String lastName;

    @Column(name = "EMAIL", unique = true)
    private String email;

    @Column(name = "PHONE", unique = true)
    private String phone;

    @Column(name = "USERNAME", unique = true, nullable = false)
    private String username;

    @JsonIgnore
    @Column(name = "PASSWORD")
    private String password;

    @Column(name = "PASSWORD_RESETED")
    private Boolean passwordReseted;

    @Column(name = "PASSWORD_RESET_CODE")
    private Integer passwordResetCode;

    @Column(name = "PASSWORD_RESET_CODE_EXPIRY_DATE")
    private Instant passwordResetCodeExperyDate;

    @Column(name = "DEFAULT_LANGUAGE")
    @Enumerated(EnumType.ORDINAL)
    private Language defaultLanguage;

    @Column(name = "LAST_CONNECTION_AT")
    private LocalDateTime lastConnectionAt;

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_ROLE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "ROLE_ID") })
    private Set<Role> roles = new HashSet<>();

    @ManyToMany(fetch = FetchType.EAGER)
    @JoinTable(name = "USER_PROFILE", joinColumns = { @JoinColumn(name = "USER_ID") }, inverseJoinColumns = {
            @JoinColumn(name = "PROFILE_ID") })
    private Set<Profile> profiles = new HashSet<>();

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "COMPANY_ID", updatable = false)
    private Company company;

    @Column(name = "PROFILE_PATH")
    private String profilePath;

    @Column(name = "BIRTH_DATE")
    private LocalDate birthDate;

    @Column(name = "BIRTH_PLACE")
    private String birthPlace;

    public void addRole(Role role) {
        if (roles == null) {
            roles = new HashSet<>();
        }
        roles.add(role);
    }

    public boolean hasProfile(UserProfile profile) {
        return profiles.stream().anyMatch(p -> p.getName().equals(profile));
    }

    public void addProfile(UserProfile userProfile) {
        if (CollectionUtils.isEmpty(profiles)) {
            profiles = new HashSet<>();
        }
        if (profiles.stream().anyMatch(p -> p.getName().equals(userProfile))) {
            return;
        }
        Profile model = SpringContext.getBean(ProfileRepository.class).findByName(userProfile);
        if (model == null) {
            return;
        }
        Profile profile = new Profile();
        profile.setName(userProfile);
        profile.setId(model.getId());
        profiles.add(profile);
    }

    public void removeProfile(UserProfile userProfile) {
        if (CollectionUtils.isNotEmpty(profiles)) {
            profiles = profiles.stream().filter(p -> !p.getName().equals(userProfile)).collect(Collectors.toSet());
        }
    }

    public void addRole(String role) {
        if (CollectionUtils.isEmpty(roles)) {
            roles = new HashSet<>();
        }
        if (roles.stream().anyMatch(r -> r.getName().equals(role))) {
            return;
        }

        Role model = SpringContext.getBean(RoleRepository.class).findByName(role).orElse(null);
        if (model == null) {
            return;
        }
        roles.add(model);
    }

    public boolean isAdmin() {
        return CollectionUtils.isNotEmpty(roles) && roles.stream().anyMatch(r -> "ROLE_ADMIN".equals(r.getName()));
    }

    public boolean isSuperAdmin() {
        return CollectionUtils.isNotEmpty(roles)
                && roles.stream().anyMatch(r -> "ROLE_SUPER_ADMIN".equals(r.getName()));
    }

    public boolean isCompanyUser() {
        return CollectionUtils.isNotEmpty(profiles)
                && profiles.stream().anyMatch(p -> UserProfile.COMPANY.equals(p.getName()));
    }

    public String getFullName() {
        return StringUtils.defaultIfBlank(firstName, "") + " " + StringUtils.defaultIfBlank(lastName, "");
    }

    public String getDefaultLang() {
        return defaultLanguage == null ? Language.FR.name().toLowerCase() : defaultLanguage.name().toLowerCase();
    }
}