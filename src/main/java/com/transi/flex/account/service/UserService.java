package com.transi.flex.account.service;

import java.time.LocalDateTime;
import java.util.Collections;
import java.util.List;
import java.util.Locale;
import java.util.Optional;
import java.util.stream.Collectors;

import com.transi.flex.agency.dao.AgencyRepository;
import com.transi.flex.config.AgencyContextHolder;
import com.transi.flex.file.enums.FileType;
import com.transi.flex.file.service.FileUtility;
import jakarta.persistence.EntityNotFoundException;
import org.apache.commons.lang3.RandomStringUtils;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.MessageSource;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.context.Context;

import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.RoleRepository;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.company.model.Company;
import com.transi.flex.company.mapper.CompanyMapper;
import com.transi.flex.config.CompanyContextHolder;
import com.transi.flex.mailing.dto.EmailRequest;
import com.transi.flex.mailing.service.EmailService;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserService {

	private final PasswordEncoder passwordEncoder;
	private final UserRepository repository;
	private final RoleRepository roleRepository;
	private final UserMapper mapper;
	private final CompanyMapper companyMapper;
	private final EmailService emailService;
	private final MessageSource messageSource;
	private final FileUtility fileUtility;
	private final AgencyRepository agencyRepository;

	@Value("${mail.noreplay.from}")
	private String fromEmail;

	@Value("${mail.noreplay.sender}")
	private String senderEmail;

	@Value("${app-url}")
	private String appUrl;

	@Transactional
	public UserDTO add(UserDTO dto) {
		if (dto.getId() != null) {
			update(dto);
			return dto;
		}
		dto.addProfile(dto.getProfile());
		String password = buildPassword(dto);
		if (dto.getUsername() == null) {
			dto.setUsername(dto.getEmail());
		}
		User model = mapper.toModel(dto);
		if (CompanyContextHolder.getCurrentId() != null) {
			model.setCompany(new Company(CompanyContextHolder.getCurrentId()));
		}
		if (AgencyContextHolder.getCurrentAgencyId() != null) {
			model.setAgency(agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId()).orElse(null));
		}
		User savedUser = repository.save(model);
		if (dto.getId() == null) {
			sendUserCreationEmail(savedUser, password);
		}
		return mapper.toDto(savedUser);
	}

	private String generatePassword() {
		return RandomStringUtils.randomAlphanumeric(10);
	}

	@Transactional
	public void addAdminUser(UserDTO dto, CompanyDTO company) {
		String password = buildPassword(dto);
		User model = mapper.toModel(dto);
		model.addProfile(UserProfile.COMPANY);
		model.addRole(roleRepository.findByName("ROLE_COMPANY_ADMIN").get());
		model.setCompany(companyMapper.toModel(company));
		if (AgencyContextHolder.getCurrentAgencyId() !=null){
			model.setAgency(agencyRepository.findById(AgencyContextHolder.getCurrentAgencyId()).orElse(null));
		}
		repository.save(model);
		sendUserCreationEmail(model, password);
	}

	private String buildPassword(UserDTO user) {
		String password = generatePassword();
		user.setPassword(passwordEncoder.encode(password));
		user.setPasswordReseted(false);
		return password;
	}

	public List<UserDTO> getAll() {
		return mapper.toDtos(repository.findAll());
	}


	public List<UserDTO> getUserAdmin() {
		return mapper.toDtos(repository.findByCompanyIdIsNull());
	}

	public List<UserDTO> getByCompany() {
		return mapper.toDtos(repository.findByCompanyId(CompanyContextHolder.getCurrentId()));
	}

	public UserSummary getByFirstName(String firstName) {
		return mapper.toSummary(repository.findByFirstName(firstName));
	}

	public UserSummary getByEmail(String email) {
		return mapper.toSummary(repository.findByEmail(email).orElse(null));
	}

	public UserDTO getByEmailIndicator(String email) {
		return mapper.toDto(repository.findByEmail(email).orElse(null));
	}

	public UserSummary getByLastName(String lastName) {
		return mapper.toSummary(repository.findByLastName(lastName));
	}

	@Transactional
	public void delete(Long id) {
		repository.deleteById(id);
	}

	public List<UserProfile> getCurrentUserProfile() {
		UserDTO user = getCurrentUser();
		if (user == null) {
			return Collections.emptyList();
		}
		return user.getProfiles().stream().map(u -> u.getName()).collect(Collectors.toList());
	}

	public UserDTO getCurrentUser() {
		Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
		Optional<User> user = repository.findByUsername(authentication.getName());
		return mapper.toDto(user.get());
	}

	@Transactional
	public void update(UserDTO dto) {
		User user = repository.findById(dto.getId()).orElseThrow();
		User userUpdated = mapper.toModel(dto);
		if (StringUtils.isNotBlank(userUpdated.getEmail())) {
			user.setEmail(userUpdated.getEmail().trim());
			user.setUsername(userUpdated.getEmail().trim());
		}
		user.setFirstName(userUpdated.getFirstName());
		user.setLastName(userUpdated.getLastName());
		user.setRoles(userUpdated.getRoles());
		user.setPhone(userUpdated.getPhone());
		user.setDefaultLanguage(userUpdated.getDefaultLanguage());
		user.setBirthDate(userUpdated.getBirthDate());
		user.setBirthPlace(userUpdated.getBirthPlace());
		user.setAgency(agencyRepository.findById(dto.getAgencyId()).orElse(null));
	}

	@Transactional
	public void updateLastConnectionTime(Long id) {
		User user = repository.findById(id).orElseThrow();
		user.setLastConnectionAt(LocalDateTime.now());
	}

	public void sendUserCreationEmail(User user, String password) {
		if (user == null) {
			return;
		}
		Locale locale = new Locale(user.getDefaultLang());
		String subject = messageSource.getMessage("EMAIL.ACCOUNT_CREATION.SUBJECT", null, locale);
		Context context = new Context();
		context.setVariable("username", user.getUsername());
		context.setVariable("password", password);
		context.setVariable("fullName", user.getFullName());
		context.setVariable("loginUrl", appUrl + "auth/login");
		var emailRequest = EmailRequest.builder().lang(user.getDefaultLang()).from(fromEmail).senderName(senderEmail)
				.to(new String[] { user.getEmail() }).subject(subject).build();
		emailService.send(context, "account-creation", emailRequest);
	}

	public void uploadProfile(MultipartFile path) throws Exception {
		User user = mapper.toModel(getCurrentUser());
		if (user.getProfilePath() !=null){
			fileUtility.deleteFile(user.getProfilePath());
		}
		String profilePath = fileUtility.save(path, path.getOriginalFilename(),
				FileType.USER_PROFILE);
		user.setProfilePath(profilePath);
		repository.save(user);
	}
}