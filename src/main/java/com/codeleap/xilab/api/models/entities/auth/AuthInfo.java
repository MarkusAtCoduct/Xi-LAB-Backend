package com.codeleap.xilab.api.models.entities.auth;


import java.time.LocalDateTime;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.Index;
import javax.persistence.JoinColumn;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.persistence.UniqueConstraint;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.Size;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString;

@Data
@ToString(exclude = {"user"})
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Entity
@Table(name = "auth", uniqueConstraints = { @UniqueConstraint(columnNames = "username") },
		indexes = @Index(columnList = "username"))
public class AuthInfo {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@NotBlank
	@Size(max = 80)
	private String username;

	@NotBlank
	@Size(max = 120)
	private String password;

	private String resetPasswordToken;

	private LocalDateTime resetPasswordOn;

	private Boolean useSocialAccount;

	private String socialToken;

	@OneToOne
	@JoinColumn(name = "user_id")
	private User user;

}
