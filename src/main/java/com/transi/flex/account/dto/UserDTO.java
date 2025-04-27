package com.transi.flex.account.dto;

import java.time.LocalDateTime;
import java.util.Set;
import com.fasterxml.jackson.annotation.JsonProperty;
import com.fasterxml.jackson.annotation.JsonProperty.Access;
import lombok.Getter;
import lombok.Setter;
import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.model.Profile;
import com.transi.flex.account.repository.ProfileRepository;
import com.transi.flex.common.utils.SpringContext;
import com.transi.flex.company.dto.CompanyDTO;
import com.transi.flex.setting.enums.Language;

@Getter
@Setter
public class UserDTO {

	private Long id;

	private String firstName;

	private String lastName;

	private String email;

	private Language defaultLanguage;

	private String phone;

	private String username;

	@JsonProperty(access = Access.WRITE_ONLY)
	private String password;

	private LocalDateTime lastConnectionAt;

	private Boolean passwordReseted;

	private Set<RoleDTO> roles;

	private Set<ProfileDTO> profiles;

	private UserProfile profile;

	private CompanyDTO company;

	public boolean hasProfile(UserProfile profile) {
		return profiles.stream().anyMatch(p -> p.getName().equals(profile));
	}

	public void addProfile(UserProfile profile) {
		if (profiles.stream().anyMatch(p -> p.getName().equals(profile))) {
			return;
		}
		Profile model = SpringContext.getBean(ProfileRepository.class).findByName(profile);
		if (model == null) {
			return;
		}
		ProfileDTO dto = new ProfileDTO();
		dto.setName(profile);
		dto.setId(model.getId());
		profiles.add(dto);
	}
}