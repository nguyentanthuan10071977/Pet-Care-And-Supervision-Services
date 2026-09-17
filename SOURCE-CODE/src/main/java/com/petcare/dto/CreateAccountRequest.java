package com.petcare.dto;

import com.petcare.entity.Role;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

/** Manager tạo tài khoản cho Veterinarian (hoặc Manager khác). */
@Data
public class CreateAccountRequest {
    @NotBlank
    private String phoneNumber;
    @NotBlank
    private String password;
    @NotBlank
    private String fullName;
    private String specialization;
    @NotNull
    private Role role;
}
