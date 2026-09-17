package com.petcare.dto;

import com.petcare.entity.Pet;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class PetResponse {
    private Long id;
    private Long ownerId;
    private String name;
    private String species;
    private String breed;
    private Integer age;
    private String photo1Url;
    private String photo2Url;
    private String photo3Url;
    private String photo4Url;
    private Boolean hasDevice;

    public static PetResponse from(Pet p) {
        return new PetResponse(p.getId(), p.getOwner().getId(), p.getName(), p.getSpecies(),
                p.getBreed(), p.getAge(), p.getPhoto1Url(), p.getPhoto2Url(), p.getPhoto3Url(),
                p.getPhoto4Url(), p.getDevice() != null);
    }
}
