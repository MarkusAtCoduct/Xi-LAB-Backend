package com.codeleap.xilab.api.repository;

import com.codeleap.xilab.api.models.entities.MethodSet;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

@Repository
public interface MethodSetRepository extends JpaRepository<MethodSet, Long> {
    @Transactional
    void deleteBySetId(Long setId);
}
