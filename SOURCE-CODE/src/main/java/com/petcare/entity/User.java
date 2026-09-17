package com.petcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

/**
 * Đại diện cho cả 3 actor: Pet Owner, Veterinarian, Manager.
 * Dùng single-table với cột "role" để phân biệt (đúng với UML: 3 actor kế thừa
 * hành vi đăng nhập / xem-sửa hồ sơ chung).
 */
@Entity
@Table(name = "users")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true, length = 11)
    private String phoneNumber;

    @Column(nullable = false)
    private String password; // đã hash BCrypt

    @Column(nullable = false)
    private String fullName;

    private String email;

    private String avatarUrl;

    private String address;

    /** Chỉ dùng cho Veterinarian. */
    private String specialization;

    @Enumerated(EnumType.STRING)
    @Column(nullable = false)
    private Role role;

    @Column(nullable = false)
    @Builder.Default
    private Boolean active = true;

    /** Trạng thái xác thực OTP khi đăng ký (chỉ Pet Owner). */
    @Builder.Default
    private Boolean otpVerified = false;

    private String otpCode;

    private LocalDateTime otpExpiresAt;

    @Column(nullable = false)
    @Builder.Default
    private LocalDateTime createdAt = LocalDateTime.now();

    @OneToMany(mappedBy = "owner", cascade = CascadeType.ALL, orphanRemoval = true)
    @Builder.Default
    private List<Pet> pets = new ArrayList<>();
}
