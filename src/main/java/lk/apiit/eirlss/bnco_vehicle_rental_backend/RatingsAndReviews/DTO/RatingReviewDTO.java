package lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.DTO;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class RatingReviewDTO {
    private long ratingId;

    private double customerRating;

    private String customerReview;

    private String timestamp;

    private long customerId;

    private String customerUserName;

    private long vehicleId;

}
