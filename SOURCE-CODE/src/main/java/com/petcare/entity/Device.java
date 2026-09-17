package com.petcare.entity;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/** Thiết bị định vị GPS gắn cho một pet (User Story "Add Device" / "Locate pet"). */
@Entity
@Table(name = "devices")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Device {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false, unique = true)
    private String deviceCode;

    @OneToOne
    @JoinColumn(name = "pet_id", nullable = false, unique = true)
    private Pet pet;

    private Double latitude;

    private Double longitude;

    private LocalDateTime lastUpdated;
}
