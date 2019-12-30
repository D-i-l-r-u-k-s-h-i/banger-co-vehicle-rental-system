package lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.controller;

import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.DTO.RatingReviewDTO;
import lk.apiit.eirlss.bnco_vehicle_rental_backend.RatingsAndReviews.service.RatingsAndReviewsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.*;

@RequestMapping(path = "/reviews")
@Controller
public class RatingsReviewsController {

    @Autowired
    RatingsAndReviewsService ratingsAndReviewsService;

    @RequestMapping(value = "/save", method = RequestMethod.POST, consumes = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> addVehicle(@RequestHeader(value = "Authorization") String token, @RequestBody RatingReviewDTO ratingReviewDTO){
        return ResponseEntity.ok(ratingsAndReviewsService.saveRatingOrReview(ratingReviewDTO));
    }

    @RequestMapping(value = "/get/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getReviewsVehicle(@PathVariable(name = "id") long id, @RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok(ratingsAndReviewsService.getReviewsForVehicle(id));
    }

    @RequestMapping(value = "/ratings/{id}", method = RequestMethod.GET)
    public ResponseEntity<?> getVehicleRating(@PathVariable(name = "id") long id, @RequestHeader(value = "Authorization") String token){
        return ResponseEntity.ok(ratingsAndReviewsService.calculateOverallRating(id));
    }

}
