// UserRepository
package com.transi.flex.account.repository;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import com.transi.flex.account.model.User;

public interface UserRepository extends JpaRepository<User, Long> {

	Optional<User> findByUsername(String username);

	Optional<User> findByPasswordResetCode(Integer code);

	List<User> findByCompanyId(Long companyId);

	List<User> findByCompanyIdIsNull();

	User findByFirstName(String firstName);

	User findByLastName(String lastName);

	Optional<User> findByEmail(String email);

}