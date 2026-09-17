package com.petcare.service;

import com.petcare.dto.*;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.UserRepository;
import com.petcare.util.ValidationUtils;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.concurrent.ThreadLocalRandom;

/**
 * User Story "Login" và "Register".
 * Đăng ký: nhập SĐT + mật khẩu + xác nhận mật khẩu -> sinh OTP -> verify OTP.
 * Đăng nhập: SĐT + mật khẩu khớp với tài khoản đã đăng ký (đã verify OTP).
 */
@Service
@RequiredArgsConstructor
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Transactional
    public UserResponse register(RegisterRequest req) {
        ValidationUtils.validatePhoneNumber(req.getPhoneNumber());
        ValidationUtils.validatePassword(req.getPassword());

        if (!req.getPassword().equals(req.getConfirmPassword())) {
            throw ApiException.badRequest("Mật khẩu xác nhận không khớp.");
        }
        if (userRepository.existsByPhoneNumber(req.getPhoneNumber())) {
            throw ApiException.conflict("Số điện thoại đã được đăng ký.");
        }

        String otp = generateOtp();
        User user = User.builder()
                .phoneNumber(req.getPhoneNumber())
                .password(passwordEncoder.encode(req.getPassword()))
                .fullName(req.getFullName())
                .role(Role.PET_OWNER)
                .otpVerified(false)
                .otpCode(otp)
                .otpExpiresAt(LocalDateTime.now().plusMinutes(5))
                .build();
        userRepository.save(user);

        // Trong thực tế OTP sẽ được gửi qua SMS; ở đây log ra để demo/testing.
        System.out.println("[OTP] Gửi OTP " + otp + " tới số " + req.getPhoneNumber());

        return UserResponse.from(user);
    }

    @Transactional
    public UserResponse verifyOtp(VerifyOtpRequest req) {
        User user = userRepository.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy tài khoản."));

        if (user.getOtpVerified()) {
            throw ApiException.badRequest("Tài khoản đã được xác thực.");
        }
        if (user.getOtpExpiresAt().isBefore(LocalDateTime.now())) {
            throw ApiException.badRequest("OTP đã hết hạn, vui lòng đăng ký lại.");
        }
        if (!user.getOtpCode().equals(req.getOtpCode())) {
            throw ApiException.badRequest("OTP không đúng.");
        }

        user.setOtpVerified(true);
        user.setOtpCode(null);
        userRepository.save(user);
        return UserResponse.from(user);
    }

    public UserResponse login(LoginRequest req) {
        User user = userRepository.findByPhoneNumber(req.getPhoneNumber())
                .orElseThrow(() -> ApiException.unauthorized("Số điện thoại hoặc mật khẩu không đúng."));

        if (!passwordEncoder.matches(req.getPassword(), user.getPassword())) {
            throw ApiException.unauthorized("Số điện thoại hoặc mật khẩu không đúng.");
        }
        if (user.getRole() == Role.PET_OWNER && !user.getOtpVerified()) {
            throw ApiException.forbidden("Tài khoản chưa xác thực OTP.");
        }
        if (!user.getActive()) {
            throw ApiException.forbidden("Tài khoản đã bị khoá.");
        }
        return UserResponse.from(user);
    }

    private String generateOtp() {
        return String.valueOf(ThreadLocalRandom.current().nextInt(100000, 999999));
    }
}
