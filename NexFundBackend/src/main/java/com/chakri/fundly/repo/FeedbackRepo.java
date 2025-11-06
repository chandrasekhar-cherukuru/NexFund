package com.chakri.fundly.repo;

import com.chakri.fundly.model.Feedback;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;
import java.util.List;

@Repository
public interface FeedbackRepo extends JpaRepository<Feedback, Long> {
    List<Feedback> findByIsApprovedTrue();

    // ✅ NEW - Get paginated approved feedbacks
    Page<Feedback> findByIsApprovedTrue(Pageable pageable);
}
