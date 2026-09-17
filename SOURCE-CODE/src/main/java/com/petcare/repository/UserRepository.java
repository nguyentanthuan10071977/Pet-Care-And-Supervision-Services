package com.petcare.repository;

import com.petcare.entity.Role;
import com.petcare.entity.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
    Optional<User> findByPhoneNumber(String phoneNumber);
    boolean existsByPhoneNumber(String phoneNumber);
    List<User> findByRole(Role role);
    List<User> findByRoleAndFullNameContainingIgnoreCase(Role role, String keyword);
}
