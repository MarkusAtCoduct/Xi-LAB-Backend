package com.codeleap.xilab.api.repository;

import com.codeleap.xilab.api.models.entities.auth.ERole;
import com.codeleap.xilab.api.models.entities.auth.Role;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface RoleRepository extends JpaRepository<Role, Long> {

	Optional<Role> findByName(ERole name);
}
