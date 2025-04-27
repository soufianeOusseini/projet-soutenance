package com.transi.flex.account.repository;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transi.flex.account.enums.UserProfile;
import com.transi.flex.account.model.Profile;

public interface ProfileRepository extends JpaRepository<Profile, Long> {

	Profile findByName(UserProfile profile);

}