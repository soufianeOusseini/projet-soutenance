package com.transi.flex.account.mapper;

import java.util.List;
import java.util.Set;

import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.model.Profile;
import org.mapstruct.Builder;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.ReportingPolicy;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.dto.UserSummary;
import com.transi.flex.account.model.User;
import com.transi.flex.company.mapper.CompanyMapper;

@Mapper(componentModel = "spring",
		unmappedTargetPolicy = ReportingPolicy.IGNORE,
		builder = @Builder(disableBuilder = true),
		uses = {CompanyMapper.class, MapperUtils.class}
)
public interface UserMapper {
	@Mapping(target = "company", source = "company")
	User toModel(UserDTO dto);

	@Mapping(target = "company", source = "company")
	@Mapping(target = "profile", expression = "java(getFirstProfile(user.getProfiles()))")
	UserDTO toDto(User user);

	UserSummary toSummary(User user);

	List<UserDTO> toDtos(List<User> users);


	default UserProfile getFirstProfile(Set<Profile> profiles) {
		if (profiles == null || profiles.isEmpty()) {
			return null;
		}
		return profiles.iterator().next().getName();
	}
}