package com.chakri.fundly.service;

import com.chakri.fundly.model.Feedback;
import com.chakri.fundly.repo.FeedbackRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;
import java.util.List;

@Service
public class FeedbackService {

    @Autowired
    private FeedbackRepo repo;

    public Feedback saveFeedback(Feedback feedback) {
        Feedback saved = repo.save(feedback);
        System.out.println("✅ Feedback received from " + saved.getName() +
                " | Type: " + saved.getType() +
                " | Rating: " + saved.getRating());
        return saved;
    }

    public List<Feedback> getApprovedFeedback() {
        List<Feedback> feedbacks = repo.findByIsApprovedTrue();
        System.out.println("✅ Retrieved " + feedbacks.size() + " approved feedbacks");
        return feedbacks;
    }

    public List<Feedback> getLastThreeApprovedFeedback() {
        Pageable pageable = PageRequest.of(0, 3, Sort.by(Sort.Direction.DESC, "createdAt"));
        List<Feedback> feedbacks = repo.findByIsApprovedTrue(pageable).getContent();
        System.out.println("✅ Retrieved last 3 approved feedbacks");
        return feedbacks;
    }

    public List<Feedback> getAllFeedback() {
        return repo.findAll();
    }

    public void approveFeedback(Long id) {
        repo.findById(id).ifPresent(feedback -> {
            feedback.setIsApproved(true);
            repo.save(feedback);
            System.out.println("✅ Feedback " + id + " approved");
        });
    }

    public void rejectFeedback(Long id) {
        repo.findById(id).ifPresent(feedback -> {
            repo.deleteById(id);
            System.out.println("✅ Feedback " + id + " rejected");
        });
    }
}
