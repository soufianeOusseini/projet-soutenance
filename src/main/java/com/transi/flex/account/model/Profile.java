package com.transi.flex.account.model;

import com.transi.flex.account.enums.UserProfile;
import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;
;
@Getter
@Setter
@Entity
@Table(name = "T_PROFILE")
@NoArgsConstructor
@AllArgsConstructor
public class Profile {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "ID")
	private Long id;

	@Column(name = "NAME", unique = true)
	@Enumerated(EnumType.STRING)
	private UserProfile name;
}
