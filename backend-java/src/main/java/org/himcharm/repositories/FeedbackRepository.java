package org.himcharm.repositories;

import org.himcharm.entities.Feedback;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

@Repository
public interface FeedbackRepository extends JpaRepository<Feedback, Long> {

    @Query("SELECT COALESCE(AVG(feedback.rating), 0.0) FROM Feedback feedback")
    double getAverageRating();
}
