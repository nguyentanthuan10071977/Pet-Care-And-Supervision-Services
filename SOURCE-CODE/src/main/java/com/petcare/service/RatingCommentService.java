package com.petcare.service;

import com.petcare.dto.CommentRequest;
import com.petcare.dto.CommentResponse;
import com.petcare.dto.RatingRequest;
import com.petcare.entity.Comment;
import com.petcare.entity.Rating;
import com.petcare.entity.Role;
import com.petcare.entity.User;
import com.petcare.exception.ApiException;
import com.petcare.repository.CommentRepository;
import com.petcare.repository.RatingRepository;
import com.petcare.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

/**
 * User Story: Rate on Veterinarian's profile, Comment on Veterinarian's profile
 * (chỉ được comment sau khi đã rate).
 */
@Service
@RequiredArgsConstructor
public class RatingCommentService {

    private final RatingRepository ratingRepository;
    private final CommentRepository commentRepository;
    private final UserRepository userRepository;

    @Transactional
    public double rate(Long veterinarianId, RatingRequest req) {
        User vet = getVeterinarian(veterinarianId);
        User ratedBy = userRepository.findById(req.getRatedById())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người đánh giá."));

        Rating rating = ratingRepository
                .findByVeterinarianIdAndRatedById(veterinarianId, req.getRatedById())
                .orElse(Rating.builder().veterinarian(vet).ratedBy(ratedBy).build());
        rating.setStars(req.getStars());
        ratingRepository.save(rating);

        return averageStars(veterinarianId);
    }

    public double averageStars(Long veterinarianId) {
        List<Rating> ratings = ratingRepository.findByVeterinarianId(veterinarianId);
        return ratings.stream().mapToInt(Rating::getStars).average().orElse(0.0);
    }

    /** Chỉ cho phép comment nếu người đó đã rate veterinarian này trước đó. */
    @Transactional
    public CommentResponse comment(Long veterinarianId, CommentRequest req) {
        User vet = getVeterinarian(veterinarianId);
        User author = userRepository.findById(req.getAuthorId())
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy người dùng."));

        boolean hasRated = ratingRepository
                .findByVeterinarianIdAndRatedById(veterinarianId, req.getAuthorId()).isPresent();
        if (!hasRated) {
            throw ApiException.badRequest("Bạn cần đánh giá (rate) bác sĩ trước khi bình luận.");
        }
        if (req.getContent() == null || req.getContent().isBlank()) {
            throw ApiException.badRequest("Nội dung bình luận không được để trống.");
        }

        Comment comment = Comment.builder()
                .veterinarian(vet)
                .author(author)
                .content(req.getContent())
                .build();
        commentRepository.save(comment);
        return CommentResponse.from(comment);
    }

    public List<CommentResponse> listComments(Long veterinarianId) {
        return commentRepository.findByVeterinarianIdOrderByCreatedAtDesc(veterinarianId).stream()
                .map(CommentResponse::from).toList();
    }

    private User getVeterinarian(Long id) {
        User vet = userRepository.findById(id)
                .orElseThrow(() -> ApiException.notFound("Không tìm thấy veterinarian."));
        if (vet.getRole() != Role.VETERINARIAN) {
            throw ApiException.badRequest("Người dùng này không phải là veterinarian.");
        }
        return vet;
    }
}
