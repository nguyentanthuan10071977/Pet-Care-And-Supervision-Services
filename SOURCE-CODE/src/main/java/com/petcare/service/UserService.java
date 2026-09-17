package com.petcare.service;

import com.petcare.dto.CreateAccountRequest;
import com.petcare.dto.UpdateProfileRequest;
import com.petcare.dto.UserResponse;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.UserRepository;
import com.petcare.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Story: View/Edit profile (mọi actor), View list Veterinarian,
 * Search Veterinarian or pets, và quản lý tài khoản bởi Manager.
 */
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public UserResponse getProfile(Long userId) {
        return UserResponse.from(findUser(userId));
    }

    @Transactional
    public UserResponse updateProfile(Long userId, UpdateProfileRequest req) {
        User user = findUser(userId);

        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw ApiException.badRequest("Không được để trống họ tên.");
        }
        user.setFullName(req.getFullName());
        user.setEmail(req.getEmail());
        user.setAvatarUrl(req.getAvatarUrl());
        user.setAddress(req.getAddress());
        if (user.getRole() == Role.VETERINARIAN) {
            user.setSpecialization(req.getSpecialization());
        }
        userRepository.save(user);
        return UserResponse.from(user);
    }

    public List<UserResponse> listVeterinarians() {
        return userRepository.findByRole(Role.VETERINARIAN).stream()
                .map(UserResponse::from).toList();
    }

    public List<UserResponse> listPetOwners() {
        return userRepository.findByRole(Role.PET_OWNER).stream()
                .map(UserResponse::from).toList();
    }

    /** User Story "Search Veterinarian or pets": tìm theo tên veterinarian (>=1 ký tự). */
    public List<UserResponse> searchVeterinarians(String keyword) {
        if (keyword == null || keyword.isBlank()) {
            throw ApiException.badRequest("Vui lòng nhập ít nhất 1 ký tự để tìm kiếm.");
        }
        return userRepository.findByRoleAndFullNameContainingIgnoreCase(Role.VETERINARIAN, keyword)
                .stream().map(UserResponse::from).toList();
    }

    // ----- Manager: quản lý tài khoản (Manage/Create/Edit/Delete account) -----

    @Transactional
    public UserResponse createAccount(CreateAccountRequest req) {
        ValidationUtils.validatePhoneNumber(req.getPhoneNumber());
        ValidationUtils.validatePassword(req.getPassword());
        if (req.getFullName() == null || req.getFullName().isBlank()) {
            throw ApiException.badRequest("Không được để trống họ tên.");
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw ApiException.conflict("Số điện thoại đã tồn tại.");
        }
        User user = User.builder()
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .specialization(req.getSpecialization())
                .role(req.getRole())
                .otpVerified(true) // tài khoản do manager tạo, không cần OTP
                .build();
        userRepository.save(user);
        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse editAccount(Long accountId, UpdateProfileRequest req) {
        return updateProfile(accountId, req);
    }

    @Transactional
    public void deleteAccount(Long accountId) {
        User user = findUser(accountId);
        userRepository.delete(user);
    }

    public List<UserResponse> listAllAccounts() {
        return userRepository.findAll().stream().map(UserResponse::from).toList();
    }

    private User findUser(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người dùng id=" + userId));
    }
}
