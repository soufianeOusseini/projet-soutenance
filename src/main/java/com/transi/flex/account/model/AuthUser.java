// Classe AuthUser
package com.transi.flex.account.model;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;

import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

public class AuthUser extends User implements UserDetails {

	private final User user;

	private static final long serialVersionUID = 1L;

	Collection<? extends GrantedAuthority> authorities;

	public AuthUser(User user) {
		this.user = user;
		List<GrantedAuthority> auths = new ArrayList<>();
		user.getRoles().forEach(role -> {
			auths.add(new SimpleGrantedAuthority(role.getName().toUpperCase()));
			role.getPermissions()
					.forEach(permission -> auths.add(new SimpleGrantedAuthority(permission.getName().toUpperCase())));
		});
		this.authorities = auths;
	}

	public User getUser() {
		return this.user;
	}

	@Override
	public Collection<? extends GrantedAuthority> getAuthorities() {
		return authorities;
	}

	@Override
	public String getPassword() {
		return user.getPassword();
	}

	@Override
	public String getUsername() {
		return user.getUsername();
	}

	@Override
	public boolean isAccountNonExpired() {
		return true;
	}

	@Override
	public boolean isAccountNonLocked() {
		return true;
	}

	@Override
	public boolean isCredentialsNonExpired() {
		return true;
	}

	@Override
	public boolean isEnabled() {
		return true;
	}
}