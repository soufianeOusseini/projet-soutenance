package com.transi.flex.account.repository;

import com.transi.flex.account.model.Role;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(String string);

    List<Role> findByCompanyId(Long currentId);

    @Query("SELECT r FROM Role r WHERE r.company.id = :companyId OR r.company.id IS NULL")
    List<Role> findAllByCompanyOrSystem(@Param("companyId") Long companyId);

}
