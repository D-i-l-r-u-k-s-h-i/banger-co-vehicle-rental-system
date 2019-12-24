package lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.entity.RatingsAndReviews;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface RatingsAndReviewsRepository extends JpaRepository<RatingsAndReviews,Long> {
}
