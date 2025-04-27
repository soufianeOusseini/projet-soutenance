package com.transi.flex.account.dto;

import java.util.Set;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RoleDTO {

	private Long id;

	private String name;

	private Set<PermissionDTO> permissions;
}