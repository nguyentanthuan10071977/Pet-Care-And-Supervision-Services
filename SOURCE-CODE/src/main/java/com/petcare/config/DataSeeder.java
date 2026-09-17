package com.petcare.config;

import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

/**
 * Tạo sẵn vài tài khoản mẫu khi chạy ứng dụng lần đầu, để dễ test API ngay
 * (không cần đăng ký/verify OTP trước). Mật khẩu demo: Password123
 */
@Component
@RequiredArgsConstructor
public class DataSeeder implements CommandLineRunner {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    @Override
    public void run(String... args) {
        if (userRepository.count() > 0) {
            return;
        }
        String pwd = passwordEncoder.encode("Password123");

        userRepository.save(User.builder()
                .phoneNumber("0900000001").password(pwd).fullName("Nguyễn Văn Chủ (Pet Owner)")
                .role(Role.PET_OWNER).otpVerified(true).build());

        userRepository.save(User.builder()
                .phoneNumber("0900000002").password(pwd).fullName("Bs. Trần Thị Thú Y")
                .specialization("Thú y tổng quát").role(Role.VETERINARIAN).otpVerified(true).build());

        userRepository.save(User.builder()
                .phoneNumber("0900000003").password(pwd).fullName("Lê Văn Quản Lý")
                .role(Role.MANAGER).otpVerified(true).build());

        System.out.println("[Seed] Đã tạo 3 tài khoản mẫu (mật khẩu: Password123): " +
                "0900000001 (Pet Owner), 0900000002 (Veterinarian), 0900000003 (Manager)");
    }
}
