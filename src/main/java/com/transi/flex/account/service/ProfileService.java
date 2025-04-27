package com.transi.flex.account.service;

import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.ProfileUpdateDTO;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.mapper.UserMapper;
import com.transi.flex.account.model.User;
import com.transi.flex.account.repository.UserRepository;
import com.transi.flex.common.exception.ResourceNotFoundException;

@Service
@RequiredArgsConstructor
public class ProfileService {

    private final UserRepository userRepository;
    private final UserMapper userMapper;

    public UserDTO getUserProfile(String username) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        return userMapper.toDto(user);
    }

    @Transactional
    public UserDTO updateUserProfile(String username, ProfileUpdateDTO profileUpdateDTO) {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with username: " + username));

        if (profileUpdateDTO.getFirstName() != null) {
            user.setFirstName(profileUpdateDTO.getFirstName());
        }

        if (profileUpdateDTO.getLastName() != null) {
            user.setLastName(profileUpdateDTO.getLastName());
        }

        if (profileUpdateDTO.getEmail() != null) {
            user.setEmail(profileUpdateDTO.getEmail());
        }

        if (profileUpdateDTO.getPhone() != null) {
            user.setPhone(profileUpdateDTO.getPhone());
        }

        if (profileUpdateDTO.getDefaultLanguage() != null) {
            user.setDefaultLanguage(profileUpdateDTO.getDefaultLanguage());
        }

        User updatedUser = userRepository.save(user);

        return userMapper.toDto(updatedUser);
    }
}