package com.transi.flex.company.repository;

import com.transi.flex.company.model.Company;
import org.springframework.data.jpa.repository.JpaRepository;

public interface CompanyRepository  extends JpaRepository<Company, Long> {
}
