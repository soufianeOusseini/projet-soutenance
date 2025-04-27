// Classe Role
package com.transi.flex.account.model;

import java.util.ArrayList;
import java.util.Collection;

import com.transi.flex.company.model.Company;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.JoinTable;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import lombok.Getter;
import lombok.Setter;


@Getter
@Setter
@Entity
@Table(name = "T_ROLE")
public class Role {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", unique = true)
	private String name;

	@ManyToOne
	@JoinColumn(name = "COMPANY_ID", updatable = false)
	private Company company;

	@ManyToMany(fetch = FetchType.EAGER)
	@JoinTable(name = "ROLE_PERMISSION", joinColumns = { @JoinColumn(name = "ROLE_ID") }, inverseJoinColumns = {
			@JoinColumn(name = "PERMISSION_ID") })
	private Collection<Permission> permissions = new ArrayList<>();
}