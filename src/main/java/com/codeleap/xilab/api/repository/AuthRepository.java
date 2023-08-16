package com.codeleap.xilab.api.repository;


import com.codeleap.xilab.api.models.entities.auth.AuthInfo;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface AuthRepository extends JpaRepository<AuthInfo, Long> {

	Boolean existsByUsername(String username);

	Optional<AuthInfo> findByResetPasswordToken(String token);

	Optional<AuthInfo> findByUsername(String username);

}
