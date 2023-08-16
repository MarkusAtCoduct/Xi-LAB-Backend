package com.codeleap.xilab.api.repository;

import com.codeleap.xilab.api.models.entities.Rating;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

@Repository
public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> getByMethodId(Long methodId);

    @Transactional
    void deleteByMethodId(Long methodId);
}
