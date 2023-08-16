package com.codeleap.xilab.api.repository;

import com.codeleap.xilab.api.models.entities.auth.UserAvatar;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public interface UserAvatarRepository extends JpaRepository<UserAvatar, UUID> {
    Optional<UserAvatar> findByUserId(Long userId);
    void deleteByUserId(Long userId);
}
