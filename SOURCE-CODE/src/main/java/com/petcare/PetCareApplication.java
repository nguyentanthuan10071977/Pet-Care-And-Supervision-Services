package com.petcare;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;

/**
 * Điểm khởi động ứng dụng PetCare Backend.
 * Ứng dụng triển khai các User Story trong tài liệu "User Story - Pet Care"
 * cho 3 actor: Pet Owner, Veterinarian, Manager.
 */
@SpringBootApplication
public class PetCareApplication {
    public static void main(String[] args) {
        SpringApplication.run(PetCareApplication.class, args);
    }
}
