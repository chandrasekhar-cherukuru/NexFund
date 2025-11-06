package com.chakri.fundly.controller;

import com.chakri.fundly.model.Feedback;
import com.chakri.fundly.service.FeedbackService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Map;

@RestController
@CrossOrigin(origins = "http://localhost:3000")
public class FeedbackController {

    @Autowired
    private FeedbackService service;

    @PostMapping("/feedback")
    public ResponseEntity<?> submitFeedback(@RequestBody Feedback feedback) {
        try {
            if (feedback.getName() == null || feedback.getName().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Name is required"));
            }
            if (feedback.getMessage() == null || feedback.getMessage().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Message is required"));
            }
            if (feedback.getRating() < 1 || feedback.getRating() > 5) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Rating must be between 1 and 5"));
            }
            if (feedback.getType() == null || feedback.getType().isEmpty()) {
                return ResponseEntity.badRequest()
                        .body(Map.of("error", "Type is required"));
            }

            // ✅ AUTO-APPROVE: Set isApproved to true
            feedback.setIsApproved(true);
            feedback.setCreatedAt(LocalDateTime.now());

            Feedback saved = service.saveFeedback(feedback);

            System.out.println("✅ Feedback APPROVED from " + saved.getName() +
                    " | Type: " + saved.getType() +
                    " | Rating: " + saved.getRating() +
                    " | Approved: " + saved.getIsApproved());

            return ResponseEntity.ok(Map.of(
                    "message", "Feedback submitted successfully!",
                    "feedbackId", saved.getId()
            ));
        } catch (Exception e) {
            System.out.println("❌ Error submitting feedback: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    @GetMapping("/feedback/approved")
    public ResponseEntity<?> getApprovedFeedback() {
        try {
            List<Feedback> feedbacks = service.getApprovedFeedback();
            System.out.println("✅ Returning " + feedbacks.size() + " approved feedbacks");
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            System.out.println("❌ Error fetching approved feedbacks: " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Get all feedbacks (for admin/debugging)
    @GetMapping("/feedback/all")
    public ResponseEntity<?> getAllFeedbacks() {
        try {
            List<Feedback> feedbacks = service.getAllFeedback();
            System.out.println("✅ Retrieved " + feedbacks.size() + " total feedbacks");
            feedbacks.forEach(f -> System.out.println(
                    "  📝 Name: " + f.getName() +
                            " | Type: " + f.getType() +
                            " | Rating: " + f.getRating() +
                            " | Approved: " + f.getIsApproved()
            ));
            return ResponseEntity.ok(feedbacks);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }

    // ✅ NEW: Admin endpoint to approve feedback by ID
    @PutMapping("/feedback/{id}/approve")
    public ResponseEntity<?> approveFeedback(@PathVariable Long id) {
        try {
            service.approveFeedback(id);
            System.out.println("✅ Feedback " + id + " approved");
            return ResponseEntity.ok(Map.of("message", "Feedback approved successfully"));
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body(Map.of("error", e.getMessage()));
        }
    }
}
