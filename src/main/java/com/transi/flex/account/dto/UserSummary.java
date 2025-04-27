package com.transi.flex.account.dto;

import org.apache.commons.lang3.StringUtils;

import lombok.Getter;
import lombok.Setter;
import com.transi.flex.setting.enums.Language;

@Getter
@Setter
public class UserSummary {

	private Long id;

	private String email;

	private String firstName;

	private String lastName;

	private Language defaultLanguage;

	private String phone;

	public String getFullName() {
		return StringUtils.defaultIfBlank(firstName, "") + " " + StringUtils.defaultIfBlank(lastName, "");
	}

	public String getDefaultLang() {
		return defaultLanguage == null ? Language.FR.name().toLowerCase() : defaultLanguage.name().toLowerCase();
	}
}