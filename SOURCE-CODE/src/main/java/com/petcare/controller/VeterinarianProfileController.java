package com.petcare.controller;

import com.petcare.dto.CommentRequest;
import com.petcare.dto.CommentResponse;
import com.petcare.dto.RatingRequest;
import com.petcare.service.RatingCommentService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

/** User Story: Rate on Veterinarian's profile / Comment on Veterinarian's profile. */
@RestController
@RequestMapping("/api/veterinarians/{vetId}")
@RequiredArgsConstructor
public class VeterinarianProfileController {

    private final RatingCommentService ratingCommentService;

    @PostMapping("/ratings")
    public Map<String, Object> rate(@PathVariable Long vetId, @Valid @RequestBody RatingRequest req) {
        double avg = ratingCommentService.rate(vetId, req);
        return Map.of("veterinarianId", vetId, "averageStars", avg);
    }

    @GetMapping("/ratings")
    public Map<String, Object> averageRating(@PathVariable Long vetId) {
        return Map.of("veterinarianId", vetId, "averageStars", ratingCommentService.averageStars(vetId));
    }

    @PostMapping("/comments")
    public CommentResponse comment(@PathVariable Long vetId, @Valid @RequestBody CommentRequest req) {
        return ratingCommentService.comment(vetId, req);
    }

    @GetMapping("/comments")
    public List<CommentResponse> listComments(@PathVariable Long vetId) {
        return ratingCommentService.listComments(vetId);
    }
}
