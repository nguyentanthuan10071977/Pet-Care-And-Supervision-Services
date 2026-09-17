package com.petcare.dto;

import com.petcare.entity.Role;
import com.petcare.entity.User;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class UserResponse {
    private Long id;
    private String phoneNumber;
    private String fullName;
    private String email;
    private String avatarUrl;
    private String address;
    private String specialization;
    private Role role;
    private Boolean active;

    public static UserResponse from(User u) {
        return new UserResponse(u.getId(), u.getPhoneNumber(), u.getFullName(), u.getEmail(),
                u.getAvatarUrl(), u.getAddress(), u.getSpecialization(), u.getRole(), u.getActive());
    }
}
