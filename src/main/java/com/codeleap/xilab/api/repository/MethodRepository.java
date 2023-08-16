package com.codeleap.xilab.api.repository;


import com.codeleap.xilab.api.models.entities.Method;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface MethodRepository extends JpaRepository<Method, Long> {
}
