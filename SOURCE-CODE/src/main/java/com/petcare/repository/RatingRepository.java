package com.petcare.repository;

import com.petcare.entity.Rating;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface RatingRepository extends JpaRepository<Rating, Long> {
    List<Rating> findByVeterinarianId(Long veterinarianId);
    Optional<Rating> findByVeterinarianIdAndRatedById(Long veterinarianId, Long ratedById);
}
