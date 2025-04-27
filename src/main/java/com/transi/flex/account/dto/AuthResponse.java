package com.transi.flex.account.dto;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class AuthResponse {
	private UserDTO user;
	private String accessToken;
	private String refreshToken;
	private Long companyId;
	private String company;
}