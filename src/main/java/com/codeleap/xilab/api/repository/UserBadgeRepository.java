package com.codeleap.xilab.api.repository;

import com.codeleap.xilab.api.models.entities.UserBadge;
import com.codeleap.xilab.api.models.entities.auth.User;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface UserBadgeRepository extends JpaRepository<UserBadge, UUID> {

    List<UserBadge> getByBelongToUser(User user);
}
