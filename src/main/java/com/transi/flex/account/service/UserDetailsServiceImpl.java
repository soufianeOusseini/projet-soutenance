package com.transi.flex.account.service;

import java.util.Optional;

import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.AuthUser;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;

@Component
@Slf4j
@RequiredArgsConstructor
public class UserDetailsServiceImpl implements UserDetailsService {

	private final UserRepository repository;
	private final UserMapper mapper;

	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		Optional<User> user = repository.findByUsername(username);
		if (user.isEmpty()) {
			log.error("Username not found: " + username);
			throw new UsernameNotFoundException("could not found user..!!");
		}
		log.info("User Authenticated Successfully..!!!");
		return new AuthUser(user.get());
	}

	public UserDTO findByUsername(String username) {
		Optional<User> user = repository.findByUsername(username);
		return mapper.toDto(user.orElse(null));
	}
}