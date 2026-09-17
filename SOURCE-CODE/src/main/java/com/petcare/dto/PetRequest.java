package com.petcare.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class PetRequest {
    @NotNull
    private Long ownerId;
    @NotBlank
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String photo1Url;
    private String photo2Url;
    private String photo3Url;
    private String photo4Url;
}
