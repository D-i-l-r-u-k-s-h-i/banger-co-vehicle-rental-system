package lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.service;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.Repository.CustomerRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.UserSession;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Auth.entity.Customer;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.DTO.RatingReviewDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.entity.RatingsAndReviews;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.repository.RatingsAndReviewsRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Util.Utils;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.Repository.VehicleRepository;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.Vehicle.entity.Vehicle;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;

import java.text.SimpleDateFormat;
import java.util.*;

@Service
public class RatingsAndReviewsService {
    @Autowired
    RatingsAndReviewsRepository ratingsAndReviewsRepository;

    @Autowired
    VehicleRepository vehicleRepository;

    @Autowired
    CustomerRepository customerRepository;

    public String saveRatingOrReview(RatingReviewDTO dto){
        String ret="";

        UserSession userSession = (UserSession) SecurityContextHolder.getContext().getAuthentication().getPrincipal();
        Customer customer= customerRepository.findByUserId(userSession.getId());
        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(dto.getVehicleId());

        //get previous ratings/reviews by customer
        List<RatingsAndReviews> ratingsAndReviewsList=ratingsAndReviewsRepository.getRatingsAndReviewsByCustomerAndVehicle(customer,vehicle);

        ModelMapper mapper=new ModelMapper();
        RatingsAndReviews ratingsAndReviews=mapper.map(dto,RatingsAndReviews.class);

        SimpleDateFormat sdf=new SimpleDateFormat("E, dd MMM yyyy HH:mm:ss");
        Date date=new Date();

        if(ratingsAndReviewsList.size()!=0){
            //updating existing rating
            for (RatingsAndReviews rateOrReview:ratingsAndReviewsList) {
                if(dto.getCustomerRating()!=0){ //&& rateOrReview.getCustomerRating()!=0
                    rateOrReview.setCustomerRating(dto.getCustomerRating());
                    ratingsAndReviewsRepository.save(rateOrReview);
                    ret="successful updation of rating";
                }
                if(dto.getCustomerReview()!=null && rateOrReview.getCustomerReview()==null){
                    rateOrReview.setTimestamp(sdf.format(date));
                    rateOrReview.setCustomerReview(dto.getCustomerReview());

                    ratingsAndReviewsRepository.save(rateOrReview);
                    ret="successful saving of review on rating";
                }
            }
            if(dto.getCustomerReview()!=null){

                ratingsAndReviews.setTimestamp(sdf.format(date));
                ratingsAndReviews.setCustomerRating(ratingsAndReviewsList.get(0).getCustomerRating());
                ratingsAndReviews.setCustomer(customer);
                ratingsAndReviews.setVehicle(vehicle);

                ratingsAndReviewsRepository.save(ratingsAndReviews);
                ret="successful saving of review";
            }

        }//for the first time rates and reviews
        else if(dto.getCustomerReview()!=null || dto.getCustomerRating()!=0){

            ratingsAndReviews.setTimestamp(sdf.format(date));
            ratingsAndReviews.setCustomer(customer);
            ratingsAndReviews.setVehicle(vehicle);

            ratingsAndReviewsRepository.save(ratingsAndReviews);
            ret="successful saving of rating or review";
        }

        return ret;
    }

    public List<RatingReviewDTO> getRatingsAndReviewsForVehicle(long vehicle_id){

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(vehicle_id);
        List<RatingsAndReviews> ratingsAndReviewsList=ratingsAndReviewsRepository.getRatingsAndReviewsByVehicle(vehicle);

        List<RatingReviewDTO> dtoList= Utils.mapAll(ratingsAndReviewsList,RatingReviewDTO.class);

        return dtoList;
    }

    public List<RatingReviewDTO> getReviewsForVehicle(long vehicle_id){
        List<RatingReviewDTO> dtoReviewList=new ArrayList<>();

        List<RatingReviewDTO> dtoList=getRatingsAndReviewsForVehicle(vehicle_id);
        for (RatingReviewDTO ratingOrReview:dtoList) {
            if(ratingOrReview.getCustomerReview()!=null){
                dtoReviewList.add(ratingOrReview);
            }
        }

        return dtoReviewList;
    }

    public int getRatingsForVehicle(Vehicle vehicle, int starValue){
        int star2rating=ratingsAndReviewsRepository.getNoOfRatingsForStars(vehicle,starValue);
        return star2rating;
    }

    public double calculateOverallRating(long vehicle_id){

        Vehicle vehicle=vehicleRepository.findVehicleByVehicleId(vehicle_id);
        int totalRatings=ratingsAndReviewsRepository.getNoOfRatingsForVehicle(vehicle);

        //calculating the weighted mean
        int count1stars=getRatingsForVehicle(vehicle,1);
        int count2stars=getRatingsForVehicle(vehicle,2);
        int count3stars=getRatingsForVehicle(vehicle,3);
        int count4stars=getRatingsForVehicle(vehicle,4);
        int count5stars=getRatingsForVehicle(vehicle,5);

        double weighted_mean=(count1stars+2*count2stars+3*count3stars+4*count4stars+5*count5stars)/totalRatings;

        return weighted_mean;
    }

}
