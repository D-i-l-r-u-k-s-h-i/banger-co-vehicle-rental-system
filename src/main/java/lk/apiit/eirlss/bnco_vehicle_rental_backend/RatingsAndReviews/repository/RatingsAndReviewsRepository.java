package lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.repository;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.entity.RatingsAndReviews;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public interface RatingsAndReviewsRepository extends JpaRepository<RatingsAndReviews,Long> {
    List<RatingsAndReviews> getRatingsAndReviewsByCustomerAndVehicle(Customer customer, Vehicle vehicle);

    List<RatingsAndReviews> getRatingsAndReviewsByVehicle(Vehicle vehicle);

    @Query("SELECT COUNT(DISTINCT c.customer) FROM RatingsAndReviews AS c WHERE c.vehicle=?1")
    Integer getNoOfRatingsForVehicle(Vehicle vehicle);

//    @Query("SELECT DISTINCT new RatingsAndReviews(c.customer,c.customerRating)  FROM RatingsAndReviews AS c WHERE c.vehicle=?1")
//    List<RatingsAndReviews> getCustomersWhoRatedVehicle(Vehicle vehicle);

//    List<RatingsAndReviews> findDistinctByCustomerAndVehicle(Vehicle vehicle);


    @Query(value="SELECT COUNT(cc.customer_id) AS tot FROM\n" +
            "(SELECT DISTINCT c.customer_id,c.customer_rating FROM ratings_reviews_vehicle AS c WHERE c.vehicle_id=?1) \n" +
            "AS cc WHERE cc.customer_rating=?2",nativeQuery = true)
    Integer getNoOfRatingsForStars(Vehicle vehicle,double star);
}
