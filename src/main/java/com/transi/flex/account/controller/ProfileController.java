package com.transi.flex.account.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;
import lombok.RequiredArgsConstructor;
import com.transi.flex.account.dto.ProfileUpdateDTO;
import com.transi.flex.account.dto.UserDTO;
import com.transi.flex.account.service.ProfileService;

@RestController
@RequestMapping("/profile")
@RequiredArgsConstructor
public class ProfileController {

    private final ProfileService profileService;

    @GetMapping
    public ResponseEntity<UserDTO> getCurrentUserProfile(Authentication authentication) {
        UserDTO userDTO = profileService.getUserProfile(authentication.getName());
        return ResponseEntity.ok(userDTO);
    }

    @PutMapping
    public ResponseEntity<UserDTO> updateProfile(@RequestBody ProfileUpdateDTO profileUpdateDTO,
                                                 Authentication authentication) {
        UserDTO updatedUser = profileService.updateUserProfile(authentication.getName(), profileUpdateDTO);
        return ResponseEntity.ok(updatedUser);
    }
}